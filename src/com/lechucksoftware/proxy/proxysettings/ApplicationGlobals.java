package com.lechucksoftware.proxy.proxysettings;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;

public class ApplicationGlobals extends Application
{
	private static ApplicationGlobals mInstance;

	private Map<String, ProxyConfiguration> configurations;

	public ProxyCheckStatus proxyCheckStatus;
	public int timeout;
	private WifiManager mWifiManager;
	private ConnectivityManager mConnManager;

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

		proxyCheckStatus = ProxyCheckStatus.CHECKING;
		timeout = 10000; // Set default timeout value (10 seconds)
		mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		mConnManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		configurations = new HashMap<String, ProxyConfiguration>();

		mInstance = this;
		
		
		LogWrapper.d(TAG, "Calling broadcast intent " + Constants.PROXY_SETTINGS_STARTED);
		sendBroadcast(new Intent(Constants.PROXY_SETTINGS_STARTED));
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
	
	public static void updateProxyConfigurationList()
	{
		// Get information regarding other configured AP
		List<ProxyConfiguration> confs = ProxySettings.getProxiesConfigurations(mInstance);
		List<ScanResult> scanResults = ApplicationGlobals.getWifiManager().getScanResults();
		
		for (ProxyConfiguration conf : confs)
		{
			ApplicationGlobals.addConfiguration(Utils.cleanUpSSID(conf.getSSID()), conf);
		}
		
		if (scanResults != null)
		{
			for (ScanResult res : scanResults)
			{
				
			}
		}
	}

	public static ProxyConfiguration getCurrentConfiguration()
	{
		ProxyConfiguration conf = null;

		if (mInstance.mWifiManager != null && mInstance.mWifiManager.isWifiEnabled())
		{
			WifiInfo info = mInstance.mWifiManager.getConnectionInfo();
			String SSID = Utils.cleanUpSSID(info.getSSID());

			if (mInstance.configurations.isEmpty())
				updateProxyConfigurationList();
			
			if (mInstance.configurations.containsKey(SSID))
			{
				conf = mInstance.configurations.get(SSID);
			}
		}
			
		if (conf == null)
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
