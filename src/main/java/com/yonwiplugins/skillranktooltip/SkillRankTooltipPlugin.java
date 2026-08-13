package com.yonwiplugins.skillranktooltip;

import com.google.inject.Provides;
import java.util.Locale;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
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

	@Inject
	private Client client;

	@Inject
	private HiscoreClient hiscoreClient;

	@Inject
	private SkillRankTooltipConfig config;

	@Inject
	private ConfigManager configManager;

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

		HiscoreEndpoint endpoint = resolveHiscoreEndpoint(
			HiscoreEndpoint.fromWorldTypes(client.getWorldType()),
			client.getVarbitValue(VarbitID.IRONMAN),
			getSelectedHiscoreType(client.getVarbitValue(VarbitID.IRONMAN)));
		if (endpoint != lookupEndpoint)
		{
			resetLookup();
			requestHiscores();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!CONFIG_GROUP.equals(event.getGroup()) || !"hiscoreType".equals(event.getKey()))
		{
			return;
		}

		resetLookup();
		requestHiscores();
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

		int accountType = client.getVarbitValue(VarbitID.IRONMAN);
		HiscoreEndpoint endpoint = resolveHiscoreEndpoint(
			HiscoreEndpoint.fromWorldTypes(client.getWorldType()),
			accountType,
			getSelectedHiscoreType(accountType));
		lookupUsername = username;
		lookupEndpoint = endpoint;
		hiscoreClient.lookupAsync(username, endpoint).whenComplete((result, error) ->
		{
			if (!username.equals(lookupUsername) || endpoint != lookupEndpoint)
			{
				return;
			}

			if (error != null)
			{
				log.warn("Unable to fetch hiscores for {}", username, error);
				return;
			}

			hiscoreResult = result;
			if (result == null)
			{
				log.debug("No hiscore result found for {}", username);
			}
			else
			{
				log.debug("Fetched hiscores for {}", username);
			}
		});
	}

	private void resetLookup()
	{
		lookupUsername = null;
		lookupEndpoint = null;
		hiscoreResult = null;
	}

	private HiscoreType getSelectedHiscoreType(int accountType)
	{
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
				return accountType == 1 || accountType == 2 || accountType == 3;
			case IRONMAN:
				return accountType == 2 || accountType == 3;
			default:
				return false;
		}
	}

	static HiscoreEndpoint resolveHiscoreEndpoint(
		HiscoreEndpoint worldEndpoint,
		int accountType,
		HiscoreType hiscoreType)
	{
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
			case IRONMAN:
				return HiscoreEndpoint.IRONMAN;
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
		for (Skill skill : Skill.values())
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
