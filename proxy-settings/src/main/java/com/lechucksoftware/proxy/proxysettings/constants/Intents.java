package com.lechucksoftware.proxy.proxysettings.constants;

/**
 * Created by Marco on 11/02/14.
 */
public class Intents
{
    /**
     * Prefix for all intents created
     */
    public static final String INTENT_PREFIX = "com.lechucksoftware.proxy.proxysettings.";

    /**
     * Started Proxy Settings app
     */
    public static final String PROXY_SETTINGS_STARTED = INTENT_PREFIX + "PROXY_SETTINGS_STARTED";

    /**
     * Call a manual refresh of Proxy Settings app
     */
    public static final String PROXY_SETTINGS_MANUAL_REFRESH = INTENT_PREFIX + "PROXY_SETTINGS_MANUAL_REFRESH";

    /**
     * Updated a Wi-Fi access point configuration
     */
//    public static final String WIFI_AP_UPDATED = INTENT_PREFIX + "WIFI_AP_UPDATED";

    /**
     * Argument for the WIFI_AP_UPDATED intent that identify the updated Wi-Fi access point
     * */
//    public static final String UPDATED_WIFI = "UPDATED_WIFI";

    /**
     * Call a Refresh of the UI after a proxy change
     */
    public static final String PROXY_REFRESH_UI = INTENT_PREFIX + "PROXY_REFRESH_UI";

    /**
     * Saved a new proxy configuration on DB
     */
    public static final String PROXY_SAVED = INTENT_PREFIX + "PROXY_SAVED";

    /**
     * Receive information from a service for the user
     */
    public static final String SERVICE_COMUNICATION = INTENT_PREFIX + "SERVICE_COMUNICATION";
}
