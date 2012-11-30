package com.shouldit.proxy.lib;

import java.io.Serializable;

import com.shouldit.proxy.lib.Constants.ProxyStatusCodes;
import com.shouldit.proxy.lib.Constants.ProxyStatusProperties;
import com.shouldit.proxy.lib.Constants.StatusValues;

public class ProxyStatus implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2657093750716229587L;
		
	ProxyStatusProperty enabled;
	ProxyStatusProperty valid_address;
	ProxyStatusProperty proxy_reachable;
	ProxyStatusProperty web_reachable;
	
	public ProxyStatusProperty getEnabled()
	{
		return enabled;
	}

	public ProxyStatusProperty getValid_address()
	{
		return valid_address;
	}

	public ProxyStatusProperty getProxy_reachable()
	{
		return proxy_reachable;
	}

	public ProxyStatusProperty getWeb_reachable()
	{
		return web_reachable;
	}

	public ProxyStatus()
	{
		clear();
	}

	public void clear()
	{
		enabled = new ProxyStatusProperty(ProxyStatusProperties.PROXY_ENABLED);
		valid_address = new ProxyStatusProperty(ProxyStatusProperties.PROXY_VALID_ADDRESS);
		proxy_reachable = new ProxyStatusProperty(ProxyStatusProperties.PROXY_REACHABLE);
		web_reachable = new ProxyStatusProperty(ProxyStatusProperties.WEB_REACHABLE);
	}
	
	public void startchecking()
	{
		enabled.status = StatusValues.CHECKING;
		valid_address.status = StatusValues.CHECKING;
		proxy_reachable.status = StatusValues.CHECKING;
		web_reachable.status = StatusValues.CHECKING;
	}
	
	public void add(ProxyStatusCodes statusCode, StatusValues status, Boolean value)
	{
		switch(statusCode)
		{
			case PROXY_ENABLED:
				enabled.status = status;
				enabled.result = value;
				break;
				
			case PROXY_ADDRESS_VALID:
				valid_address.status = status;
				valid_address.result = value;
				break;
				
			case PROXY_REACHABLE:
				proxy_reachable.status = status;
				proxy_reachable.result = value;
				break;
				
			case WEB_REACHABILE:
				web_reachable.status = status;
				web_reachable.result = value;
				break;
		}
	}
	

}
