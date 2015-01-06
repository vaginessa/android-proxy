package com.lechucksoftware.proxy.proxysettings.constants;

import android.net.Uri;

public class Constants
{
    public static final String PREFERENCES_FILENAME = "ProxySettingsPreferences";

    public static final String PREFERENCES_ACCEPTED_DISCLAIMER = "AcceptedDisclaimer";
    public static final String PREFERENCES_APPRATE_DONT_SHOW_AGAIN = "DontShowAgainAppRater";
    public static final String PREFERENCES_BETATEST_DONT_SHOW_AGAIN = "DontShowAgainBetaTest";

    public static final String PREFERENCES_APP_LAUNCH_COUNT = "LaunchCount";
    public static final String PREFERENCES_APP_DATE_FIRST_LAUNCH = "DateFirstLaunch";

    public static final String PREFERENCES_CACHED_URLS = "CachedUrls";

    public static final String PREFERENCES_DEMO_MODE = "DemoMode";

    public final static int APPRATE_DAYS_UNTIL_PROMPT = 7;
    public final static int APPRATE_LAUNCHES_UNTIL_PROMPT = 10;

    public final static int BETATEST_DAYS_UNTIL_PROMPT = 14;
    public final static int BETATEST_LAUNCHES_UNTIL_PROMPT = 50;

    // URL
    public static final String AMAZON_MARKET_PACKAGE = "com.amazon.venezia";
    public static final String PLAY_MARKET_PACKAGE = "com.android.vending";

    public static final Uri AMAZON_MARKET_URL = Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.lechucksoftware.proxy.proxysettings");
    public static final Uri PLAY_MARKET_URL = Uri.parse("market://details?id=com.lechucksoftware.proxy.proxysettings");


    // Arguments
    public static final String SELECTED_AP_CONF_ARG = "SELECTED_AP_CONF_ARG";
    public static final String SELECTED_PROXY_CONF_ARG = "SELECTED_PROXY_CONF_ARG";

    public static final String FRAGMENT_MODE_ARG = "FRAGMENT_MODE_ARG";
    public static final String WIFI_AP_NETWORK_ARG = "WIFI_AP_NETWORK_ARG";
}
