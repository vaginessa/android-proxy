package com.lechucksoftware.proxy.lib;

import java.net.Proxy;

import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;

public class ProxyConfiguration
{
	public static final String TAG = "ProxyConfiguration";

	public WifiConfiguration wifiConfiguration;
	public NetworkInfo networkInfo;
	public Proxy proxyHost;
	public String exclusionList;

	public ProxyConfiguration(Proxy proxy, String exList, NetworkInfo netInfo, WifiConfiguration wifiConf)
	{
		proxyHost = proxy;
		exclusionList = exList;
		networkInfo = netInfo;
		wifiConfiguration = wifiConf;
	}
	
	@Override
	public String toString()
	{
		return String.format("Proxy: %s\nExclusion List: %s",proxyHost.toString(), exclusionList);
	}
}
