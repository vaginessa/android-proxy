package com.shouldit.proxy.lib;

import java.io.Serializable;

import com.shouldit.proxy.lib.Constants.ProxyStatusCodes;

public class ProxyStatus implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2657093750716229587L;
	
	Boolean checking;
	Boolean checked;
	Boolean enabled;
	Boolean valid_address;
	Boolean proxy_reachable;
	Boolean web_reachable;
	
	public Boolean getChecking()
	{
		return checking;
	}

	public Boolean getChecked()
	{
		return checked;
	}

	public Boolean getEnabled()
	{
		return enabled;
	}

	public Boolean getValid_address()
	{
		return valid_address;
	}

	public Boolean getProxy_reachable()
	{
		return proxy_reachable;
	}

	public Boolean getWeb_reachable()
	{
		return web_reachable;
	}

	public ProxyStatus()
	{
		clear();
	}

	public void clear()
	{
		checking = false;
		checked = false;
		enabled = false;
		valid_address = false;
		proxy_reachable = false;
		web_reachable = false;
	}
	
	public void add(ProxyStatusCodes statusCode, Boolean value)
	{
		switch(statusCode)
		{
			case PROXY_ENABLED:
				enabled = value;
				break;
				
			case PROXY_ADDRESS_VALID:
				valid_address = value;
				break;
				
			case PROXY_REACHABLE:
				proxy_reachable = value;
				break;
				
			case WEB_REACHABILE:
				web_reachable = value;
				break;
		}
	}
	

}
