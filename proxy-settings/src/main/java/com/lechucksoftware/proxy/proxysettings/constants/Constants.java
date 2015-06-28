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
    public static final String SELECTED_PROXY_TYPE_ARG = "SELECTED_PROXY_TYPE_ARG";
    public static final String SELECTED_PROXY_CONF_ARG = "SELECTED_PROXY_CONF_ARG";
    public static final String SELECTED_PAC_CONF_ARG = "SELECTED_PAC_CONF_ARG";

    public static final String FRAGMENT_MODE_ARG = "FRAGMENT_MODE_ARG";
    public static final String WIFI_AP_NETWORK_ARG = "WIFI_AP_NETWORK_ARG";

    public static final String SERVICE_COMUNICATION_TITLE = "SERVICE_COMUNICATION_TITLE";
    public static final String SERVICE_COMUNICATION_MESSAGE = "SERVICE_COMUNICATION_MESSAGE";
    public static final String SERVICE_COMUNICATION_CLOSE_ACTIVITY = "SERVICE_COMUNICATION_CLOSE_ACTIVITY";

    // IAB SKUs
    public static final String IAB_ITEM_SKU_BASE = "be.shouldit.proxy.billing.base";
    public static final String IAB_ITEM_SKU_PRO = "be.shouldit.proxy.billing.pro";
    public static final String IAB_ITEM_SKU_NINJA = "be.shouldit.proxy.billing.ninja";

    public static final String IAB_ITEM_SKU_DONATION_0_99 = "be.shouldit.proxy.billing.donation.0.99";
    public static final String IAB_ITEM_SKU_DONATION_1_99 = "be.shouldit.proxy.billing.donation.1.99";
    public static final String IAB_ITEM_SKU_DONATION_2_99 = "be.shouldit.proxy.billing.donation.2.99";
    public static final String IAB_ITEM_SKU_DONATION_5_99 = "be.shouldit.proxy.billing.donation.5.99";
    public static final String IAB_ITEM_SKU_DONATION_9_99 = "be.shouldit.proxy.billing.donation.9.99";

    public static final String IAB_ITEM_SKU_TEST_PURCHASED = "android.test.purchased"; // Buying this item will cause a successful purchase response.
    public static final String IAB_ITEM_SKU_TEST_CANCELED = "android.test.canceled"; // Buying this item will act as if the user had canceled the purchase.
    public static final String IAB_ITEM_SKU_TEST_REFUNDED = "android.test.refunded"; // Buying this item will act as if the purchase was refunded.
    public static final String IAB_ITEM_SKU_TEST_UNAVAILABLE = "android.test.item_unavailable"; // Buying this item will act as if this item was not added to the Google Developer Console for your game.

    public static final String [] IAB_AVAILABLE_ITEMS =
            {
                    IAB_ITEM_SKU_DONATION_0_99,
                    IAB_ITEM_SKU_DONATION_1_99,
                    IAB_ITEM_SKU_DONATION_2_99,
                    IAB_ITEM_SKU_DONATION_5_99,
                    IAB_ITEM_SKU_DONATION_9_99
            };

    public static final String [] IAB_DEBUG_ITEMS =
            {
                    IAB_ITEM_SKU_TEST_PURCHASED,
                    IAB_ITEM_SKU_TEST_CANCELED,
                    IAB_ITEM_SKU_TEST_REFUNDED,
                    IAB_ITEM_SKU_TEST_UNAVAILABLE
            };
}
