package com.yonwiplugins.skillranktooltip;

public enum HiscoreType
{
	CURRENT_MODE("Main"),
	OVERALL("All players");

	private final String displayName;
	private static volatile String currentModeDisplayName = "Main";

	HiscoreType(String displayName)
	{
		this.displayName = displayName;
	}

	static void setCurrentMode(int accountType)
	{
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
