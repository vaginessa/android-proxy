package com.lechucksoftware.proxy.proxysettings;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class ApplicationGlobals extends Application
{
	private static ApplicationGlobals mInstance;

	private Map<String, ProxyConfiguration> configurations;

	public ProxyCheckStatus proxyCheckStatus;
	public int timeout;
	private WifiManager mWifiManager;
	private ConnectivityManager mConnManager;

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

		proxyCheckStatus = ProxyCheckStatus.CHECKING;
		timeout = 10000; // Set default timeout value (10 seconds)
		mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		mConnManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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

		if (mInstance.mWifiManager != null && mInstance.mWifiManager.isWifiEnabled())
		{
			WifiInfo info = mInstance.mWifiManager.getConnectionInfo();
			String SSID = Utils.cleanUpSSID(info.getSSID());

			if (mInstance.configurations.containsKey(SSID))
			{
				conf = mInstance.configurations.get(SSID);
			}
		}
		else
		{
			NetworkInfo activeNetInfo = mInstance.mConnManager.getActiveNetworkInfo();
			conf = new ProxyConfiguration(mInstance.getApplicationContext(), Proxy.NO_PROXY, null, activeNetInfo, null);
		}

		return conf;
	}

	public static List<ProxyConfiguration> getConfigurationsList()
	{
		return new ArrayList<ProxyConfiguration>(mInstance.configurations.values());
	}

	public static void startWifiScan()
	{
		if (mInstance.mWifiManager != null && mInstance.mWifiManager.isWifiEnabled())
		{
			mInstance.mWifiManager.startScan();
		}
	}
}
