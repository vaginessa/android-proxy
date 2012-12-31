package com.lechucksoftware.proxy.proxysettings;

import java.net.Proxy;

import android.app.Application;
import android.content.Context;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class ApplicationGlobals extends Application
{
	private static ApplicationGlobals mInstance;
	public ProxyConfiguration proxyConf;
	public ProxyCheckStatus proxyCheckStatus;
	public int timeout;
	public Context context;

	@Override
	public void onCreate()
	{
		super.onCreate();
		
		proxyCheckStatus = ProxyCheckStatus.CHECKING;
		timeout = 10000; // Set default timeout value (10 seconds)
		context = getApplicationContext();
		proxyConf = new ProxyConfiguration(context, Proxy.NO_PROXY, null, null, null);
		mInstance = this;
	}
		
	public static synchronized ApplicationGlobals getInstance()
	{
		return mInstance;
	}
}
