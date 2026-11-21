package com.yonwiplugins.skillranktooltip;

import com.google.inject.Provides;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Skill Ranks",
	description = "Shows your hiscore rank in skill tooltips",
	tags = {"skills", "rank", "hiscore", "tooltip"}
)
@Slf4j
public class SkillRankTooltipPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "skill-rank-tooltip";
	private static final int BACKGROUND_CHILD_INDEX = 0;
	private static final int BORDER_CHILD_INDEX = 1;
	private static final int LABEL_CHILD_INDEX = 2;
	private static final int VALUE_CHILD_INDEX = 3;
	private static final int TOOLTIP_ROW_HEIGHT = 13;
	private static final String RANK_LABEL = "Rank:";
	private static final String SHOW_ALL_SKILLS_KEY = "showAllSkills";
	private static final String SHOW_ALL_99S_KEY = "showAll99s";
	private static final String AUTO_TICKED_BY_ALL_SKILLS_KEY = "autoTickedByAllSkills";
	private static final String AUTO_TICKED_BY_ALL_99S_KEY = "autoTickedByAll99s";
	// Skill.OVERALL is deprecated and has no checkbox; Total level is separate.
	static final Skill[] SKILLS = java.util.Arrays.stream(Skill.values())
		.filter(skill -> !"Overall".equals(skill.getName()))
		.toArray(Skill[]::new);

	// Skills the plugin is unticking itself, so their ConfigChanged events
	// aren't mistaken for the player unticking them.
	private final Set<Skill> pendingUnticks = ConcurrentHashMap.newKeySet();

	@Inject
	private Client client;

	@Inject
	private HiscoreClient hiscoreClient;

	@Inject
	private SkillRankTooltipConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientThread clientThread;

	private volatile HiscoreResult hiscoreResult;
	private volatile String lookupUsername;
	private volatile HiscoreEndpoint lookupEndpoint;

	@Override
	protected void startUp()
	{
		resetLookup();
		requestHiscores();
	}

	@Override
	protected void shutDown()
	{
		resetLookup();
	}

	@Provides
	SkillRankTooltipConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SkillRankTooltipConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			requestHiscores();
		}
		else if (event.getGameState() == GameState.LOGIN_SCREEN
			|| event.getGameState() == GameState.HOPPING)
		{
			resetLookup();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() == GameState.LOGGED_IN
			&& lookupUsername == null)
		{
			requestHiscores();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN || lookupUsername == null)
		{
			return;
		}

		HiscoreEndpoint worldEndpoint = HiscoreEndpoint.fromWorldTypes(client.getWorldType());
		int accountType = client.getVarbitValue(VarbitID.IRONMAN);
		HiscoreEndpoint endpoint = resolveHiscoreEndpoint(
			worldEndpoint,
			accountType,
			getSelectedHiscoreType(worldEndpoint, accountType));
		if (endpoint != lookupEndpoint)
		{
			resetLookup();
			requestHiscores();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!CONFIG_GROUP.equals(event.getGroup()))
		{
			return;
		}

		String key = event.getKey();
		boolean ticked = "true".equals(event.getNewValue());
		if ("hiscoreType".equals(key))
		{
			resetLookup();
			requestHiscores();
		}
		else if (SHOW_ALL_SKILLS_KEY.equals(key))
		{
			if (ticked)
			{
				for (Skill skill : SKILLS)
				{
					autoTick(skill, AUTO_TICKED_BY_ALL_SKILLS_KEY);
				}
			}
			else
			{
				clientThread.invoke(() -> revertAutoTicks(AUTO_TICKED_BY_ALL_SKILLS_KEY));
			}
		}
		else if (SHOW_ALL_99S_KEY.equals(key))
		{
			if (ticked)
			{
				clientThread.invoke(this::tickAll99s);
			}
			else
			{
				clientThread.invoke(() -> revertAutoTicks(AUTO_TICKED_BY_ALL_99S_KEY));
			}
		}
		else if (!ticked)
		{
			Skill skill = skillForConfigKey(key);
			if (skill != null && !pendingUnticks.remove(skill))
			{
				onSkillUnticked(skill);
			}
		}
	}

	/**
	 * Ticks a skill for "All skills" or "All 99s" and remembers that the
	 * master ticked it, so unticking the master can put it back. Skills that
	 * were already ticked are left alone and not remembered.
	 */
	private void autoTick(Skill skill, String listKey)
	{
		if (SkillRankTooltipConfig.showRankFor(config, skill))
		{
			return;
		}

		Set<Skill> autoTicked = readSkillList(listKey);
		autoTicked.add(skill);
		writeSkillList(listKey, autoTicked);
		setSkillTicked(skill, true);
	}

	/**
	 * Called when the player unticks a master box: unticks the skills that
	 * master ticked, unless the other master still covers them.
	 */
	private void revertAutoTicks(String listKey)
	{
		Set<Skill> autoTicked = readSkillList(listKey);
		writeSkillList(listKey, EnumSet.noneOf(Skill.class));

		boolean allSkillsOn = config.showAllSkills();
		boolean all99sOn = config.showAll99s();
		boolean levelsKnown = client.getGameState() == GameState.LOGGED_IN;
		for (Skill skill : autoTicked)
		{
			if (allSkillsOn)
			{
				continue;
			}

			if (all99sOn && levelsKnown && isMaxedLevel(client.getRealSkillLevel(skill)))
			{
				// Still covered by "All 99s": hand it over so unticking that
				// master later puts it back instead.
				Set<Skill> by99s = readSkillList(AUTO_TICKED_BY_ALL_99S_KEY);
				by99s.add(skill);
				writeSkillList(AUTO_TICKED_BY_ALL_99S_KEY, by99s);
				continue;
			}

			if (SkillRankTooltipConfig.showRankFor(config, skill))
			{
				pendingUnticks.add(skill);
				setSkillTicked(skill, false);
			}
		}
	}

	private Set<Skill> readSkillList(String listKey)
	{
		return parseSkillList(configManager.getConfiguration(CONFIG_GROUP, listKey));
	}

	private void writeSkillList(String listKey, Set<Skill> skills)
	{
		if (skills.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, listKey);
		}
		else
		{
			configManager.setConfiguration(CONFIG_GROUP, listKey, formatSkillList(skills));
		}
	}

	static Set<Skill> parseSkillList(String value)
	{
		Set<Skill> skills = EnumSet.noneOf(Skill.class);
		if (value == null || value.isEmpty())
		{
			return skills;
		}

		for (String name : value.split(","))
		{
			try
			{
				skills.add(Skill.valueOf(name.trim()));
			}
			catch (IllegalArgumentException ex)
			{
				// Ignore skills from older or newer versions.
			}
		}

		return skills;
	}

	static String formatSkillList(Set<Skill> skills)
	{
		StringBuilder builder = new StringBuilder();
		for (Skill skill : skills)
		{
			if (builder.length() > 0)
			{
				builder.append(',');
			}
			builder.append(skill.name());
		}
		return builder.toString();
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		// Covers both logging in (every skill reports its level) and reaching
		// a new 99 while "All 99s" is on.
		if (config.showAll99s()
			&& isMaxedLevel(event.getLevel())
			&& isTickableSkill(event.getSkill()))
		{
			autoTick(event.getSkill(), AUTO_TICKED_BY_ALL_99S_KEY);
		}
	}

	private void tickAll99s()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			// Levels aren't known yet; onStatChanged ticks them on login.
			return;
		}

		for (Skill skill : SKILLS)
		{
			if (isMaxedLevel(client.getRealSkillLevel(skill)))
			{
				autoTick(skill, AUTO_TICKED_BY_ALL_99S_KEY);
			}
		}
	}

	/**
	 * The player unticked a skill themselves. A master covering it turns off,
	 * but the skills it ticked stay ticked, so its list is forgotten first.
	 */
	private void onSkillUnticked(Skill skill)
	{
		Set<Skill> by99s = readSkillList(AUTO_TICKED_BY_ALL_99S_KEY);
		if (by99s.remove(skill))
		{
			writeSkillList(AUTO_TICKED_BY_ALL_99S_KEY, by99s);
		}

		if (config.showAllSkills())
		{
			writeSkillList(AUTO_TICKED_BY_ALL_SKILLS_KEY, EnumSet.noneOf(Skill.class));
			configManager.setConfiguration(CONFIG_GROUP, SHOW_ALL_SKILLS_KEY, false);
		}

		if (config.showAll99s())
		{
			clientThread.invoke(() ->
			{
				if (client.getGameState() == GameState.LOGGED_IN
					&& isMaxedLevel(client.getRealSkillLevel(skill)))
				{
					writeSkillList(AUTO_TICKED_BY_ALL_99S_KEY, EnumSet.noneOf(Skill.class));
					configManager.setConfiguration(CONFIG_GROUP, SHOW_ALL_99S_KEY, false);
				}
			});
		}
	}

	private static boolean isTickableSkill(Skill skill)
	{
		for (Skill s : SKILLS)
		{
			if (s == skill)
			{
				return true;
			}
		}
		return false;
	}

	private void setSkillTicked(Skill skill, boolean ticked)
	{
		configManager.setConfiguration(CONFIG_GROUP, configKey(skill), ticked);
	}

	static String configKey(Skill skill)
	{
		return "show" + skill.getName().replace(" ", "");
	}

	static Skill skillForConfigKey(String key)
	{
		for (Skill skill : SKILLS)
		{
			if (configKey(skill).equals(key))
			{
				return skill;
			}
		}

		return null;
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		HiscoreResult result = hiscoreResult;
		if (result == null)
		{
			return;
		}

		Widget tooltip = client.getWidget(InterfaceID.Stats.TOOLTIP);
		if (tooltip == null || tooltip.isHidden())
		{
			return;
		}

		Widget[] children = tooltip.getDynamicChildren();
		if (children == null || children.length <= VALUE_CHILD_INDEX)
		{
			return;
		}

		Widget labelWidget = children[LABEL_CHILD_INDEX];
		Widget valueWidget = children[VALUE_CHILD_INDEX];
		if (labelWidget == null || valueWidget == null)
		{
			return;
		}

		if (!shouldShowRankForTooltip(labelWidget.getText(), config))
		{
			return;
		}

		TooltipText update = buildTooltipText(
			labelWidget.getText(),
			valueWidget.getText(),
			result);
		if (update == null)
		{
			return;
		}

		labelWidget.setText(update.labelText);
		valueWidget.setText(update.valueText);
		resizeTooltip(tooltip, children, update.addedRows * TOOLTIP_ROW_HEIGHT);
		log.debug("Added hiscore rank to a skill tooltip after script {}", event.getScriptId());
	}

	private void requestHiscores()
	{
		if (client.getGameState() != GameState.LOGGED_IN
			|| lookupUsername != null)
		{
			return;
		}

		Player localPlayer = client.getLocalPlayer();
		String username = localPlayer == null ? null : localPlayer.getName();
		if (username == null || username.isEmpty())
		{
			return;
		}

		HiscoreEndpoint worldEndpoint = HiscoreEndpoint.fromWorldTypes(client.getWorldType());
		int accountType = client.getVarbitValue(VarbitID.IRONMAN);
		HiscoreEndpoint endpoint = resolveHiscoreEndpoint(
			worldEndpoint,
			accountType,
			getSelectedHiscoreType(worldEndpoint, accountType));
		lookupUsername = username;
		lookupEndpoint = endpoint;
		HiscoreEndpoint fallback = fallbackEndpoint(endpoint, worldEndpoint, accountType);
		lookup(username, endpoint, endpoint, fallback);
	}

	/**
	 * Looks up {@code queryEndpoint}. If the player isn't on those hiscores and a
	 * fallback is given, looks up the fallback instead. {@code requestedEndpoint}
	 * is what the current lookup was started for, so stale results are dropped.
	 */
	private void lookup(String username, HiscoreEndpoint requestedEndpoint,
		HiscoreEndpoint queryEndpoint, HiscoreEndpoint fallback)
	{
		hiscoreClient.lookupAsync(username, queryEndpoint).whenComplete((result, error) ->
		{
			if (!username.equals(lookupUsername) || requestedEndpoint != lookupEndpoint)
			{
				return;
			}

			if (error != null)
			{
				log.warn("Unable to fetch hiscores for {}", username, error);
				return;
			}

			if (result == null && fallback != null)
			{
				// Not on that build's hiscores, so the build doesn't apply to this
				// account: switch the setting back to the account mode. The
				// resulting ConfigChanged starts a fresh lookup.
				log.debug("{} is not on the {} hiscores, switching back to the account mode", username, queryEndpoint);
				configManager.setConfiguration(CONFIG_GROUP, "hiscoreType", HiscoreType.CURRENT_MODE);
				return;
			}

			hiscoreResult = result;
			if (result == null)
			{
				log.debug("No hiscore result found for {}", username);
			}
			else
			{
				log.debug("Fetched {} hiscores for {}", queryEndpoint, username);
			}
		});
	}

	/**
	 * Build hiscores (1 Defence Pure, Level 3 Skiller) only list accounts that
	 * fit the build, and the client can't tell which build an account is. If
	 * the player isn't listed there, fall back to their account mode's hiscores
	 * so the tooltip still shows a rank.
	 */
	static HiscoreEndpoint fallbackEndpoint(HiscoreEndpoint endpoint, HiscoreEndpoint worldEndpoint, int accountType)
	{
		if (endpoint != HiscoreEndpoint.PURE && endpoint != HiscoreEndpoint.LEVEL_3_SKILLER)
		{
			return null;
		}

		return resolveHiscoreEndpoint(worldEndpoint, accountType, HiscoreType.CURRENT_MODE);
	}

	private void resetLookup()
	{
		lookupUsername = null;
		lookupEndpoint = null;
		hiscoreResult = null;
	}

	private HiscoreType getSelectedHiscoreType(HiscoreEndpoint worldEndpoint, int accountType)
	{
		HiscoreType.setCurrentMode(worldEndpoint, accountType);
		HiscoreType selected = config.hiscoreType();
		if (isHiscoreTypeAvailable(selected, accountType))
		{
			return selected;
		}

		configManager.setConfiguration(CONFIG_GROUP, "hiscoreType", HiscoreType.CURRENT_MODE);
		return HiscoreType.CURRENT_MODE;
	}

	static boolean isHiscoreTypeAvailable(HiscoreType hiscoreType, int accountType)
	{
		switch (hiscoreType)
		{
			case CURRENT_MODE:
				return true;
			case OVERALL:
			case PURE:
			case SKILLER:
				return true;
			default:
				return false;
		}
	}

	static HiscoreEndpoint resolveHiscoreEndpoint(
		HiscoreEndpoint worldEndpoint,
		int accountType,
		HiscoreType hiscoreType)
	{
		// Hardcore Main, Leagues, Deadman etc. have their own hiscores and are
		// not ranked on the main ones, so "All players" can't apply to them.
		if (worldEndpoint != HiscoreEndpoint.NORMAL)
		{
			return worldEndpoint;
		}

		if (!isHiscoreTypeAvailable(hiscoreType, accountType))
		{
			hiscoreType = HiscoreType.CURRENT_MODE;
		}

		switch (hiscoreType)
		{
			case OVERALL:
				return HiscoreEndpoint.NORMAL;
			case PURE:
				return HiscoreEndpoint.PURE;
			case SKILLER:
				return HiscoreEndpoint.LEVEL_3_SKILLER;
			case CURRENT_MODE:
			default:
				break;
		}

		switch (accountType)
		{
			case 1:
				return HiscoreEndpoint.IRONMAN;
			case 2:
				return HiscoreEndpoint.ULTIMATE_IRONMAN;
			case 3:
				return HiscoreEndpoint.HARDCORE_IRONMAN;
			default:
				// Jagex does not provide separate individual Group Ironman hiscores.
				return HiscoreEndpoint.NORMAL;
		}
	}

	static boolean isMaxedLevel(int realLevel)
	{
		return realLevel >= 99;
	}

	static boolean shouldShowRankForTooltip(String labelText, SkillRankTooltipConfig config)
	{
		if (isTotalTooltip(labelText))
		{
			return config.showTotalLevel();
		}

		Skill skill = identifySkillFromTooltip(labelText);
		return skill != null && SkillRankTooltipConfig.showRankFor(config, skill);
	}

	@SuppressWarnings("deprecation")
	private static void resizeTooltip(Widget tooltip, Widget[] children, int addedHeight)
	{
		// This tooltip and its dynamic background are rebuilt by a client script.
		// Adjust the rendered dimensions after that script rather than the layout
		// inputs, which are recalculated and leave the final row clipped.
		tooltip.setHeight(tooltip.getHeight() + addedHeight);
		resizeChild(children, BACKGROUND_CHILD_INDEX, addedHeight);
		resizeChild(children, BORDER_CHILD_INDEX, addedHeight);

		// The extra row can push tooltips for the bottom skill rows past the
		// panel's edge, where they are clipped, so move them up to fit.
		Widget parent = tooltip.getParent();
		if (parent != null)
		{
			int y = fitWithinParent(tooltip.getRelativeY(), tooltip.getHeight(), parent.getHeight());
			if (y != tooltip.getRelativeY())
			{
				tooltip.setRelativeY(y);
			}
		}
	}

	static int fitWithinParent(int y, int height, int parentHeight)
	{
		int overflow = y + height - parentHeight;
		if (overflow <= 0)
		{
			return y;
		}

		return Math.max(0, y - overflow);
	}

	@SuppressWarnings("deprecation")
	private static void resizeChild(Widget[] children, int index, int addedHeight)
	{
		if (children.length > index && children[index] != null)
		{
			Widget child = children[index];
			child.setHeight(child.getHeight() + addedHeight);
		}
	}

	static TooltipText buildTooltipText(
		String labelText,
		String valueText,
		HiscoreResult result)
	{
		if (labelText == null || labelText.isEmpty() || labelText.contains(RANK_LABEL))
		{
			return null;
		}

		HiscoreSkill hiscoreSkill;
		if (isTotalTooltip(labelText))
		{
			hiscoreSkill = HiscoreSkill.OVERALL;
		}
		else
		{
			Skill skill = identifySkillFromTooltip(labelText);
			if (skill == null)
			{
				return null;
			}

			try
			{
				hiscoreSkill = HiscoreSkill.valueOf(skill.name());
			}
			catch (IllegalArgumentException ex)
			{
				return null;
			}
		}

		net.runelite.client.hiscore.Skill skillData = result.getSkill(hiscoreSkill);
		if (skillData == null)
		{
			return null;
		}

		String labels = new StringBuilder(labelText).append("<br>").append(RANK_LABEL).toString();
		String values = new StringBuilder(valueText == null ? "" : valueText)
			.append("<br>")
			.append(formatRank(skillData.getRank()))
			.toString();

		return new TooltipText(labels, values, 1);
	}

	static boolean isTotalTooltip(String text)
	{
		if (text == null)
		{
			return false;
		}

		return Text.removeTags(text).trim().toLowerCase(Locale.ENGLISH).startsWith("total");
	}

	static Skill identifySkillFromTooltip(String text)
	{
		if (text == null)
		{
			return null;
		}

		String plainText = Text.removeTags(text).trim().toLowerCase(Locale.ENGLISH);
		for (Skill skill : SKILLS)
		{
			if (plainText.startsWith(skill.getName().toLowerCase(Locale.ENGLISH)))
			{
				return skill;
			}
		}

		return null;
	}

	static String formatRank(int rank)
	{
		return rank < 0 ? "Unranked" : QuantityFormatter.formatNumber(rank);
	}

	static final class TooltipText
	{
		final String labelText;
		final String valueText;
		final int addedRows;

		private TooltipText(String labelText, String valueText, int addedRows)
		{
			this.labelText = labelText;
			this.valueText = valueText;
			this.addedRows = addedRows;
		}
	}
}
