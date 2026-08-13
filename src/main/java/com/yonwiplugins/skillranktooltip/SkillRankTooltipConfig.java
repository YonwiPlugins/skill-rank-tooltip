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
		description = "Use this character's account mode or a broader eligible rank category",
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

	@ConfigItem(
		keyName = "showTotalLevel",
		name = "Total level",
		description = "Add the overall rank when hovering Total level",
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
		description = "Add a rank when hovering Attack",
		position = 11,
		section = skillsSection
	)
	default boolean showAttack()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDefence",
		name = "Defence",
		description = "Add a rank when hovering Defence",
		position = 12,
		section = skillsSection
	)
	default boolean showDefence()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showStrength",
		name = "Strength",
		description = "Add a rank when hovering Strength",
		position = 13,
		section = skillsSection
	)
	default boolean showStrength()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHitpoints",
		name = "Hitpoints",
		description = "Add a rank when hovering Hitpoints",
		position = 14,
		section = skillsSection
	)
	default boolean showHitpoints()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRanged",
		name = "Ranged",
		description = "Add a rank when hovering Ranged",
		position = 15,
		section = skillsSection
	)
	default boolean showRanged()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPrayer",
		name = "Prayer",
		description = "Add a rank when hovering Prayer",
		position = 16,
		section = skillsSection
	)
	default boolean showPrayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMagic",
		name = "Magic",
		description = "Add a rank when hovering Magic",
		position = 17,
		section = skillsSection
	)
	default boolean showMagic()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCooking",
		name = "Cooking",
		description = "Add a rank when hovering Cooking",
		position = 18,
		section = skillsSection
	)
	default boolean showCooking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWoodcutting",
		name = "Woodcutting",
		description = "Add a rank when hovering Woodcutting",
		position = 19,
		section = skillsSection
	)
	default boolean showWoodcutting()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFletching",
		name = "Fletching",
		description = "Add a rank when hovering Fletching",
		position = 20,
		section = skillsSection
	)
	default boolean showFletching()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFishing",
		name = "Fishing",
		description = "Add a rank when hovering Fishing",
		position = 21,
		section = skillsSection
	)
	default boolean showFishing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFiremaking",
		name = "Firemaking",
		description = "Add a rank when hovering Firemaking",
		position = 22,
		section = skillsSection
	)
	default boolean showFiremaking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCrafting",
		name = "Crafting",
		description = "Add a rank when hovering Crafting",
		position = 23,
		section = skillsSection
	)
	default boolean showCrafting()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSmithing",
		name = "Smithing",
		description = "Add a rank when hovering Smithing",
		position = 24,
		section = skillsSection
	)
	default boolean showSmithing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMining",
		name = "Mining",
		description = "Add a rank when hovering Mining",
		position = 25,
		section = skillsSection
	)
	default boolean showMining()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHerblore",
		name = "Herblore",
		description = "Add a rank when hovering Herblore",
		position = 26,
		section = skillsSection
	)
	default boolean showHerblore()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAgility",
		name = "Agility",
		description = "Add a rank when hovering Agility",
		position = 27,
		section = skillsSection
	)
	default boolean showAgility()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showThieving",
		name = "Thieving",
		description = "Add a rank when hovering Thieving",
		position = 28,
		section = skillsSection
	)
	default boolean showThieving()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSlayer",
		name = "Slayer",
		description = "Add a rank when hovering Slayer",
		position = 29,
		section = skillsSection
	)
	default boolean showSlayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFarming",
		name = "Farming",
		description = "Add a rank when hovering Farming",
		position = 30,
		section = skillsSection
	)
	default boolean showFarming()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRunecraft",
		name = "Runecraft",
		description = "Add a rank when hovering Runecraft",
		position = 31,
		section = skillsSection
	)
	default boolean showRunecraft()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHunter",
		name = "Hunter",
		description = "Add a rank when hovering Hunter",
		position = 32,
		section = skillsSection
	)
	default boolean showHunter()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showConstruction",
		name = "Construction",
		description = "Add a rank when hovering Construction",
		position = 33,
		section = skillsSection
	)
	default boolean showConstruction()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSailing",
		name = "Sailing",
		description = "Add a rank when hovering Sailing",
		position = 34,
		section = skillsSection
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
