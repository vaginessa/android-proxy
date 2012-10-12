package com.shouldit.proxy.lib;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;

import com.shouldit.proxy.lib.Constants.ProxyStatusCodes;
import com.shouldit.proxy.lib.Constants.ProxyStatusErrors;

import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.util.Log;
import android.webkit.URLUtil;

public class ProxyConfiguration
{
	public static final String TAG = "ProxyConfiguration";

	public ProxyStatus status;
	public WifiConfiguration wifiConfiguration;
	public NetworkInfo networkInfo;
	public Proxy proxyHost;
	public int deviceVersion;
	public String proxyDescription;

	public ProxyConfiguration(Proxy proxy, String description, NetworkInfo netInfo, WifiConfiguration wifiConf)
	{
		proxyHost = proxy;
		proxyDescription = description;
		networkInfo = netInfo;
		wifiConfiguration = wifiConf;
		deviceVersion = Build.VERSION.SDK_INT;
		status = new ProxyStatus();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Proxy: %s\n", proxyHost.toString()));
		sb.append(String.format("Is Proxy reachable: %B\n", isProxyReachable()));
		sb.append(String.format("Is WEB reachable: %B\n", isWebReachable(60000)));

		if (networkInfo != null)
			sb.append(String.format("Network Info: %s\n", networkInfo));
		if (wifiConfiguration != null)
			sb.append(String.format("Wi-Fi Configuration Info: %s\n", wifiConfiguration));

		return sb.toString();
	}

	public Proxy.Type getConnectionType()
	{
		return proxyHost.type();
	}

	/**
	 * Can take a long time to execute this task. - Check if the proxy is
	 * enabled - Check if the proxy address is valid - Check if the proxy is
	 * reachable (using a PING) - Check if is possible to retrieve an URI
	 * resource using the proxy
	 * */
	public void acquireProxyStatus(int timeout)
	{
		status.clear();

		Log.d(TAG, "Checking if proxy is enabled ...");
		if (!isProxyEnabled())
		{
			Log.e(TAG, "PROXY NOT ENABLED");
			status.add(ProxyStatusCodes.PROXY_ENABLED, false);
		}
		else
		{
			Log.e(TAG, "PROXY ENABLED");
			status.add(ProxyStatusCodes.PROXY_ENABLED, true);
		}

		Log.d(TAG, "Checking if proxy is valid address ...");
		if (!isProxyValidAddress())
		{
			Log.e(TAG, "PROXY NOT VALID ADDRESS");
			status.add(ProxyStatusCodes.PROXY_ADDRESS_VALID, false);
		}
		else
		{
			Log.e(TAG, "PROXY VALID ADDRESS");
			status.add(ProxyStatusCodes.PROXY_ADDRESS_VALID, true);
		}

		Log.d(TAG, "Checking if proxy is reachable ...");
		if (!isProxyReachable())
		{
			Log.e(TAG, "PROXY NOT REACHABLE");
			status.add(ProxyStatusCodes.PROXY_REACHABLE, false);
		}
		else
		{
			Log.d(TAG, "PROXY REACHABLE");
			status.add(ProxyStatusCodes.PROXY_REACHABLE, true);
		}

		Log.d(TAG, "Checking if web is reachable ...");
		if (!isWebReachable(timeout))
		{
			Log.e(TAG, "WEB NOT REACHABLE");
			status.add(ProxyStatusCodes.WEB_REACHABILE, false);
		}
		else
		{
			Log.d(TAG, "WEB REACHABLE");
			status.add(ProxyStatusCodes.WEB_REACHABILE, true);
		}

	}

	public ProxyStatusErrors getMostRelevantProxyStatusError()
	{
		if (!status.getEnabled())
			return ProxyStatusErrors.PROXY_NOT_ENABLED;

		if (status.getWeb_reachable())
		{
			// If the WEB is reachable, the proxy is OK!
			return ProxyStatusErrors.NO_ERRORS;
		}
		else
		{
			if (status.getProxy_reachable())
			{
				return ProxyStatusErrors.WEB_NOT_REACHABLE;
			}
			else
			{	
				if (status.getValid_address())
				{
					return ProxyStatusErrors.PROXY_NOT_REACHABLE;
				}
				else
					return ProxyStatusErrors.PROXY_ADDRESS_NOT_VALID;
			}	
		}
	}

	private Boolean isProxyEnabled()
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
		try
		{
			String proxyHost = getProxyHost();

			if (InetAddressUtils.isIPv4Address(proxyHost) || InetAddressUtils.isIPv6Address(proxyHost) || InetAddressUtils.isIPv6HexCompressedAddress(proxyHost) || InetAddressUtils.isIPv6StdAddress(proxyHost))
			{
				return true;
			}

			if (URLUtil.isNetworkUrl(proxyHost))
			{
				return true;
			}

			if (URLUtil.isValidUrl(proxyHost))
			{
				return true;
			}

			// Test REGEX for Hostname validation
			// http://stackoverflow.com/questions/106179/regular-expression-to-match-hostname-or-ip-address
			//
			String ValidHostnameRegex = "^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$";
			Pattern pattern = Pattern.compile(ValidHostnameRegex);
			Matcher matcher = pattern.matcher(proxyHost);

			if (matcher.find())
			{
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Try to PING the HOST specified in the current proxy configuration
	 * */
	private Boolean isProxyReachable()
	{
		if (proxyHost != null && proxyHost.type() != Proxy.Type.DIRECT)
			return ProxyUtils.isHostReachable(proxyHost);
		else
			return false;
	}

	/**
	 * Try to download a webpage using the current proxy configuration
	 * */
	public static int DEFAULT_TIMEOUT = 60000; // 60 seconds

	private Boolean isWebReachable()
	{
		return isWebReachable(DEFAULT_TIMEOUT);
	}

	private Boolean isWebReachable(int timeout)
	{
		return ProxyUtils.isWebReachable(proxyHost, timeout);
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
		if (proxyHost == Proxy.NO_PROXY)
		{
			return Proxy.NO_PROXY.toString();
		}
		
		if (proxyDescription != null)
		{
			return proxyDescription;
		}
		else
		{
			return String.format("%s", proxyHost.address().toString());
		}
	}

	public String toShortIPString()
	{
		return String.format("%s:%d", getProxyIPHost(), getProxyPort());
	}

	public int getNetworkType()
	{
		return networkInfo.getType();
	}
}