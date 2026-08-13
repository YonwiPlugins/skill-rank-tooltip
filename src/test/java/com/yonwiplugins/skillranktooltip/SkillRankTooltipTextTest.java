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
	public void selectsValidRankCategories()
	{
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 1, HiscoreType.OVERALL));
		assertEquals(HiscoreEndpoint.IRONMAN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 2, HiscoreType.IRONMAN));
		assertEquals(HiscoreEndpoint.IRONMAN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 3, HiscoreType.IRONMAN));
	}

	@Test
	public void rejectsRankCategoriesForOtherAccountTypes()
	{
		assertEquals(HiscoreEndpoint.NORMAL,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 0, HiscoreType.OVERALL));
		assertEquals(HiscoreEndpoint.HARDCORE_IRONMAN,
			SkillRankTooltipPlugin.resolveHiscoreEndpoint(HiscoreEndpoint.NORMAL, 3, HiscoreType.CURRENT_MODE));

		assertEquals(false, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.IRONMAN, 0));
		assertEquals(false, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.IRONMAN, 1));
		assertEquals(false, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.OVERALL, 0));
		assertEquals(true, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.OVERALL, 1));
		assertEquals(true, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.IRONMAN, 2));
		assertEquals(true, SkillRankTooltipPlugin.isHiscoreTypeAvailable(HiscoreType.IRONMAN, 3));
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
