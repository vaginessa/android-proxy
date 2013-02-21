package com.shouldit.proxy.lib;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatusItem
{
	public ProxyStatusProperties statusCode;
	public CheckStatusValues status;
	public Boolean value;
	
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val)
	{
		statusCode = code;
		status = st;
		value = val;
	}
	
	public String toString()
	{
		return String.format("Property:%s, Status:%s, Result: %s", statusCode,status, value);
	}
}
