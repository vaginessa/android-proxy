package com.lechucksoftware.proxy.proxysettings;

public class Constants
{
	public static final String PREFERENCES_FILENAME = "ProxySettingsPreferences";
	
	public static final String PREFERENCES_ACCEPTED_DISCLAIMER = "AcceptedDisclaimer";
	
	public static final String PREFERENCES_APPRATE_DONT_SHOW_AGAIN = "DontShowAgainAppRater";
	public static final String PREFERENCES_APPRATE_LAUNCH_COUNT = "LaunchCount";
	public static final String PREFERENCES_APPRATE_DATE_FIRST_LAUNCH = "DateFirstLaunch";
	
	public final static int   APPRATE_DAYS_UNTIL_PROMPT	 = 7;
	public final static int   APPRATE_LAUNCHES_UNTIL_PROMPT = 10;
	
	public enum ProxyCheckStatus
	{
		CHECKING,
		CHECKED
	}
}
