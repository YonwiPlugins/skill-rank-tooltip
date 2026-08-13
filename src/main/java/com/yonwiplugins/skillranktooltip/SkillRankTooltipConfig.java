package com.yonwiplugins.skillranktooltip;

import net.runelite.api.Skill;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("skill-rank-tooltip")
public interface SkillRankTooltipConfig extends Config
{
	@ConfigItem(
		keyName = "showRanks",
		name = "Show ranks",
		description = "Add rank rows to skill tooltips",
		position = 0
	)
	default boolean showRanks()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hiscoreType",
		name = "Rank category",
		description = "Your mode automatically uses this character's Main, Ironman, UIM, or HCIM hiscores",
		position = 1
	)
	default HiscoreType hiscoreType()
	{
		return HiscoreType.CURRENT_MODE;
	}

	@ConfigItem(
		keyName = "showTotalLevel",
		name = "Show rank for Total level",
		description = "Add the overall rank when hovering Total level",
		position = 10
	)
	default boolean showTotalLevel()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAttack",
		name = "Show rank for Attack",
		description = "Add a rank when hovering Attack",
		position = 11
	)
	default boolean showAttack()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDefence",
		name = "Show rank for Defence",
		description = "Add a rank when hovering Defence",
		position = 12
	)
	default boolean showDefence()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showStrength",
		name = "Show rank for Strength",
		description = "Add a rank when hovering Strength",
		position = 13
	)
	default boolean showStrength()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHitpoints",
		name = "Show rank for Hitpoints",
		description = "Add a rank when hovering Hitpoints",
		position = 14
	)
	default boolean showHitpoints()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRanged",
		name = "Show rank for Ranged",
		description = "Add a rank when hovering Ranged",
		position = 15
	)
	default boolean showRanged()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPrayer",
		name = "Show rank for Prayer",
		description = "Add a rank when hovering Prayer",
		position = 16
	)
	default boolean showPrayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMagic",
		name = "Show rank for Magic",
		description = "Add a rank when hovering Magic",
		position = 17
	)
	default boolean showMagic()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCooking",
		name = "Show rank for Cooking",
		description = "Add a rank when hovering Cooking",
		position = 18
	)
	default boolean showCooking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWoodcutting",
		name = "Show rank for Woodcutting",
		description = "Add a rank when hovering Woodcutting",
		position = 19
	)
	default boolean showWoodcutting()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFletching",
		name = "Show rank for Fletching",
		description = "Add a rank when hovering Fletching",
		position = 20
	)
	default boolean showFletching()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFishing",
		name = "Show rank for Fishing",
		description = "Add a rank when hovering Fishing",
		position = 21
	)
	default boolean showFishing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFiremaking",
		name = "Show rank for Firemaking",
		description = "Add a rank when hovering Firemaking",
		position = 22
	)
	default boolean showFiremaking()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCrafting",
		name = "Show rank for Crafting",
		description = "Add a rank when hovering Crafting",
		position = 23
	)
	default boolean showCrafting()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSmithing",
		name = "Show rank for Smithing",
		description = "Add a rank when hovering Smithing",
		position = 24
	)
	default boolean showSmithing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMining",
		name = "Show rank for Mining",
		description = "Add a rank when hovering Mining",
		position = 25
	)
	default boolean showMining()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHerblore",
		name = "Show rank for Herblore",
		description = "Add a rank when hovering Herblore",
		position = 26
	)
	default boolean showHerblore()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAgility",
		name = "Show rank for Agility",
		description = "Add a rank when hovering Agility",
		position = 27
	)
	default boolean showAgility()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showThieving",
		name = "Show rank for Thieving",
		description = "Add a rank when hovering Thieving",
		position = 28
	)
	default boolean showThieving()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSlayer",
		name = "Show rank for Slayer",
		description = "Add a rank when hovering Slayer",
		position = 29
	)
	default boolean showSlayer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFarming",
		name = "Show rank for Farming",
		description = "Add a rank when hovering Farming",
		position = 30
	)
	default boolean showFarming()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRunecraft",
		name = "Show rank for Runecraft",
		description = "Add a rank when hovering Runecraft",
		position = 31
	)
	default boolean showRunecraft()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHunter",
		name = "Show rank for Hunter",
		description = "Add a rank when hovering Hunter",
		position = 32
	)
	default boolean showHunter()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showConstruction",
		name = "Show rank for Construction",
		description = "Add a rank when hovering Construction",
		position = 33
	)
	default boolean showConstruction()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSailing",
		name = "Show rank for Sailing",
		description = "Add a rank when hovering Sailing",
		position = 34
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
