package com.shouldit.proxy.lib;

import java.util.Comparator;

public class APLConstants
{
	/**
	 * Broadcasted intent when updates on the proxy status are available
	 * */
	public static final String APL_UPDATED_PROXY_STATUS_CHECK = "com.shouldit.proxy.lib.PROXY_CHECK_STATUS_UPDATE";

	/**
	 * Broadcasted intent when a proxy configuration is written on the device
	 * */
	public static final String APL_UPDATED_PROXY_CONFIGURATION = "com.shouldit.proxy.lib.PROXY_CONFIGURATION_UPDATED";

	public static final String ProxyStatus = "ProxyStatus";

	public enum ProxyStatusProperties
	{
		WIFI_ENABLED(0),
		PROXY_ENABLED(1), 
		WEB_REACHABLE(2), 
		PROXY_VALID_HOSTNAME(3), 
		PROXY_VALID_PORT(4), 
		PROXY_REACHABLE(5);

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
		NOT_CHECKED, CHECKING, CHECKED
	}
}
