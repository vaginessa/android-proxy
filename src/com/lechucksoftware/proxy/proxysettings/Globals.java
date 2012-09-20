package com.lechucksoftware.proxy.proxysettings;

import java.net.Proxy;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.Constants.ProxyStatus;

public class Globals
{
	private static Globals mInstance = null;

	public ProxyConfiguration proxyConf;
	public ProxyCheckStatus proxyCheckStatus;

	protected Globals()
	{
		proxyConf = new ProxyConfiguration(Proxy.NO_PROXY, null, null);
		proxyConf.status = ProxyStatus.NOT_CHECKED;
		proxyCheckStatus = ProxyCheckStatus.CHECKING;		
	}

	public static synchronized Globals getInstance()
	{
		if (null == mInstance)
		{
			mInstance = new Globals();
		}
		
		return mInstance;
	}
}
