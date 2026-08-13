package com.yonwiplugins.skillranktooltip;

public enum HiscoreType
{
	CURRENT_MODE("Your mode (automatic)"),
	IRONMAN("Ironman rankings"),
	OVERALL("All players");

	private final String displayName;

	HiscoreType(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
