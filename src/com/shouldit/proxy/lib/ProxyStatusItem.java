package com.shouldit.proxy.lib;

import java.util.Date;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatusItem
{
	public ProxyStatusProperties statusCode;
	public CheckStatusValues status;
	public Boolean result;
	public String message;
	public Date checkedDate;
	
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val, String msg, Date date)
	{
		statusCode = code;
		status = st;
		result = val;
		message = msg;
		checkedDate = date;
	}
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val, String msg)
	{
		this(code,st,val,"",new Date());
	}
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val)
	{
		this(code,st,val,"",new Date());
	}
	
	public ProxyStatusItem(ProxyStatusProperties code)
	{
		this(code,CheckStatusValues.NOT_CHECKED, false, "",null);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s (Status: %s, Result: %s, Checked at: %s", statusCode,status, result, checkedDate.toLocaleString()));
		if (message != null && message.length() > 0)
			sb.append(", Message: " + message);
		
		sb.append(")");
		
		return sb.toString();
	}
}
