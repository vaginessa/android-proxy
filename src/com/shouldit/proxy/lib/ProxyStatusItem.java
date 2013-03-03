package com.shouldit.proxy.lib;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatusItem
{
	public ProxyStatusProperties statusCode;
	public CheckStatusValues status;
	public Boolean result;
	public String message;
	
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val, String msg)
	{
		statusCode = code;
		status = st;
		result = val;
		message = msg;
	}
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val)
	{
		this(code,st,val,"");
	}
	
	public ProxyStatusItem(ProxyStatusProperties code)
	{
		this(code,CheckStatusValues.NOT_CHECKED, false, "");
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s (Status:%s, Result:%s", statusCode,status, result));
		if (message != null && message.length() > 0)
			sb.append(", Message: " + message);
		
		sb.append(")");
		
		return sb.toString();
	}
}
