package com.lechucksoftware.proxy.lib;

import org.apache.http.HttpHost;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;

public class ProxyConfiguration
{
	public static final String TAG = "ProxyConfiguration";

	public WifiConfiguration wifiConfiguration;
	public NetworkInfo networkInfo;
	public HttpHost proxy;
	public String exclusionList;
	public boolean isValid;
	public int networkType;
	
	public static ProxyConfiguration GetVoidProxyConfiguration()
	{
		return new ProxyConfiguration();
	}
	
	public static ProxyConfiguration GetWifiProxyConfiguration(HttpHost pHost, String exList, WifiConfiguration conf)
	{
		return new ProxyConfiguration(pHost,exList,conf);
	}
	
	public static ProxyConfiguration GetMobileProxyConfiguration(HttpHost pHost, String exList, NetworkInfo conf)
	{
		return new ProxyConfiguration(pHost,exList,conf);
	}
	
	private ProxyConfiguration()
	{
		proxy = null;
		exclusionList = null;
		wifiConfiguration = null;
		networkInfo = null;
		isValid = false;
		networkType = -1;
	}
	
	private ProxyConfiguration(HttpHost pHost, String exList, WifiConfiguration conf)
	{
		proxy = pHost;
		exclusionList = exList;
		wifiConfiguration = conf;
		networkInfo = null;
		isValid = true;
		networkType = ConnectivityManager.TYPE_WIFI;
	}
	
	private ProxyConfiguration(HttpHost pHost, String exList, NetworkInfo netInfo)
	{
		proxy = pHost;
		exclusionList = exList;
		networkInfo = netInfo;
		wifiConfiguration = null;
		isValid = true;
		networkType = netInfo.getType();
	}
	
	@Override
	public String toString()
	{
		return String.format("Proxy: %s\nExclusion List: %s\nAP configuration: %s",proxy.toHostString(), exclusionList, wifiConfiguration.toString() );
	}
}
