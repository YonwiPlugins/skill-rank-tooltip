package com.yonwiplugins.skillranktooltip;

import net.runelite.client.hiscore.HiscoreEndpoint;

public enum HiscoreType
{
	CURRENT_MODE("Main"),
	PURE("1 Defence Pure"),
	SKILLER("Level 3 Skiller"),
	OVERALL("All players");

	private final String displayName;
	private static volatile String currentModeDisplayName = "Main";

	HiscoreType(String displayName)
	{
		this.displayName = displayName;
	}

	static void setCurrentMode(int accountType)
	{
		setCurrentMode(HiscoreEndpoint.NORMAL, accountType);
	}

	static void setCurrentMode(HiscoreEndpoint worldEndpoint, int accountType)
	{
		if (worldEndpoint != null && worldEndpoint != HiscoreEndpoint.NORMAL)
		{
			// Hardcore Main, Leagues, Deadman etc. come from the world type and
			// have their own hiscores, so name the mode after them.
			currentModeDisplayName = worldEndpoint.getName();
			return;
		}

		switch (accountType)
		{
			case 1:
				currentModeDisplayName = "Ironman";
				break;
			case 2:
				currentModeDisplayName = "Ultimate Ironman";
				break;
			case 3:
				currentModeDisplayName = "Hardcore Ironman";
				break;
			case 4:
				// Group Ironmen are ranked on the normal hiscores. Jagex's group
				// hiscores rank whole groups, not individual players.
				currentModeDisplayName = "Group Ironman";
				break;
			case 5:
				currentModeDisplayName = "Hardcore Group Ironman";
				break;
			case 6:
				currentModeDisplayName = "Unranked Group Ironman";
				break;
			case 0:
				currentModeDisplayName = "Main";
				break;
			default:
				currentModeDisplayName = "All players";
				break;
		}
	}

	@Override
	public String toString()
	{
		return this == CURRENT_MODE ? currentModeDisplayName : displayName;
	}
}
