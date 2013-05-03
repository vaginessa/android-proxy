package com.shouldit.proxy.lib;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatus implements Serializable
{
	public static final String TAG = "ProxyConfiguration";
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
				if (prop.effective)
				{
					if (prop.status == CheckStatusValues.NOT_CHECKED)
						return CheckStatusValues.NOT_CHECKED;
				}
			}

			for (ProxyStatusItem prop : properties.values())
			{
				if (prop.effective)
				{
					if (prop.status == CheckStatusValues.CHECKING)
						return CheckStatusValues.CHECKING;
				}
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
			properties.put(ProxyStatusProperties.WEB_REACHABLE, new ProxyStatusItem(ProxyStatusProperties.WEB_REACHABLE));
			properties.put(ProxyStatusProperties.PROXY_ENABLED, new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED));
			properties.put(ProxyStatusProperties.PROXY_VALID_HOSTNAME, new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_HOSTNAME));
			properties.put(ProxyStatusProperties.PROXY_VALID_PORT, new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_PORT));
			properties.put(ProxyStatusProperties.PROXY_REACHABLE, new ProxyStatusItem(ProxyStatusProperties.PROXY_REACHABLE));
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

	public void set(ProxyStatusItem item)
	{
		set(item.statusCode, item.status, item.result, item.effective, item.message, new Date());
	}

	public void set(ProxyStatusProperties psp, CheckStatusValues stat, Boolean res, String msg, Date checkDate)
	{
		set(psp, stat, res, true, msg, checkDate);
	}
	
	public void set(ProxyStatusProperties psp, CheckStatusValues stat, boolean res, boolean effect)
	{
		set(psp, stat, res, true, "", new Date());
	}

	public void set(ProxyStatusProperties psp, CheckStatusValues stat, Boolean res, Boolean effect, String msg, Date checkDate)
	{
		synchronized (this)
		{
			if (properties.containsKey(psp))
			{
				properties.get(psp).status = stat;
				properties.get(psp).result = res;
				properties.get(psp).effective = effect;
				properties.get(psp).message = msg;
				properties.get(psp).checkedDate = checkDate;
			}
			else
			{
				LogWrapper.e(TAG, "Cannot find status code: " + psp);
			}
		}
	}

	public ProxyStatusItem getMostRelevantErrorProxyStatusItem()
	{
		synchronized (this)
		{
			for (ProxyStatusItem prop : properties.values())
			{
				if (prop.effective)
				{
					if (prop.result == false)
					{
						return prop;
					}
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
				if (prop.effective)
				{
					if (prop.result == false)
					{
						count++;
					}
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
