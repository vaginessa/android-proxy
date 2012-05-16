package com.lechucksoftware.proxy.lib;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Build;

public class ProxyConfiguration
{
	public static final String TAG = "ProxyConfiguration";

	public WifiConfiguration wifiConfiguration;
	public NetworkInfo networkInfo;
	public Proxy proxyHost;
	public int deviceVersion;

	public ProxyConfiguration(Proxy proxy, NetworkInfo netInfo, WifiConfiguration wifiConf)
	{
		proxyHost = proxy;
		networkInfo = netInfo;
		wifiConfiguration = wifiConf;
		deviceVersion = Build.VERSION.SDK_INT;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(); 
		sb.append(String.format("Proxy: %s\n",proxyHost.toString()));
		sb.append(String.format("Is Proxy reachable: %B\n",isProxyReachable()));
		sb.append(String.format("Is WEB reachable: %B\n",isWebReachable()));
		
		if (networkInfo != null) sb.append(String.format("Network Info: %s\n", networkInfo));
		if (wifiConfiguration != null) sb.append(String.format("Wi-Fi Configuration Info: %s\n", wifiConfiguration));
		
		return sb.toString();
	}
	
	public Proxy.Type getConnectionType()
	{
		return proxyHost.type();
	}
	
	public Boolean isProxyEnabled()
	{
		if (proxyHost.type() == Type.DIRECT)
		{
			return false;
		}
		else
		{
			return true; // HTTP or SOCKS proxy
		}
	}
	
	public Boolean isProxyReachable()
    {
	    if (proxyHost != null && proxyHost.type() != Proxy.Type.DIRECT)
	        return ProxyUtils.isHostReachable(proxyHost);
	    else
	        return false;
    }
	
	public Boolean isWebReachable()
    {
        return ProxyUtils.isWebReachable(proxyHost);
    }
	
	public String getProxyHost()
	{
		InetSocketAddress proxyAddress = (InetSocketAddress) proxyHost.address();
		return proxyAddress.getHostName();
	}
	
	public Integer getProxyPort()
	{
		InetSocketAddress proxyAddress = (InetSocketAddress) proxyHost.address();
		return proxyAddress.getPort();
	}
	
	public String toShortString()
	{
		return String.format("%s",proxyHost.address().toString());
	}
	
	public int getNetworkType()
	{
		return networkInfo.getType();
	}
}