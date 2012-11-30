package com.shouldit.proxy.lib;

import java.io.Serializable;

import com.shouldit.proxy.lib.Constants.ProxyStatusProperties;
import com.shouldit.proxy.lib.Constants.StatusValues;

public class ProxyStatusProperty implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3857385153670591196L;
	
	public ProxyStatusProperties propertyName;
	public StatusValues status;
	public Boolean result;
	
	public ProxyStatusProperty(ProxyStatusProperties name)
	{
		propertyName = name;
		status = StatusValues.NOT_CHECKED;
		result = false;
	}
}
