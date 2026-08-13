package com.yonwiplugins.skillranktooltip;

import java.util.Collections;
import java.util.Set;
import net.runelite.api.Skill;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("skill-rank-tooltip")
public interface SkillRankTooltipConfig extends Config
{
	@ConfigSection(
		name = "Per-skill settings",
		description = "Hide ranks for selected skills",
		position = 1,
		closedByDefault = true
	)
	String perSkillSection = "perSkill";

	@ConfigItem(
		keyName = "showRanks",
		name = "Show ranks",
		description = "Add the rank row to skill tooltips",
		position = 0
	)
	default boolean showRanks()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hiddenSkills",
		name = "Hidden skills",
		description = "Do not add ranks when hovering the selected skills",
		position = 2,
		section = perSkillSection
	)
	default Set<Skill> hiddenSkills()
	{
		return Collections.emptySet();
	}
}
