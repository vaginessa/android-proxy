package com.shouldit.proxy.lib;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatus implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2657093750716229587L;
	
	SortedMap<ProxyStatusProperties,ProxyStatusProperty> properties;
	
	
	public CheckStatusValues getCheckingStatus()
	{
		for (ProxyStatusProperty prop : properties.values())
		{
			if (prop.status == CheckStatusValues.NOT_CHECKED)
				return CheckStatusValues.NOT_CHECKED;
		}
		
		for (ProxyStatusProperty prop : properties.values())
		{
			if (prop.status == CheckStatusValues.CHECKING)
				return CheckStatusValues.CHECKING;
		}
		
		return CheckStatusValues.CHECKED;
	}
	
	public ProxyStatusProperty getEnabled()
	{
		return properties.get(ProxyStatusProperties.PROXY_ENABLED);
	}

	public ProxyStatusProperty getValid_hostname()
	{
		return properties.get(ProxyStatusProperties.PROXY_VALID_HOSTNAME);
	}
	
	public ProxyStatusProperty getValid_port()
	{
		return properties.get(ProxyStatusProperties.PROXY_VALID_PORT);
	}

	public ProxyStatusProperty getProxy_reachable()
	{
		return properties.get(ProxyStatusProperties.PROXY_REACHABLE);
	}

	public ProxyStatusProperty getWeb_reachable()
	{
		return properties.get(ProxyStatusProperties.WEB_REACHABLE);
	}

	public ProxyStatus()
	{
		clear();
	}

	public void clear()
	{
		properties = new TreeMap<ProxyStatusProperties, ProxyStatusProperty>(new ProxyStatusPropertiesComparator());
		
		properties.put(ProxyStatusProperties.PROXY_ENABLED, new ProxyStatusProperty(ProxyStatusProperties.PROXY_ENABLED));
		properties.put(ProxyStatusProperties.PROXY_VALID_HOSTNAME, new ProxyStatusProperty(ProxyStatusProperties.PROXY_VALID_HOSTNAME));
		properties.put(ProxyStatusProperties.PROXY_VALID_PORT, new ProxyStatusProperty(ProxyStatusProperties.PROXY_VALID_PORT));
		properties.put(ProxyStatusProperties.PROXY_REACHABLE, new ProxyStatusProperty(ProxyStatusProperties.PROXY_REACHABLE));
		properties.put(ProxyStatusProperties.WEB_REACHABLE, new ProxyStatusProperty(ProxyStatusProperties.WEB_REACHABLE));
	}
	
	public void startchecking()
	{
		for (ProxyStatusProperty prop : properties.values())
		{
			prop.status = CheckStatusValues.CHECKING;
		}
	}
	
	public void add(ProxyStatusProperties statusCode, CheckStatusValues status, Boolean value)
	{
		properties.get(statusCode).status = status;
		properties.get(statusCode).result = value;
	}
	
	public ProxyStatusProperty getMostRelevantErrorProxyStatusProperty()
	{
		for (ProxyStatusProperty prop : properties.values())
		{
			if (prop.result == false)
			{
				return prop;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for (ProxyStatusProperty prop : properties.values())
		{
			sb.append(prop.status + " - " + prop.propertyName + ": " + prop.result + "\n");
		}
		
		return sb.toString();
	}
	

}
