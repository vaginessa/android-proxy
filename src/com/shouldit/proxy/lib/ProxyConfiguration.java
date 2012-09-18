package com.shouldit.proxy.lib;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.apache.http.conn.util.InetAddressUtils;

import com.shouldit.proxy.lib.Constants.ProxyStatus;

import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.webkit.URLUtil;

public class ProxyConfiguration
{	
	public static final String TAG = "ProxyConfiguration";

	public ProxyStatus status;
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
		status = ProxyStatus.NOT_CHECKED;
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
	
	/**
	 * Can take a long time to execute this task.
	 * - Check if the proxy is enabled
	 * - Check if the proxy address is valid
	 * - Check if the proxy is reachable (using a PING)
	 * - Check if is possible to retrieve an URI resource using the proxy 
	 * */
	public void acquireProxyStatus()
	{
		if (!isProxyEnabled())
		{
			status = ProxyStatus.PROXY_NOT_ENABLED;
			return;
		}
		
		if (!isProxyValidAddress())
		{
			status = ProxyStatus.PROXY_INVALID_ADDRESS;
			return;
		}
		
		if (!isProxyReachable())
		{
			status = ProxyStatus.PROXY_NOT_REACHABLE;
			return;
		}
		
		if (!isWebReachable())
		{
			status = ProxyStatus.WEB_NOT_REACHABLE;
			return;
		}
		
		status = ProxyStatus.OK;
	}

	public ProxyStatus getProxyStatus()
	{
		return status;
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
	
	private boolean isProxyValidAddress()
	{
		String proxyHost = getProxyHost();
		
		if (InetAddressUtils.isIPv4Address(proxyHost) || 
			InetAddressUtils.isIPv6Address(proxyHost) || 
			InetAddressUtils.isIPv6HexCompressedAddress(proxyHost) || 
			InetAddressUtils.isIPv6StdAddress(proxyHost))
		{
			return true;
		}
		
		if (URLUtil.isNetworkUrl(proxyHost))
		{
			return true;
		}

		return false;
	}
	
	/**
	 * Try to PING the HOST specified in the current proxy configuration
	 * */ 
	public Boolean isProxyReachable()
    {
	    if (proxyHost != null && proxyHost.type() != Proxy.Type.DIRECT)
	        return ProxyUtils.isHostReachable(proxyHost);
	    else
	        return false;
    }
	
	/**
	 * Try to download a webpage using the current proxy configuration
	 * */
	public Boolean isWebReachable()
    {
        return ProxyUtils.isWebReachable(proxyHost);
    }
	
	public String getProxyHost()
	{
		InetSocketAddress proxyAddress = (InetSocketAddress) proxyHost.address();
		return proxyAddress.getHostName();
	}
	
	public String getProxyIPHost()
	{
	    return ((InetSocketAddress) proxyHost.address()).getAddress().getHostAddress();
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

	public String toShortIPString()
    {
        return String.format("%s:%d",getProxyIPHost(),getProxyPort());
    }
	
	
	public int getNetworkType()
	{
		return networkInfo.getType();
	}
}