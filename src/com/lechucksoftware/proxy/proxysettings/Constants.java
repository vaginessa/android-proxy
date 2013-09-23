package com.lechucksoftware.proxy.proxysettings;

public class Constants
{
	public static final String PREFERENCES_FILENAME = "ProxySettingsPreferences";
	
	public static final String PREFERENCES_ACCEPTED_DISCLAIMER = "AcceptedDisclaimer";
	public static final String PREFERENCES_APPRATE_DONT_SHOW_AGAIN = "DontShowAgainAppRater";
    public static final String PREFERENCES_BETATEST_DONT_SHOW_AGAIN = "DontShowAgainBetaTest";

    public static final String PREFERENCES_APP_LAUNCH_COUNT = "LaunchCount";
    public static final String PREFERENCES_APP_DATE_FIRST_LAUNCH = "DateFirstLaunch";
	
	public static final String PREFERENCES_CACHED_URLS = "CachedUrls";
	
	public final static int   APPRATE_DAYS_UNTIL_PROMPT	 = 7;
	public final static int   APPRATE_LAUNCHES_UNTIL_PROMPT = 10;

    public final static int   BETATEST_DAYS_UNTIL_PROMPT	 = 14;
    public final static int   BETATEST_LAUNCHES_UNTIL_PROMPT = 50;
	
	/***************************************************************************
	 * Intent 
	 * */

	/* 
	 * Started Proxy Settings app
	 */
	public static final String PROXY_SETTINGS_STARTED = "com.lechucksoftware.proxy.proxysettings.PROXY_SETTINGS_STARTED";	
	
	/* 
	 * Started Proxy Settings app
	 */
	public static final String PROXY_SETTINGS_MANUAL_REFRESH = "com.lechucksoftware.proxy.proxysettings.PROXY_SETTINGS_MANUAL_REFRESH";	
	
	
	/*
	 * Call a Refresh of the UI after a proxy change
	 */
	public static final String PROXY_REFRESH_UI = "com.lechucksoftware.proxy.proxysettings.PROXY_REFRESH_UI";
	
	
	
	public enum DonwloadStatus
	{
		DOWNLOADING,
		STARTED,
		ERROR
	}

    public enum StatusFragmentStates
    {
        NONE,
        CONNECTED,
        CONNECT_TO,
        NOT_AVAILABLE,
        ENABLE_WIFI,
        GOTO_AVAILABLE_WIFI,
        CHECKING
    }
}
