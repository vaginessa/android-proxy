package com.lechucksoftware.proxy.proxysettings;

import java.net.Proxy;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class Globals
{
	private static Globals mInstance = null;

	public ProxyConfiguration proxyConf;
	public ProxyCheckStatus proxyCheckStatus;
	public int timeout;

	protected Globals()
	{
		proxyConf = new ProxyConfiguration(Proxy.NO_PROXY, null, null);
		proxyCheckStatus = ProxyCheckStatus.CHECKING;
		timeout = 60000; // Set default timeout value
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
