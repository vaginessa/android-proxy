package com.lechucksoftware.proxy.proxysettings.constants;

import android.net.Uri;

import java.net.URI;

public class Constants
{
    public static final String PREFERENCES_FILENAME = "ProxySettingsPreferences";

    public static final String PREFERENCES_ACCEPTED_DISCLAIMER = "AcceptedDisclaimer";
    public static final String PREFERENCES_APPRATE_DONT_SHOW_AGAIN = "DontShowAgainAppRater";
    public static final String PREFERENCES_BETATEST_DONT_SHOW_AGAIN = "DontShowAgainBetaTest";

    public static final String PREFERENCES_APP_LAUNCH_COUNT = "LaunchCount";
    public static final String PREFERENCES_APP_DATE_FIRST_LAUNCH = "DateFirstLaunch";

    public static final String PREFERENCES_CACHED_URLS = "CachedUrls";

    public final static int APPRATE_DAYS_UNTIL_PROMPT = 7;
    public final static int APPRATE_LAUNCHES_UNTIL_PROMPT = 10;

    public final static int BETATEST_DAYS_UNTIL_PROMPT = 14;
    public final static int BETATEST_LAUNCHES_UNTIL_PROMPT = 50;

    // Extra arguments
    public static final String SELECTED_AP_CONF_ARG = "SELECTED_AP_CONF_ARG";


    public static final String AMAZON_MARKET_PACKAGE = "com.amazon.venezia";
    public static final String PLAY_MARKET_PACKAGE = "com.android.vending";

    public static final Uri AMAZON_MARKET_URL = Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.lechucksoftware.proxy.proxysettings");
    public static final Uri PLAY_MARKET_URL = Uri.parse("market://details?id=com.lechucksoftware.proxy.proxysettings");

    /**
     * ************************************************************************
     * Intent
     */

	/* 
     * Started Proxy Settings app
	 */
    public static final String PROXY_SETTINGS_STARTED = "com.lechucksoftware.proxy.proxysettings.PROXY_SETTINGS_STARTED";

    /*
	 * Call a manual refresh of Proxy Settings app
	 */
	public static final String PROXY_SETTINGS_MANUAL_REFRESH = "com.lechucksoftware.proxy.proxysettings.PROXY_SETTINGS_MANUAL_REFRESH";


	/*
     * Call a Refresh of the UI after a proxy change
     */
    public static final String PROXY_REFRESH_UI = "com.lechucksoftware.proxy.proxysettings.PROXY_REFRESH_UI";

    /*
	 * Saved a new proxy configuration on DB
	 */
    public static final String PROXY_SAVED = "com.lechucksoftware.proxy.proxysettings.PROXY_SAVED";
}
