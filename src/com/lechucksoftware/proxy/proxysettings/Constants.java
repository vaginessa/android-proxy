package com.lechucksoftware.proxy.proxysettings;

public class Constants
{
	public static final String PREFERENCES_FILENAME = "ProxySettingsPreferences";
	
	public static final String PREFERENCES_ACCEPTED_DISCLAIMER = "AcceptedDisclaimer";
	
	public static final String PREFERENCES_APPRATE_DONT_SHOW_AGAIN = "DontShowAgainAppRater";
	public static final String PREFERENCES_APPRATE_LAUNCH_COUNT = "LaunchCount";
	public static final String PREFERENCES_APPRATE_DATE_FIRST_LAUNCH = "DateFirstLaunch";
	
	public static final String PREFERENCES_CACHED_URLS = "CachedUrls";
	
	public final static int   APPRATE_DAYS_UNTIL_PROMPT	 = 7;
	public final static int   APPRATE_LAUNCHES_UNTIL_PROMPT = 10;
	
	/**
	 * Intent 
	 * */
//	public static final String PROXY_CONFIGURATION_CHANGED = "com.lechucksoftware.proxy.proxysettings.PROXY_CHANGE";
	public static final String PROXY_SETTINGS_STARTED = "com.lechucksoftware.proxy.proxysettings.PROXY_SETTINGS_STARTED";
	public static final String PROXY_CONFIGURATION_UPDATED = "com.lechucksoftware.proxy.proxysettings.PROXY_CONFIGURATION_UPDATED";
	public static final String PROXY_UPDATE_NOTIFICATION = "com.lechucksoftware.proxy.proxysettings.PROXY_UPDATE_NOTIFICATION";
	
	
	
	
	public enum ProxyCheckStatus
	{
		CHECKING,
		CHECKED
	}
	
	public enum DonwloadStatus
	{
		DOWNLOADING,
		STARTED,
		ERROR
	}
}
