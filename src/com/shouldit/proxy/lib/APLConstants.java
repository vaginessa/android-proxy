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

	//	public enum ProxyStatusCodes
	//	{
	//		NOT_CHECKED,
	//		FOUND_PROBLEM_CHECKING,
	//		PROXY_ENABLED,
	//		PROXY_ADDRESS_VALID,
	//		PROXY_REACHABLE,
	//		WEB_REACHABILE,
	//		CONFIGURATION_OK
	//	}

	//	public enum ProxyStatusErrors
	//	{
	//		PROXY_NOT_ENABLED,
	//		PROXY_NOT_REACHABLE,
	//		PROXY_HOSTNAME_NOT_VALID,
	//		PROXY_PORT_NOT_VALID,
	//		WEB_NOT_REACHABLE,
	//		NO_ERRORS
	//	}

	public enum ProxyStatusProperties
	{
		PROXY_ENABLED(0), 
		WEB_REACHABLE(1), 
		PROXY_VALID_HOSTNAME(2), 
		PROXY_VALID_PORT(3), 
		PROXY_REACHABLE(4);

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
