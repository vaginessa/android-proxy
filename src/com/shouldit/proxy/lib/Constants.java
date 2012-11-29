package com.shouldit.proxy.lib;

public class Constants
{
	public static String ProxyStatus = "ProxyStatus"; 
	
	
	
	public enum ProxyStatusCodes
	{
		NOT_CHECKED,
		FOUND_PROBLEM_CHECKING,
		PROXY_ENABLED,
		PROXY_ADDRESS_VALID,
		PROXY_REACHABLE,
		WEB_REACHABILE,
		CONFIGURATION_OK
	}
	
	
	public enum ProxyStatusErrors
	{
		PROXY_NOT_ENABLED,
		PROXY_NOT_REACHABLE,
		PROXY_ADDRESS_NOT_VALID,
		WEB_NOT_REACHABLE,
		NO_ERRORS
	}
}
