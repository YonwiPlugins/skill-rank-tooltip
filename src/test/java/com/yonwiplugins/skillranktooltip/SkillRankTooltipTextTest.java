package com.yonwiplugins.skillranktooltip;

import java.util.EnumMap;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreSkill;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SkillRankTooltipTextTest
{
	@Test
	public void leavesTooltipThatFitsInPlace()
	{
		assertEquals(100, SkillRankTooltipPlugin.fitWithinParent(100, 50, 250));
	}

	@Test
	public void movesOverflowingTooltipUp()
	{
		assertEquals(187, SkillRankTooltipPlugin.fitWithinParent(200, 63, 250));
	}

	@Test
	public void neverMovesTooltipAboveParent()
	{
		assertEquals(0, SkillRankTooltipPlugin.fitWithinParent(10, 300, 250));
	}

	@Test
	public void addsFormattedSkillRank()
	{
		HiscoreResult result = hiscores(HiscoreSkill.RANGED, 1_234_567, 42);

		SkillRankTooltipPlugin.TooltipText update = SkillRankTooltipPlugin.buildTooltipText(
			"Ranged XP:<br>Next level at:",
			"1,000<br>2,000",
			result);

		assertEquals("Ranged XP:<br>Next level at:<br>Rank:", update.labelText);
		assertEquals("1,000<br>2,000<br>1,234,567", update.valueText);
		assertEquals(1, update.addedRows);
	}

	@Test
	public void onlyAddsTheHoveredSkillRank()
	{
		HiscoreResult result = hiscores(HiscoreSkill.ATTACK, 100, 200);

		SkillRankTooltipPlugin.TooltipText update = SkillRankTooltipPlugin.buildTooltipText(
			"Attack XP:",
			"13,034,431",
			result);

		assertEquals("Attack XP:<br>Rank:", update.labelText);
		assertEquals("13,034,431<br>100", update.valueText);
		assertEquals(1, update.addedRows);
	}

	@Test
	public void displaysUnranked()
	{
		HiscoreResult result = hiscores(HiscoreSkill.CONSTRUCTION, -1, -1);

		SkillRankTooltipPlugin.TooltipText update = SkillRankTooltipPlugin.buildTooltipText(
			"Construction XP:",
			"0",
			result);

		assertEquals("0<br>Unranked", update.valueText);
	}

	@Test
	public void supportsSailing()
	{
		HiscoreResult result = hiscores(HiscoreSkill.SAILING, 321, 654);

		SkillRankTooltipPlugin.TooltipText update = SkillRankTooltipPlugin.buildTooltipText(
			"<col=ff981f>Sailing XP:</col>",
			"500",
			result);

		assertEquals(Skill.SAILING, SkillRankTooltipPlugin.identifySkillFromTooltip("<col=ff981f>Sailing XP:</col>"));
		assertEquals("500<br>321", update.valueText);
	}

	@Test
	public void addsOverallRankToTotalTooltip()
	{
		HiscoreResult result = hiscores(HiscoreSkill.ATTACK, 100, 12_345);

		SkillRankTooltipPlugin.TooltipText update = SkillRankTooltipPlugin.buildTooltipText(
			"Total level:<br>Total XP:",
			"2,277<br>300,000,000",
			result);

		assertEquals("Total level:<br>Total XP:<br>Rank:", update.labelText);
		assertEquals("2,277<br>300,000,000<br>12,345", update.valueText);
		assertEquals(1, update.addedRows);
	}

	@Test
	public void namesTheDetectedAccountMode()
	{
		HiscoreType.setCurrentMode(0);
		assertEquals("Main", HiscoreType.CURRENT_MODE.toString());
		HiscoreType.setCurrentMode(1);
		assertEquals("Ironman", HiscoreType.CURRENT_MODE.toString());
		HiscoreType.setCurrentMode(2);
		assertEquals("Ultimate Ironman", HiscoreType.CURRENT_MODE.toString());
		HiscoreType.setCurrentMode(3);
		assertEquals("Hardcore Ironman", HiscoreType.CURRENT_MODE.toString());
	}

	@Test
	public void selectsCurrentModeHiscoresOnNormalWorlds()
	{
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 0, HiscoreType.CURRENT_MODE));
		assertEquals(HiscoreEndpoint.IRONMAN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 1, HiscoreType.CURRENT_MODE));
		assertEquals(HiscoreEndpoint.ULTIMATE_IRONMAN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 2, HiscoreType.CURRENT_MODE));
		assertEquals(HiscoreEndpoint.HARDCORE_IRONMAN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 3, HiscoreType.CURRENT_MODE));
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 4, HiscoreType.CURRENT_MODE));
	}

	@Test
	public void selectsAllPlayerRankCategory()
	{
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 1, HiscoreType.OVERALL));
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 2, HiscoreType.OVERALL));
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 3, HiscoreType.OVERALL));
	}

	@Test
	public void rejectsRankCategoriesForOtherAccountTypes()
	{
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 0, HiscoreType.OVERALL));
		assertEquals(HiscoreEndpoint.HARDCORE_IRONMAN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 3, HiscoreType.CURRENT_MODE));

		assertEquals(true, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.OVERALL, 0));
		assertEquals(true, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.OVERALL, 1));
		assertEquals(true, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.OVERALL, 2));
		assertEquals(true, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.OVERALL, 3));
	}

	@Test
	public void selectsHardcoreMainHiscores()
	{
		assertEquals(HiscoreEndpoint.HARDCORE_MAIN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.HARDCORE_MAIN, 0, HiscoreType.CURRENT_MODE));
		// Hardcore Mains are not ranked on the main hiscores.
		assertEquals(HiscoreEndpoint.HARDCORE_MAIN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.HARDCORE_MAIN, 0, HiscoreType.OVERALL));
	}

	@Test
	public void namesHardcoreMainMode()
	{
		HiscoreType.setCurrentMode(HiscoreEndpoint.HARDCORE_MAIN, 0);
		assertEquals("Hardcore Main", HiscoreType.CURRENT_MODE.toString());
		HiscoreType.setCurrentMode(HiscoreEndpoint.NORMAL, 0);
		assertEquals("Main", HiscoreType.CURRENT_MODE.toString());
	}

	@Test
	public void mapsEverySkillToItsCheckbox()
	{
		java.util.Set<String> configKeys = new java.util.HashSet<>();
		for (java.lang.reflect.Method method : SkillRankTooltipConfig.class.getMethods())
		{
			net.runelite.client.config.ConfigItem item = method.getAnnotation(net.runelite.client.config.ConfigItem.class);
			if (item != null)
			{
				configKeys.add(item.keyName());
			}
		}

		for (Skill skill : SkillRankTooltipPlugin.SKILLS)
		{
			assertEquals(skill, SkillRankTooltipPlugin.skillForConfigKey(SkillRankTooltipPlugin.configKey(skill)));
			// Every skill the plugin ticks must have a real checkbox.
			assertEquals(true, configKeys.contains(SkillRankTooltipPlugin.configKey(skill)));
		}

		assertNull(SkillRankTooltipPlugin.skillForConfigKey("showOverall"));

		assertEquals("showRunecraft", SkillRankTooltipPlugin.configKey(Skill.RUNECRAFT));
		assertNull(SkillRankTooltipPlugin.skillForConfigKey("showTotalLevel"));
		assertNull(SkillRankTooltipPlugin.skillForConfigKey("showAll99s"));
		assertNull(SkillRankTooltipPlugin.skillForConfigKey("hiscoreType"));
	}

	@Test
	public void onlyLevel99CountsAsMaxed()
	{
		assertEquals(false, SkillRankTooltipPlugin.isMaxedLevel(98));
		assertEquals(true, SkillRankTooltipPlugin.isMaxedLevel(99));
	}

	@Test
	public void selectsBuildHiscores()
	{
		assertEquals(HiscoreEndpoint.PURE,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 0, HiscoreType.PURE));
		assertEquals(HiscoreEndpoint.LEVEL_3_SKILLER,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 1, HiscoreType.SKILLER));
		// World-specific hiscores still win.
		assertEquals(HiscoreEndpoint.HARDCORE_MAIN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.HARDCORE_MAIN, 0, HiscoreType.PURE));
	}

	@Test
	public void buildHiscoresFallBackToAccountMode()
	{
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.fallbackEndpoint(HiscoreEndpoint.PURE, HiscoreEndpoint.NORMAL, 0));
		assertEquals(HiscoreEndpoint.IRONMAN,
			SkillRankTooltipPlugin.fallbackEndpoint(HiscoreEndpoint.LEVEL_3_SKILLER, HiscoreEndpoint.NORMAL, 1));
		assertNull(SkillRankTooltipPlugin.fallbackEndpoint(HiscoreEndpoint.NORMAL, HiscoreEndpoint.NORMAL, 0));
		assertNull(SkillRankTooltipPlugin.fallbackEndpoint(HiscoreEndpoint.IRONMAN, HiscoreEndpoint.NORMAL, 1));
	}

	@Test
	public void worldHiscoresOverrideOtherCategories()
	{
		assertEquals(HiscoreEndpoint.HARDCORE_MAIN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.HARDCORE_MAIN, 0, HiscoreType.SKILLER));
		assertEquals(HiscoreEndpoint.FRESH_START_WORLD,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.FRESH_START_WORLD, 0, HiscoreType.PURE));
		assertEquals(HiscoreEndpoint.DEADMAN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.DEADMAN, 0, HiscoreType.OVERALL));
	}

	@Test
	public void namesGroupIronmanModes()
	{
		HiscoreType.setCurrentMode(4);
		assertEquals("Group Ironman", HiscoreType.CURRENT_MODE.toString());
		HiscoreType.setCurrentMode(5);
		assertEquals("Hardcore Group Ironman", HiscoreType.CURRENT_MODE.toString());
		HiscoreType.setCurrentMode(6);
		assertEquals("Unranked Group Ironman", HiscoreType.CURRENT_MODE.toString());
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 5, HiscoreType.CURRENT_MODE));
	}

	@Test
	public void keepsWorldSpecificHiscores()
	{
		assertEquals(HiscoreEndpoint.SEASONAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.SEASONAL, 3, HiscoreType.OVERALL));
	}

	@Test
	public void supportsPerSkillAndTotalCheckboxes()
	{
		SkillRankTooltipConfig config = new SkillRankTooltipConfig()
		{
			@Override
			public boolean showAttack()
			{
				return false;
			}

			@Override
			public boolean showTotalLevel()
			{
				return false;
			}
		};

		assertEquals(true, SkillRankTooltipPlugin.shouldShowRankForTooltip(
			"Ranged XP:", config));
		assertEquals(false, SkillRankTooltipPlugin.shouldShowRankForTooltip(
			"Attack XP:", config));
		assertEquals(false, SkillRankTooltipPlugin.shouldShowRankForTooltip(
			"Total level:<br>Total XP:", config));
	}

	@Test
	public void ignoresNonSkillAndAlreadyModifiedTooltips()
	{
		HiscoreResult result = hiscores(HiscoreSkill.ATTACK, 100, 200);

		assertNull(SkillRankTooltipPlugin.buildTooltipText("Combat level:", "126", result));
		assertNull(SkillRankTooltipPlugin.buildTooltipText("Attack XP:<br>Rank:", "1<br>100", result));
	}

	private static HiscoreResult hiscores(HiscoreSkill skill, int skillRank, int overallRank)
	{
		Map<HiscoreSkill, net.runelite.client.hiscore.Skill> skills = new EnumMap<>(HiscoreSkill.class);
		skills.put(skill, new net.runelite.client.hiscore.Skill(skillRank, 99, 13_034_431));
		skills.put(HiscoreSkill.OVERALL, new net.runelite.client.hiscore.Skill(overallRank, 2_277, 300_000_000));
		return new HiscoreResult("Tester", skills);
	}
}
