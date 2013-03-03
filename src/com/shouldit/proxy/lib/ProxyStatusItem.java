package com.shouldit.proxy.lib;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatusItem
{
	public ProxyStatusProperties statusCode;
	public CheckStatusValues status;
	public Boolean value;
	public String message;
	
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val, String msg)
	{
		statusCode = code;
		status = st;
		value = val;
		message = msg;
	}
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val)
	{
		this(code,st,val,"");
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s (Status:%s, Result:%s", statusCode,status, value));
		if (message != null && message.length() > 0)
			sb.append(", Message: " + message);
		
		sb.append(")");
		
		return sb.toString();
	}
}
