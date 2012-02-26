package com.lechucksoftware.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.Proxy.Type;
import java.util.ArrayList;
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

import com.lechucksoftware.proxy.lib.reflection.ReflectionUtils;
import com.lechucksoftware.proxy.lib.reflection.android.RProxySettings;

/**
 * Main class that contains utilities for getting the proxy configuration of the
 * current or the all configured networks
 * */
public class ProxySettings
{
	public static final String TAG = "ProxySettings";

	public static ProxyConfiguration getCurrentProxyConfiguration(Context ctx, URI uri) throws Exception
    {
		ProxyConfiguration proxyConfig;
		
      	if (Build.VERSION.SDK_INT >= 11) 
    	{
      		proxyConfig = getProxySelectorConfiguration(ctx, uri);
		}
    	else
    	{
    		proxyConfig = getGlobalProxy(ctx);
    	}
      	
      	
      	/**
      	 * Add a direct connection if no proxyConfig received
      	 * */
      	if (proxyConfig == null) 
		{
			proxyConfig = new ProxyConfiguration(Proxy.NO_PROXY,"",null,null);
		} 
      	
      	/**
      	 * Add some connection informations
      	 * */
		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		proxyConfig.networkInfo = activeNetInfo;
		
		switch (activeNetInfo.getType())
		{
			case ConnectivityManager.TYPE_WIFI:
				WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
				for(WifiConfiguration wc:wifiConfigurations)
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
		
		return proxyConfig;
    }

	/**
	 * Returns the current proxy configuration based on the URI, this
	 * implementation is a wrapper of the Android's ProxySelector class. Just
	 * add some other informations that can be useful to the developer.
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
		else throw new Exception("Not found valid proxy configuration!");

		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		ProxyConfiguration proxyConfig = new ProxyConfiguration(proxy, "", activeNetInfo, null);

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

	public static ProxyConfiguration getProxyConfiguration(Context ctx, WifiConfiguration wifiConf)
	{
		if (Build.VERSION.SDK_INT >= 11)
		{
			return getProxySdk11(ctx, wifiConf);
		}
		else
		{
			// Same configuration for every AP
			// :(
			throw new UnsupportedOperationException("Proxy not defined for each AP");
		}
	}

	public static List<ProxyConfiguration> getProxiesConfigurations(Context ctx)
	{
		List<ProxyConfiguration> proxyHosts = new ArrayList<ProxyConfiguration>();
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

		/**
		 *  Just for testing on the Emulator 
		 *  */
		if (Build.PRODUCT.equals("sdk") && configuredNetworks.size() == 0)
		{
			WifiConfiguration fakeWifiConf = new WifiConfiguration();
			fakeWifiConf.SSID = "Fake_SDK_WI-FI";
			configuredNetworks.add(fakeWifiConf);
		}

		for (WifiConfiguration wifiConf : configuredNetworks)
		{
			proxyHosts.add(getProxyConfiguration(ctx, wifiConf));
		}

		return proxyHosts;
	}

	public static ProxyConfiguration getProxySdk11(Context ctx, WifiConfiguration wifiConf)
	{
		ProxyConfiguration proxyHost = null;

		try
		{
			Field proxySettingsField = wifiConf.getClass().getField("proxySettings");
			Object proxySettings = proxySettingsField.get(wifiConf);

			int ordinal = ((Enum) proxySettings).ordinal();

			if (ordinal == RProxySettings.NONE.ordinal() || ordinal == RProxySettings.UNASSIGNED.ordinal())
			{
				proxyHost = new ProxyConfiguration(null, null, null, wifiConf);
			}
			else
			{

				Field linkPropertiesField = wifiConf.getClass().getField("linkProperties");
				Object linkProperties = linkPropertiesField.get(wifiConf);
				Field mHttpProxyField = ReflectionUtils.getField(linkProperties.getClass().getDeclaredFields(), "mHttpProxy");
				mHttpProxyField.setAccessible(true);
				Object mHttpProxy = mHttpProxyField.get(linkProperties);

				/* Just for testing on the Emulator */
				if (Build.PRODUCT.equals("sdk") && mHttpProxy == null)
				{
					Class ProxyPropertiesClass = mHttpProxyField.getType();
					Constructor constr = ProxyPropertiesClass.getConstructors()[1];
					Object ProxyProperties = constr.newInstance("10.11.12.13", 1983, "");
					mHttpProxyField.set(linkProperties, ProxyProperties);
					mHttpProxy = mHttpProxyField.get(linkProperties);
				}

				if (mHttpProxy != null)
				{
					Field mHostField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mHost");
					mHostField.setAccessible(true);
					String mHost = (String) mHostField.get(mHttpProxy);

					Field mPortField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mPort");
					mPortField.setAccessible(true);
					Integer mPort = (Integer) mPortField.get(mHttpProxy);

					Field mExclusionListField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mExclusionList");
					mExclusionListField.setAccessible(true);
					String mExclusionList = (String) mExclusionListField.get(mHttpProxy);

					Log.d(TAG, "Proxy configuration: " + mHost + ":" + mPort + " , Exclusion List: " + mExclusionList);

					Proxy proxy = new Proxy(Proxy.Type.HTTP, new Socket(mHost, mPort).getRemoteSocketAddress());
					proxyHost = new ProxyConfiguration(proxy, mExclusionList, null, wifiConf);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return proxyHost;
	}

	/**
	 * Set the current proxy configuration for a specific URI scheme
	 * */
	public static void setCurrentProxyConfiguration(ProxyConfiguration proxyConf, String scheme)
	{
		String exclusionList = null;
		String host = null;
		String port = null;

		if (proxyConf != null)
		{
			exclusionList = proxyConf.exclusionList;

			SocketAddress address = proxyConf.proxyHost.address();
			if (address instanceof InetSocketAddress)
			{
				InetSocketAddress isa = (InetSocketAddress) address;
				// invoke methods on "isa". This is now safe - no risk of
				// exceptions

				host = isa.getAddress().toString();

				port = String.valueOf(isa.getPort());
				Log.d(TAG, "setHttpProxySystemProperty : " + host + ":" + port + " - " + exclusionList);
			}

		}

		if (exclusionList != null) exclusionList = exclusionList.replace(",", "|");

		if (host != null)
		{
			System.setProperty(scheme + ".proxyHost", host);
		}
		else
		{
			System.clearProperty(scheme + ".proxyHost");
		}

		if (port != null)
		{
			System.setProperty(scheme + ".proxyPort", port);
		}
		else
		{
			System.clearProperty(scheme + ".proxyPort");
		}

		if (exclusionList != null)
		{
			System.setProperty(scheme + ".nonProxyHosts", exclusionList);
		}
		else
		{
			System.clearProperty(scheme + ".nonProxyHosts");
		}
	}

	public static void setCurrentProxyHttpConfiguration(ProxyConfiguration proxyConf)
	{
		setCurrentProxyConfiguration(proxyConf, "http");
	}

	public static void setCurrentProxyHttpsConfiguration(ProxyConfiguration proxyConf)
	{
		setCurrentProxyConfiguration(proxyConf, "https");
	}

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
					Proxy p = new Proxy(Type.HTTP,new InetSocketAddress(proxyAddress,proxyPort));
					proxyConfig = new ProxyConfiguration(p, "", null, null);
					Log.d(TAG, "ProxyHost created: " + proxyConfig.toString());
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
