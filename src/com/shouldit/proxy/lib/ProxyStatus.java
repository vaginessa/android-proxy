package com.shouldit.proxy.lib;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import android.content.Context;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatus implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2657093750716229587L;

	SortedMap<ProxyStatusProperties, ProxyStatusItem> properties;
	public Date checkedDate;

	public String getCheckedDateString()
	{
		DateFormat df = DateFormat.getDateTimeInstance();
		return df.format(checkedDate);
	}

	public CheckStatusValues getCheckingStatus()
	{
		synchronized (this)
		{
			for (ProxyStatusItem prop : properties.values())
			{
				if (prop.status == CheckStatusValues.NOT_CHECKED)
					return CheckStatusValues.NOT_CHECKED;
			}

			for (ProxyStatusItem prop : properties.values())
			{
				if (prop.status == CheckStatusValues.CHECKING)
					return CheckStatusValues.CHECKING;
			}

			return CheckStatusValues.CHECKED;
		}
	}

	public ProxyStatusItem getProperty(ProxyStatusProperties property)
	{
		synchronized (this)
		{
			return properties.get(property);
		}
	}

	public ProxyStatus()
	{
		clear();
	}

	public void clear()
	{
		synchronized (this)
		{
			properties = new TreeMap<ProxyStatusProperties, ProxyStatusItem>(new ProxyStatusPropertiesComparator());

			properties.put(ProxyStatusProperties.WIFI_ENABLED, new ProxyStatusItem(ProxyStatusProperties.WIFI_ENABLED));
			properties.put(ProxyStatusProperties.PROXY_ENABLED, new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED));
			properties.put(ProxyStatusProperties.PROXY_VALID_HOSTNAME, new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_HOSTNAME));
			properties.put(ProxyStatusProperties.PROXY_VALID_PORT, new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_PORT));
			properties.put(ProxyStatusProperties.PROXY_REACHABLE, new ProxyStatusItem(ProxyStatusProperties.PROXY_REACHABLE));
			properties.put(ProxyStatusProperties.WEB_REACHABLE, new ProxyStatusItem(ProxyStatusProperties.WEB_REACHABLE));
		}
	}

	public void startchecking()
	{
		synchronized (this)
		{
			checkedDate = new Date();
			
			for (ProxyStatusItem prop : properties.values())
			{
				prop.status = CheckStatusValues.CHECKING;
			}
		}
	}

	public void add(ProxyStatusItem item)
	{
		synchronized (this)
		{
			properties.get(item.statusCode).status = item.status;
			properties.get(item.statusCode).result = item.result;
			properties.get(item.statusCode).checkedDate = new Date();
		}
	}

	public ProxyStatusItem getMostRelevantErrorProxyStatusItem()
	{
		synchronized (this)
		{
			for (ProxyStatusItem prop : properties.values())
			{
				if (prop.result == false)
				{
					return prop;
				}
			}

			return null;
		}
	}

	public Integer getErrorCount()
	{
		synchronized (this)
		{
			int count = 0;
			for (ProxyStatusItem prop : properties.values())
			{
				if (prop.result == false)
				{
					count++;
				}
			}

			return count;
		}
	}

	@Override
	public String toString()
	{
		synchronized (this)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Start checking at: " + checkedDate.toLocaleString() + "\n");

			for (ProxyStatusItem prop : properties.values())
			{
				sb.append(prop.toString() + "\n");
			}

			return sb.toString();
		}
	}
}
