package com.shouldit.proxy.lib;

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
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public enum SecurityType
    {
        SECURITY_NONE(0),
        SECURITY_WEP(1),
        SECURITY_PSK(2),
        SECURITY_EAP(3);

        private final Integer value;

        SecurityType(int index)
        {
            this.value = index;
        }

        public Integer getValue()
        {
            return value;
        }
    }

    public enum PskType
    {
        UNKNOWN, WPA, WPA2, WPA_WPA2
    }

    public enum ProxyStatusProperties
    {
        WIFI_ENABLED(0),
        WIFI_SELECTED(1),
        PROXY_ENABLED(2),
        WEB_REACHABLE(3),
        PROXY_VALID_HOSTNAME(4),
        PROXY_VALID_PORT(5),
        PROXY_REACHABLE(6);

        private final Integer priority;

        ProxyStatusProperties(int index)
        {
            this.priority = index;
        }

        public Integer getPriority()
        {
            return priority;
        }
    }

    public enum CheckStatusValues
    {
        NOT_CHECKED,
        CHECKING,
        CHECKED;

        @Override
        public String toString()
        {
            switch (this)
            {
                case NOT_CHECKED:
                    return "N";
                case CHECKING:
                    return "?";
                case CHECKED:
                    return "C";
            }

            return "?";
        }
    }
}
