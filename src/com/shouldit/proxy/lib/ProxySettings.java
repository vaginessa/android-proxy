package com.shouldit.proxy.lib;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.Proxy.Type;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Main class that contains utilities for getting the proxy configuration of the
 * current or the all configured networks
 * */
public class ProxySettings
{
	public static final String TAG = "ProxySettings";

	/**
	 * Main entry point to access the proxy settings
	 * */
	public static ProxyConfiguration getCurrentProxyConfiguration(Context ctx, URI uri) throws Exception
	{
		ProxyConfiguration proxyConfig;

		if (Build.VERSION.SDK_INT >= 12) // Honeycomb 3.1
		{
			proxyConfig = getProxySelectorConfiguration(ctx, uri);
		}
		else
		{
			proxyConfig = getGlobalProxy(ctx);
		}

		/**
		 * Set direct connection if no proxyConfig received
		 * */
		if (proxyConfig == null)
		{
			proxyConfig = new ProxyConfiguration(ctx, Proxy.NO_PROXY, null, null, null);
		}

		/**
		 * Add connection informations
		 * */
		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		proxyConfig.networkInfo = activeNetInfo;

		if (activeNetInfo != null)
		{
			switch (activeNetInfo.getType())
			{
				case ConnectivityManager.TYPE_WIFI:
					WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
					for (WifiConfiguration wc : wifiConfigurations)
					{
						if (wc.networkId == wifiInfo.getNetworkId())
						{
							proxyConfig.wifiConfiguration = wc;
							break;
						}
					}
					break;
				case ConnectivityManager.TYPE_MOBILE:
					break;
				default:
					throw new UnsupportedOperationException("Not yet implemented support for" + activeNetInfo.getTypeName() + " network type");
			}
		}

		return proxyConfig;
	}

	/**
	 * For API >= 12: Returns the current proxy configuration based on the URI,
	 * this implementation is a wrapper of the Android's ProxySelector class.
	 * Just add some other informations that can be useful to the developer.
	 * */
	public static ProxyConfiguration getProxySelectorConfiguration(Context ctx, URI uri) throws Exception
	{
		ProxySelector defaultProxySelector = ProxySelector.getDefault();

		Proxy proxy = null;

		List<Proxy> proxyList = defaultProxySelector.select(uri);
		if (proxyList.size() > 0)
		{
			proxy = proxyList.get(0);
			Log.d(TAG, "Current Proxy Configuration: " + proxy.toString());
		}
		else
			throw new Exception("Not found valid proxy configuration!");

		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		ProxyConfiguration proxyConfig = new ProxyConfiguration(ctx, proxy, proxy.toString(), activeNetInfo, null);

		return proxyConfig;
	}

	/**
	 * Return the current proxy configuration for HTTP protocol
	 * */
	public static ProxyConfiguration getCurrentHttpProxyConfiguration(Context ctx) throws Exception
	{
		URI uri = new URI("http", "wwww.google.it", null, null);
		return getCurrentProxyConfiguration(ctx, uri);
	}

	/**
	 * Return the current proxy configuration for HTTPS protocol
	 * */
	public static ProxyConfiguration getCurrentHttpsProxyConfiguration(Context ctx) throws Exception
	{
		URI uri = new URI("https", "wwww.google.it", null, null);
		return getCurrentProxyConfiguration(ctx, uri);
	}

	/**
	 * Return the current proxy configuration for FTP protocol
	 * */
	public static ProxyConfiguration getCurrentFtpProxyConfiguration(Context ctx) throws Exception
	{
		URI uri = new URI("ftp", "google.it", null, null);
		return getCurrentProxyConfiguration(ctx, uri);
	}

	/**
	 * For API < 12: Get global proxy configuration.
	 * */
	private static ProxyConfiguration getGlobalProxy(Context ctx)
	{
		ProxyConfiguration proxyConfig = null;

		ContentResolver contentResolver = ctx.getContentResolver();
		String proxyString = Settings.Secure.getString(contentResolver, Settings.Secure.HTTP_PROXY);

		if (proxyString != null && proxyString != "" && proxyString.contains(":"))
		{
			String[] proxyParts = proxyString.split(":");
			if (proxyParts.length == 2)
			{
				String proxyAddress = proxyParts[0];
				try
				{
					Integer proxyPort = Integer.parseInt(proxyParts[1]);
					Proxy p = new Proxy(Type.HTTP, new InetSocketAddress(proxyAddress, proxyPort));
					proxyConfig = new ProxyConfiguration(ctx, p, proxyString, null, null);
					// Log.d(TAG, "ProxyHost created: " +
					// proxyConfig.toString());
				}
				catch (NumberFormatException e)
				{
					Log.d(TAG, "Port is not a number: " + proxyParts[1]);
				}
			}
		}

		return proxyConfig;
	}
}
