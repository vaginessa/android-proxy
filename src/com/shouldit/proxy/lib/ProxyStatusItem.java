package com.shouldit.proxy.lib;

import java.util.Date;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatusItem
{
	public ProxyStatusProperties statusCode;
	public CheckStatusValues status;
	public Boolean result;
	public Boolean effective;
	public String message;
	public Date checkedDate;
	
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val, Boolean eff, String msg, Date date)
	{
		statusCode = code;
		status = st;
		result = val;
		effective = eff;
		message = msg;
		checkedDate = date;
	}
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean val, Boolean eff, String msg)
	{
		this(code,st,val,eff,msg,null);
	}
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues st, Boolean eff, Boolean val)
	{
		this(code,st,val,eff, "",null);
	}
	
	public ProxyStatusItem(ProxyStatusProperties code)
	{
		this(code,CheckStatusValues.NOT_CHECKED, false, true, "",null);
	}
	
	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues checked, boolean res, String msg)
	{
		this(code,checked, res, true, msg,null);
	}

	public ProxyStatusItem(ProxyStatusProperties code, CheckStatusValues checked, boolean res)
	{
		this(code,checked, res, true, "",null);
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s (Effective: %s, Status: %s, Result: %s, Checked at: %s", statusCode, effective, status, result, checkedDate.toLocaleString()));
		if (message != null && message.length() > 0)
			sb.append(", Message: " + message);
		
		sb.append(")");
		
		return sb.toString();
	}
}
