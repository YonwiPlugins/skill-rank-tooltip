package com.yonwiplugins.skillranktooltip;

import net.runelite.api.Skill;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("skill-rank-tooltip")
public interface SkillRankTooltipConfig extends Config
{
	@ConfigItem(
		keyName = "hiscoreType",
		name = "Rank category",
		description = "Use this character's account mode, a build (1 Defence Pure or Level 3 Skiller), or all-player rankings. If you aren't on the build's hiscores, it switches back to your account mode.",
		position = 0
	)
	default HiscoreType hiscoreType()
	{
		return HiscoreType.CURRENT_MODE;
	}

	@ConfigSection(
		name = "Show ranks for:",
		description = "Choose which skill tooltips show a rank",
		position = 1,
		closedByDefault = false
	)
	String skillsSection = "skills";

	@ConfigSection(
		name = "Individual skills",
		description = "Choose which skills show a rank",
		position = 2,
		closedByDefault = false
	)
	String individualSkillsSection = "individualSkills";

	@ConfigItem(
		keyName = "showAllSkills",
		name = "All skills",
		description = "Ticks every skill under Individual skills. Unticking this puts back only the skills it ticked. Unticking a skill yourself turns this off and leaves the rest ticked. To see the ticks change, go back to the plugin list and reopen Skill Ranks.",
		position = 8,
		section = skillsSection
	)
	default boolean showAllSkills()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showAll99s",
		name = "All 99s",
		description = "Ticks every skill you have at 99, and new 99s as you get them. Other ticks are left alone. Unticking this puts back only the 99s it ticked. Unticking one of your 99s yourself turns this off and leaves the rest ticked. To see the ticks change, go back to the plugin list and reopen Skill Ranks.",
		position = 9,
		section = skillsSection
	)
	default boolean showAll99s()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showTotalLevel",
		name = "Total level",
		description = "",
		position = 10,
		section = skillsSection
	)
	default boolean showTotalLevel()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAttack",
		name = "Attack",
		description = "",
		position = 11,
		section = individualSkillsSection
	)
	default boolean showAttack()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDefence",
		name = "Defence",
		description = "",
		position = 12,
		section = individualSkillsSection
	)
	default boolean showDefence()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showStrength",
		name = "Strength",
		description = "",
		position = 13,
		section = individualSkillsSection
	)
	default boolean showStrength()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHitpoints",
		name = "Hitpoints",
		description = "",
		position = 14,
		section = individualSkillsSection
	)
	default boolean showHitpoints()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRanged",
		name = "Ranged",
		description = "",
		position = 15,
		section = individualSkillsSection
	)
	default boolean showRanged()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPrayer",
		name = "Prayer",
		description = "",
		position = 16,
		section = individualSkillsSection
	)
	default boolean showPrayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMagic",
		name = "Magic",
		description = "",
		position = 17,
		section = individualSkillsSection
	)
	default boolean showMagic()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCooking",
		name = "Cooking",
		description = "",
		position = 18,
		section = individualSkillsSection
	)
	default boolean showCooking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWoodcutting",
		name = "Woodcutting",
		description = "",
		position = 19,
		section = individualSkillsSection
	)
	default boolean showWoodcutting()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFletching",
		name = "Fletching",
		description = "",
		position = 20,
		section = individualSkillsSection
	)
	default boolean showFletching()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFishing",
		name = "Fishing",
		description = "",
		position = 21,
		section = individualSkillsSection
	)
	default boolean showFishing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFiremaking",
		name = "Firemaking",
		description = "",
		position = 22,
		section = individualSkillsSection
	)
	default boolean showFiremaking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCrafting",
		name = "Crafting",
		description = "",
		position = 23,
		section = individualSkillsSection
	)
	default boolean showCrafting()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSmithing",
		name = "Smithing",
		description = "",
		position = 24,
		section = individualSkillsSection
	)
	default boolean showSmithing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMining",
		name = "Mining",
		description = "",
		position = 25,
		section = individualSkillsSection
	)
	default boolean showMining()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHerblore",
		name = "Herblore",
		description = "",
		position = 26,
		section = individualSkillsSection
	)
	default boolean showHerblore()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAgility",
		name = "Agility",
		description = "",
		position = 27,
		section = individualSkillsSection
	)
	default boolean showAgility()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showThieving",
		name = "Thieving",
		description = "",
		position = 28,
		section = individualSkillsSection
	)
	default boolean showThieving()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSlayer",
		name = "Slayer",
		description = "",
		position = 29,
		section = individualSkillsSection
	)
	default boolean showSlayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFarming",
		name = "Farming",
		description = "",
		position = 30,
		section = individualSkillsSection
	)
	default boolean showFarming()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRunecraft",
		name = "Runecraft",
		description = "",
		position = 31,
		section = individualSkillsSection
	)
	default boolean showRunecraft()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHunter",
		name = "Hunter",
		description = "",
		position = 32,
		section = individualSkillsSection
	)
	default boolean showHunter()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showConstruction",
		name = "Construction",
		description = "",
		position = 33,
		section = individualSkillsSection
	)
	default boolean showConstruction()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSailing",
		name = "Sailing",
		description = "",
		position = 34,
		section = individualSkillsSection
	)
	default boolean showSailing()
	{
		return true;
	}

	static boolean showRankFor(SkillRankTooltipConfig config, Skill skill)
	{
		switch (skill)
		{
			case ATTACK:
				return config.showAttack();
			case DEFENCE:
				return config.showDefence();
			case STRENGTH:
				return config.showStrength();
			case HITPOINTS:
				return config.showHitpoints();
			case RANGED:
				return config.showRanged();
			case PRAYER:
				return config.showPrayer();
			case MAGIC:
				return config.showMagic();
			case COOKING:
				return config.showCooking();
			case WOODCUTTING:
				return config.showWoodcutting();
			case FLETCHING:
				return config.showFletching();
			case FISHING:
				return config.showFishing();
			case FIREMAKING:
				return config.showFiremaking();
			case CRAFTING:
				return config.showCrafting();
			case SMITHING:
				return config.showSmithing();
			case MINING:
				return config.showMining();
			case HERBLORE:
				return config.showHerblore();
			case AGILITY:
				return config.showAgility();
			case THIEVING:
				return config.showThieving();
			case SLAYER:
				return config.showSlayer();
			case FARMING:
				return config.showFarming();
			case RUNECRAFT:
				return config.showRunecraft();
			case HUNTER:
				return config.showHunter();
			case CONSTRUCTION:
				return config.showConstruction();
			case SAILING:
				return config.showSailing();
			default:
				return true;
		}
	}
}
