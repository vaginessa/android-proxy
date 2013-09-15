package com.shouldit.proxy.lib;

import java.util.EnumSet;

public class APLConstants
{
    /**
     * Broadcasted intent when updates on the proxy status are available
     */
    public static final String APL_UPDATED_PROXY_STATUS_CHECK = "com.shouldit.proxy.lib.PROXY_CHECK_STATUS_UPDATE";

    /**
     * Broadcasted intent when a proxy configuration is written on the device
     */
    public static final String APL_UPDATED_PROXY_CONFIGURATION = "com.shouldit.proxy.lib.PROXY_CONFIGURATION_UPDATED";

    public static final String ProxyStatus = "ProxyStatus";

    /**
     * Try to download a webpage using the current proxy configuration
     */
    public static final Integer DEFAULT_TIMEOUT = 10000; // 10 seconds

}
