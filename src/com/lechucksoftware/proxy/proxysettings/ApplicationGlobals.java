package com.lechucksoftware.proxy.proxysettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.bugsense.trace.BugSenseHandler;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;
import com.shouldit.proxy.lib.ProxyUtils;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;



public class ApplicationGlobals extends Application
{
	private static ApplicationGlobals mInstance;

	private Map<String, ProxyConfiguration> configurations;

	public int timeout;
	private WifiManager mWifiManager;
	private ConnectivityManager mConnManager;
	private ProxyConfiguration currentConfiguration;

	private static final String TAG = "ApplicationGlobals";

	public static WifiManager getWifiManager()
	{
		return mInstance.mWifiManager;
	}

	public static ConnectivityManager getConnectivityManager()
	{
		return mInstance.mConnManager;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		timeout = 10000; // Set default timeout value (10 seconds)
		mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		mConnManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		configurations = new HashMap<String, ProxyConfiguration>();

		mInstance = this;
				
		Utils.SetupBugSense(getApplicationContext());
				
		LogWrapper.d(TAG, "Calling broadcast intent " + Constants.PROXY_SETTINGS_STARTED);
		sendBroadcast(new Intent(Constants.PROXY_SETTINGS_STARTED));
	}

	public static synchronized ApplicationGlobals getInstance()
	{
		if (mInstance == null)
			BugSenseHandler.sendException(new Exception("Cannot find valid instance of ApplicationGlobals"));
		
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
	
	public static void updateProxyConfigurationList()
	{
		// Get information regarding other configured AP
		List<ProxyConfiguration> confs = ProxySettings.getProxiesConfigurations(mInstance);
		List<ScanResult> scanResults = getWifiManager().getScanResults();
		
		for (ProxyConfiguration conf : confs)
		{
			addConfiguration(ProxyUtils.cleanUpSSID(conf.getSSID()), conf);
		}
		
		if (scanResults != null)
		{
			for (ScanResult res : scanResults)
			{
				String currSSID = ProxyUtils.cleanUpSSID(res.SSID);
				if (mInstance.configurations.containsKey(currSSID))
				{
					mInstance.configurations.get(currSSID).ap.update(res);
				}
			}
		}
	}

	public static ProxyConfiguration getCurrentConfiguration()
	{
		ProxyConfiguration conf = null;

		if (mInstance.mWifiManager != null && mInstance.mWifiManager.isWifiEnabled())
		{
			WifiInfo info = mInstance.mWifiManager.getConnectionInfo();
			String SSID = ProxyUtils.cleanUpSSID(info.getSSID());

			if (mInstance.configurations.isEmpty())
				updateProxyConfigurationList();
			
			if (mInstance.configurations.containsKey(SSID))
			{
				conf = mInstance.configurations.get(SSID);
			}
			
			mInstance.currentConfiguration = conf;
		}
		
		// Always return a not null configuration
		if (mInstance.currentConfiguration == null)
		{
			mInstance.currentConfiguration = new ProxyConfiguration(mInstance.getApplicationContext(), ProxySetting.NONE, null, null, null, null);
		}
		
		return mInstance.currentConfiguration;
	}

	public static ProxyConfiguration getCachedConfiguration()
	{
		if (mInstance.currentConfiguration == null)
		{
			return getCurrentConfiguration();
		}
		
		return mInstance.currentConfiguration;
	}

	public static List<ProxyConfiguration> getConfigurationsList()
	{
		return new ArrayList<ProxyConfiguration>(mInstance.configurations.values());
	}
	
	public static ProxyConfiguration getConfiguration(String SSID)
	{
		String cleanSSID = ProxyUtils.cleanUpSSID(SSID);
		
		if (mInstance.configurations.containsKey(cleanSSID))
		{
			return mInstance.configurations.get(cleanSSID);
		}
		else return null;
	}

	public static void startWifiScan()
	{
		if (mInstance.mWifiManager != null && mInstance.mWifiManager.isWifiEnabled())
		{
			mInstance.mWifiManager.startScan();
		}
	}
}
