package com.lechucksoftware.proxy.proxysettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class ApplicationGlobals extends Application
{
	private static ApplicationGlobals mInstance;
	
	private Map<String,ProxyConfiguration> configurations;
	
	public ProxyCheckStatus proxyCheckStatus;
	public int timeout;
	private WifiManager mWifiManager;

	public static WifiManager getWifiManager()
	{
		return mInstance.mWifiManager;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		
		proxyCheckStatus = ProxyCheckStatus.CHECKING;
		timeout = 10000; // Set default timeout value (10 seconds)
		mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		
		configurations = new HashMap<String, ProxyConfiguration>();
		
		mInstance = this;
	}
		
	public static synchronized ApplicationGlobals getInstance()
	{
		return mInstance;
	}
	
	public static void addConfiguration(String SSID, ProxyConfiguration conf)
	{
		if (mInstance.configurations.containsKey(SSID))
		{
			mInstance.configurations.remove(SSID);
		}

		mInstance.configurations.put(SSID, conf);	
	}
		
	public static ProxyConfiguration getCurrentConfiguration()
	{
		ProxyConfiguration conf = null;
		
		WifiInfo info = mInstance.mWifiManager.getConnectionInfo();
    	String SSID = Utils.cleanUpSSID(info.getSSID());
		
    	if (mInstance.configurations.containsKey(SSID))
    	{
    		conf = mInstance.configurations.get(SSID);
    	}
    	
		return conf; //TODO: do something if conf is empty configuration
	}
	
	public static List<ProxyConfiguration> getConfigurationsList()
	{
		return new ArrayList<ProxyConfiguration>(mInstance.configurations.values());
	}
}
