package com.lechucksoftware.proxy.proxysettings;

import java.net.Proxy;

import android.content.Context;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class Globals
{
	private static Globals mInstance = null;

	public ProxyConfiguration proxyConf;
	public ProxyCheckStatus proxyCheckStatus;
	public int timeout;
	public Context context;

	protected Globals()
	{
		proxyCheckStatus = ProxyCheckStatus.CHECKING;
		timeout = 10000; // Set default timeout value (10 seconds)
	}
	
	public void addApplicationContext(Context ctx)
	{
		context = ctx;
		proxyConf = new ProxyConfiguration(ctx, Proxy.NO_PROXY, null, null, null);
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
