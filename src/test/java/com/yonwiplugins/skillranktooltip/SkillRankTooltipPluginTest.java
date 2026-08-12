package com.yonwiplugins.skillranktooltip;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SkillRankTooltipPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SkillRankTooltipPlugin.class);
		RuneLite.main(args);
	}
}
