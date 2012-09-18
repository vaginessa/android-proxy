package com.lechucksoftware.proxy.proxysettings;

import com.shouldit.proxy.lib.ProxyConfiguration;

public class Globals
{
	private static Globals mInstance = null;

	public ProxyConfiguration proxyConf;

	protected Globals()
	{
		
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
