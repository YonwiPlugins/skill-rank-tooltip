package com.yonwiplugins.skillranktooltip;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("skill-rank-tooltip")
public interface SkillRankTooltipConfig extends Config
{
	@ConfigItem(
		keyName = "showOverall",
		name = "Show overall rank",
		description = "Also add your overall hiscore rank beneath individual skill ranks",
		position = 1
	)
	default boolean showOverall()
	{
		return false;
	}
}
