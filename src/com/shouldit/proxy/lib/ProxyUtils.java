package com.shouldit.proxy.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.webkit.URLUtil;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.ReflectionUtils;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;
import org.apache.http.HttpHost;
import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.net.Proxy.Type;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyUtils
{
	public static final String TAG = "ProxyUtils";

    public static void startWifiScan()
    {
        if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
        {
            APL.getWifiManager().startScan();
        }
    }

    public static void connectToAP(ProxyConfiguration conf) throws Exception
    {
        if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
        {
            if (conf != null && conf.ap != null && conf.ap.getLevel() > -1)
            {
                // Connect to AP only if it's available
                ReflectionUtils.connectToWifi(APL.getWifiManager(),conf.ap.networkId);

                APL.getWifiManager().enableNetwork(conf.ap.networkId, true);
            }
        }
    }

    public static NetworkInfo getCurrentNetworkInfo()
    {
        NetworkInfo ni = APL.getConnectivityManager().getActiveNetworkInfo();
        return ni;
    }

    public static NetworkInfo getCurrentWiFiInfo()
    {
        NetworkInfo ni = APL.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ni;
    }

    public static Boolean isConnectedToWiFi()
    {
        NetworkInfo ni = ProxyUtils.getCurrentWiFiInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Boolean isConnected()
    {
        NetworkInfo ni = ProxyUtils.getCurrentNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

	public static String cleanUpSSID(String SSID)
	{
        if (SSID != null)
        {
            if (SSID.startsWith("\""))
			    return removeDoubleQuotes(SSID);    // Remove double quotes from SSID
            else
                return SSID;
        }
		else
			return "";  // For safety return always and empty string
	}

	public static String removeDoubleQuotes(String string)
	{
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"'))
		{
			return string.substring(1, length - 1);
		}
		return string;
	}

	public static String convertToQuotedString(String string)
	{
		return "\"" + string + "\"";
	}

	public static Intent getProxyIntent()
	{
		if (Build.VERSION.SDK_INT >= 12) // Honeycomb 3.1
		{
			return getAPProxyIntent();
		}
		else
		{
			return getGlobalProxyIntent();
		}
	}

	/**
	 * For API < 12
	 * */
	private static Intent getGlobalProxyIntent()
	{
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.ProxySelector"));

		return intent;
	}

	/**
	 * For API >= 12
	 * */
	private static Intent getAPProxyIntent()
	{
		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);

		return intent;
	}

	// public static Intent getWebViewWithProxy(Context context, URI uri)
	// {
	// Intent intent = new Intent(context, );
	// intent.putExtra("URI", uri);
	//
	// return intent;
	// }

	public static boolean isHostReachable(Proxy proxy)
	{	
		Boolean standardResult = standardAPIPingHost(proxy);
		if (standardResult)
		{
			return true;
		}
		else
		{
			Boolean lowResult = lowLevelPingHost(proxy);
			return lowResult;
		}
	}

	public static boolean standardAPIPingHost(Proxy proxy)
	{
		try
		{
			InetSocketAddress proxySocketAddress = (InetSocketAddress) proxy.address();
			return InetAddress.getByName(proxySocketAddress.toString().split(":")[0]).isReachable(100000);
//			return proxySocketAddress.getAddress().isReachable(100000);
		}
		catch (Exception e)
		{
            LogWrapper.e(TAG, "ProxyUtils.standardAPIPingHost() Exception: " + e.toString());
		}
		
		return false;
	}

//	public static void pingMe()
//	{
//
//		try
//		{
//			ByteBuffer send = ByteBuffer.wrap("Hello".getBytes());
//			ByteBuffer receive = ByteBuffer.allocate("Hello".getBytes().length);
//			//use echo port 7
//			InetSocketAddress socketAddress = new InetSocketAddress("192.168.1.2", 7);
//			DatagramChannel dgChannel = DatagramChannel.open();
//			//we have the channel non-blocking.
//			dgChannel.configureBlocking(false);
//			dgChannel.connect(socketAddress);
//			dgChannel.send(send, socketAddress);
//			/*
//			 * it's non-blocking so we need some amount of delay to get the
//			 * response
//			 */
//			Thread.sleep(10000);
//			dgChannel.receive(receive);
//			String response = new String(receive.array());
//			if (response.isSameConfiguration("Hello"))
//			{
//				System.out.println("Ping is alive");
//			}
//			else
//			{
//				System.out.println("No response");
//			}
//
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
//
//	}

	public static boolean lowLevelPingHost(Proxy proxy)
	{
		int exitValue;
		Runtime runtime = Runtime.getRuntime();
		Process proc;

		String cmdline = null;
		String proxyAddress = null;

		try
		{
			InetSocketAddress proxySocketAddress = (InetSocketAddress) proxy.address();
			proxyAddress = proxySocketAddress.getAddress().getHostAddress();
		}
		catch (Exception e)
		{
            LogWrapper.e(TAG, "ProxyUtils.lowLevelPingHost() Exception calling getAddress().getHostAddress() on proxySocketAddress : " + e.toString());
		}

		if (proxyAddress == null)
		{
			try
			{
				InetSocketAddress proxySocketAddress = (InetSocketAddress) proxy.address();
				proxyAddress = proxySocketAddress.toString();
			}
			catch (Exception e)
			{
                LogWrapper.e(TAG, "ProxyUtils.lowLevelPingHost() Exception calling toString() on proxySocketAddress : " + e.toString());
			}
		}

		if (proxyAddress != null)
		{
			cmdline = "ping -c 1 -w 1 " + proxyAddress;

			try
			{
				proc = runtime.exec(cmdline);
				proc.waitFor();
				exitValue = proc.exitValue();

				LogWrapper.d(TAG, "Ping exit value: " + exitValue);

				if (exitValue == 0)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			catch (IOException e)
			{
                LogWrapper.e(TAG, "ProxyUtils.lowLevelPingHost() IOException : " + e.toString());
			}
			catch (InterruptedException e)
			{
                LogWrapper.e(TAG, "ProxyUtils.lowLevelPingHost() InterruptedException : " + e.toString());
			}
		}

		return false;
	}

	public static int testHTTPConnection(URI uri, ProxyConfiguration proxyConfiguration, int timeout)
	{
		int step = 0;
		while (step < 5)
		{
			try
			{
				URL url = uri.toURL();

				if (proxyConfiguration != null && proxyConfiguration.getProxyType() == Type.HTTP)
				{
					System.setProperty("http.proxyHost", proxyConfiguration.getProxyIPHost());
					System.setProperty("http.proxyPort", proxyConfiguration.getProxyPort().toString());
				}
				else
				{
					System.setProperty("http.proxyHost", "");
					System.setProperty("http.proxyPort", "");
				}

				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

				httpURLConnection.setReadTimeout(timeout);
				httpURLConnection.setConnectTimeout(timeout);

				return httpURLConnection.getResponseCode();
			}
			catch (MalformedURLException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() MalformedURLException : " + e.toString());
			}
			catch (UnknownHostException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() UnknownHostException : " + e.toString());
			}
			catch (SocketTimeoutException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() timed out after: " + timeout + " msec");
			}
			catch (SocketException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() SocketException : " + e.toString());
			}
			catch (IOException e)
			{
				LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() IOException : " + e.toString());
			}
            catch (Exception e)
            {
                LogWrapper.e(TAG, "ProxyUtils.testHTTPConnection() Exception : " + e.toString());
            }

			step++;

			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				LogWrapper.e(TAG, "Exception during waiting for next try of testHTTPConnection: " + e.toString());
				return -1;
			}
		}

		return -1;
	}

	public static String getURI(URI uri, Proxy proxy, int timeout)
	{
		try
		{
			URL url = uri.toURL();
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);

			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setConnectTimeout(timeout);

			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK)
			{
				// Response successful
				InputStream inputStream = httpURLConnection.getInputStream();

				// Parse it line by line
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String temp;
				StringBuilder sb = new StringBuilder();
				while ((temp = bufferedReader.readLine()) != null)
				{
					// LogWrapper.d(TAG, temp);
					sb.append(temp);
				}

				return sb.toString();
			}
			else
			{
				LogWrapper.e(TAG, "INCORRECT RETURN CODE: " + response);
				return null;
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (SocketTimeoutException e)
		{
			LogWrapper.e(TAG, "ProxyUtils.getURI() timed out after: " + timeout + " msec");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static boolean canGetWebResources(ProxyConfiguration proxyConfiguration, int timeout)
	{
		try
		{
			int result = testHTTPConnection(new URI("http://www.un.org/"), proxyConfiguration, timeout);
//            int rawresult = testHTTPConnection(new URI("http://157.150.34.32"), proxyConfiguration, timeout);


            switch (result)
			{
				case HttpURLConnection.HTTP_OK:
				case HttpURLConnection.HTTP_CREATED:
				case HttpURLConnection.HTTP_NO_CONTENT:
				case HttpURLConnection.HTTP_NOT_AUTHORITATIVE:
				case HttpURLConnection.HTTP_ACCEPTED:
				case HttpURLConnection.HTTP_PARTIAL:
				case HttpURLConnection.HTTP_RESET:
					return true;

				default:
					return false;
			}
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static void setWebViewProxy(Context context, ProxyConfiguration proxyConf)
	{
		try
		{
			if (proxyConf != null && proxyConf.getProxyType() == Type.HTTP && APL.getDeviceVersion() < 12)
			{
				setProxy(context, proxyConf.getProxyIPHost(), proxyConf.getProxyPort());
			}
			else
			{
				resetProxy(context);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void resetProxy(Context ctx) throws Exception
	{
		Object requestQueueObject = getRequestQueue(ctx);
		if (requestQueueObject != null)
		{
			setDeclaredField(requestQueueObject, "mProxyHost", null);
		}
	}

	private static boolean setProxy(Context ctx, String host, int port)
	{
		boolean ret = false;
		try
		{
			Object requestQueueObject = getRequestQueue(ctx);
			if (requestQueueObject != null)
			{
				// Create Proxy config object and set it into request Q
				HttpHost httpHost = new HttpHost(host, port, "http");
				setDeclaredField(requestQueueObject, "mProxyHost", httpHost);
				// LogWrapper.d("Webkit Setted Proxy to: " + host + ":" + port);
				ret = true;
			}
		}
		catch (Exception e)
		{
			LogWrapper.e("APL", "Exception setting WebKit proxy settings: " + e.toString());
		}
		return ret;
	}

	@SuppressWarnings("rawtypes")
	private static Object GetNetworkInstance(Context ctx) throws ClassNotFoundException
	{
		Class networkClass = Class.forName("android.webkit.Network");
		return networkClass;
	}

	private static Object getRequestQueue(Context ctx) throws Exception
	{
		Object ret = null;
		Object networkClass = GetNetworkInstance(ctx);
		if (networkClass != null)
		{
			Object networkObj = invokeMethod(networkClass, "getInstance", new Object[] { ctx }, Context.class);
			if (networkObj != null)
			{
				ret = getDeclaredField(networkObj, "mRequestQueue");
			}
		}
		return ret;
	}

	private static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		Field f = obj.getClass().getDeclaredField(name);
		f.setAccessible(true);
		Object out = f.get(obj);
		return out;
	}

	private static void setDeclaredField(Object obj, String name, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		Field f = obj.getClass().getDeclaredField(name);
		f.setAccessible(true);
		f.set(obj, value);
	}

	@SuppressWarnings("rawtypes")
	private static Object invokeMethod(Object object, String methodName, Object[] params, Class... types) throws Exception
	{
		Object out = null;
		Class c = object instanceof Class ? (Class) object : object.getClass();

		if (types != null)
		{
			Method method = c.getMethod(methodName, types);
			out = method.invoke(object, params);
		}
		else
		{
			Method method = c.getMethod(methodName);
			out = method.invoke(object);
		}
		return out;
	}

    public static SecurityType getSecurity(WifiConfiguration config)
    {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK))
        {
            return SecurityType.SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X))
        {
            return SecurityType.SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SecurityType.SECURITY_WEP : SecurityType.SECURITY_NONE;
    }

    public static SecurityType getSecurity(ScanResult result)
    {
        if (result.capabilities.contains("WEP"))
        {
            return SecurityType.SECURITY_WEP;
        }
        else if (result.capabilities.contains("PSK"))
        {
            return SecurityType.SECURITY_PSK;
        }
        else if (result.capabilities.contains("EAP"))
        {
            return SecurityType.SECURITY_EAP;
        }
        return SecurityType.SECURITY_NONE;
    }

    public static String getSecurityString(ProxyConfiguration conf, Context ctx, boolean concise)
    {
        if (conf != null && conf.ap != null)
        {
            return getSecurityString(conf.ap.security, conf.ap.pskType, ctx, true);
        }
        else
            return "";
    }

    public static String getSecurityString(SecurityType security, PskType pskType, Context context, boolean concise)
    {
        switch (security)
        {
            case SECURITY_EAP:
                return concise ? context.getString(R.string.wifi_security_short_eap) : context.getString(R.string.wifi_security_eap);
            case SECURITY_PSK:
                switch (pskType)
                {
                    case WPA:
                        return concise ? context.getString(R.string.wifi_security_short_wpa) : context.getString(R.string.wifi_security_wpa);
                    case WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa2) : context.getString(R.string.wifi_security_wpa2);
                    case WPA_WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) : context.getString(R.string.wifi_security_wpa_wpa2);
                    case UNKNOWN:
                    default:
                        return concise ? context.getString(R.string.wifi_security_short_psk_generic) : context.getString(R.string.wifi_security_psk_generic);
                }
            case SECURITY_WEP:
                return concise ? context.getString(R.string.wifi_security_short_wep) : context.getString(R.string.wifi_security_wep);
            case SECURITY_NONE:
            default:
                return concise ? context.getString(R.string.wifi_security_none) : context.getString(R.string.wifi_security_none);
        }
    }

    public static PskType getPskType(ScanResult result)
    {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa)
        {
            return PskType.WPA_WPA2;
        }
        else if (wpa2)
        {
            return PskType.WPA2;
        }
        else if (wpa)
        {
            return PskType.WPA;
        }
        else
        {
            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PskType.UNKNOWN;
        }
    }

    public static void acquireProxyStatus(ProxyConfiguration conf, ProxyStatus status)
    {
        acquireProxyStatus(conf, status, ProxyCheckOptions.ALL, APLConstants.DEFAULT_TIMEOUT);
    }

    /**
     * Can take a long time to execute this task. - Check if the proxy is
     * enabled - Check if the proxy address is valid - Check if the proxy is
     * reachable (using a PING) - Check if is possible to retrieve an URI
     * resource using the proxy
     */
    public static void acquireProxyStatus(ProxyConfiguration conf, ProxyStatus status, EnumSet<ProxyCheckOptions> checkOptions, int timeout)
    {
        status.clear();
        status.startchecking();
        broadCastUpdatedStatus();

        if (Build.VERSION.SDK_INT >= 12)
        {
            acquireProxyStatusSDK12(conf, status,checkOptions);
        }
        else
        {
            acquireProxyStatusSDK1_11(conf, status, checkOptions);
        }

        if (checkOptions.contains(ProxyCheckOptions.ONLINE_CHECK))
        {
            // Always check if WEB is reachable
            LogWrapper.d(TAG, "Checking if web is reachable ...");
            status.set(isWebReachable(conf, timeout));
            broadCastUpdatedStatus();
        }
        else
        {
            status.set(ProxyStatusProperties.WEB_REACHABLE, CheckStatusValues.NOT_CHECKED, false, false);
        }
    }

    private static void acquireProxyStatusSDK1_11(ProxyConfiguration conf, ProxyStatus status, EnumSet<ProxyCheckOptions> checkOptions)
    {
        // API version <= 11 (Older devices)
        status.set(ProxyStatusProperties.WIFI_ENABLED, CheckStatusValues.NOT_CHECKED, false, false);
        status.set(ProxyStatusProperties.WIFI_SELECTED, CheckStatusValues.NOT_CHECKED, false, false);

        LogWrapper.d(TAG, "Checking if proxy is enabled ...");
        status.set(isProxyEnabled(conf));
        broadCastUpdatedStatus();

        if (status.getProperty(ProxyStatusProperties.PROXY_ENABLED).result)
        {
            LogWrapper.d(TAG, "Checking if proxy is valid hostname ...");
            status.set(isProxyValidHostname(conf));
            broadCastUpdatedStatus();

            LogWrapper.d(TAG, "Checking if proxy is valid port ...");
            status.set(isProxyValidPort(conf));
            broadCastUpdatedStatus();

            if (checkOptions.contains(ProxyCheckOptions.ONLINE_CHECK)
                && status.getProperty(ProxyStatusProperties.PROXY_VALID_HOSTNAME).result
                && status.getProperty(ProxyStatusProperties.PROXY_VALID_PORT).result)
            {
                LogWrapper.d(TAG, "Checking if proxy is reachable ...");
                status.set(isProxyReachable(conf));
                broadCastUpdatedStatus();
            }
            else
            {
                status.set(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.NOT_CHECKED, false, false);
            }
        }
        else
        {
            wifiNotEnabled_DisableChecking(status);
        }
    }

    private static void acquireProxyStatusSDK12(ProxyConfiguration conf, ProxyStatus status, EnumSet<ProxyCheckOptions> checkOptions)
    {
        LogWrapper.d(TAG, "Checking if Wi-Fi is enabled ...");
        status.set(isWifiEnabled(conf));
        broadCastUpdatedStatus();

        if (status.getProperty(ProxyStatusProperties.WIFI_ENABLED).result)
        {
            LogWrapper.d(TAG, "Checking if Wi-Fi is selected ...");
            status.set(isWifiSelected(conf));
            broadCastUpdatedStatus();

            if (status.getProperty(ProxyStatusProperties.WIFI_SELECTED).result)
            {
                // Wi-Fi enabled & selected
                LogWrapper.d(TAG, "Checking if proxy is enabled ...");
                status.set(isProxyEnabled(conf));
                broadCastUpdatedStatus();

                if (status.getProperty(ProxyStatusProperties.PROXY_ENABLED).result)
                {
                    LogWrapper.d(TAG, "Checking if proxy is valid hostname ...");
                    status.set(isProxyValidHostname(conf));
                    broadCastUpdatedStatus();

                    LogWrapper.d(TAG, "Checking if proxy is valid port ...");
                    status.set(isProxyValidPort(conf));
                    broadCastUpdatedStatus();

                    if (checkOptions.contains(ProxyCheckOptions.ONLINE_CHECK)
                        && status.getProperty(ProxyStatusProperties.PROXY_VALID_HOSTNAME).result
                        && status.getProperty(ProxyStatusProperties.PROXY_VALID_PORT).result)
                    {
                        LogWrapper.d(TAG, "Checking if proxy is reachable ...");
                        status.set(isProxyReachable(conf));
                        broadCastUpdatedStatus();
                    }
                    else
                    {
                        status.set(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.NOT_CHECKED, false, false);
                    }
                }
                else
                {
                    wifiNotEnabled_DisableChecking(status);
                }
            }
            else
            {
                wifiNotEnabled_DisableChecking(status);
            }
        }
        else
        {
            status.set(ProxyStatusProperties.WIFI_SELECTED, CheckStatusValues.NOT_CHECKED, false, false);
            wifiNotEnabled_DisableChecking(status);
        }
    }

    private static void wifiNotEnabled_DisableChecking(ProxyStatus status)
    {
        status.set(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.NOT_CHECKED, false, false);
        status.set(ProxyStatusProperties.PROXY_VALID_HOSTNAME, CheckStatusValues.NOT_CHECKED, false, false);
        status.set(ProxyStatusProperties.PROXY_VALID_PORT, CheckStatusValues.NOT_CHECKED, false, false);
        status.set(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.NOT_CHECKED, false, false);
    }


    private static void broadCastUpdatedStatus()
    {
//        LogWrapper.d(TAG, "Sending broadcast intent: " + APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
        Intent intent = new Intent(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
        // intent.putExtra(APLConstants.ProxyStatus, status);
        APL.getContext().sendBroadcast(intent);
    }

    protected static ProxyStatusItem isWifiEnabled(ProxyConfiguration conf)
    {
        ProxyStatusItem result = null;

        if (APL.getWifiManager().isWifiEnabled())
        {
            NetworkInfo ni = APL.getConnectivityManager().getActiveNetworkInfo();
            if (ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI)
            {
                String status = APL.getContext().getString(R.string.status_wifi_enabled);
                result = new ProxyStatusItem(ProxyStatusProperties.WIFI_ENABLED, CheckStatusValues.CHECKED, true, true, status);
            }
            else
            {
                result = new ProxyStatusItem(ProxyStatusProperties.WIFI_ENABLED, CheckStatusValues.CHECKED, false, true, APL.getContext().getString(R.string.status_wifi_enabled_disconnected));
            }
        }
        else
        {
            result = new ProxyStatusItem(ProxyStatusProperties.WIFI_ENABLED, CheckStatusValues.CHECKED, false, true, APL.getContext().getString(R.string.status_wifi_not_enabled));
        }

        return result;
    }

    protected static ProxyStatusItem isWifiSelected(ProxyConfiguration conf)
    {
        ProxyStatusItem result = null;

        if (conf.isCurrentNetwork())
        {
            result = new ProxyStatusItem(ProxyStatusProperties.WIFI_SELECTED, CheckStatusValues.CHECKED, true, true, APL.getContext().getString(R.string.status_wifi_selected, conf.ap.ssid));
        }
        else
        {
            result = new ProxyStatusItem(ProxyStatusProperties.WIFI_SELECTED, CheckStatusValues.CHECKED, false, true, APL.getContext().getString(R.string.status_wifi_not_selected));
        }

        return result;
    }

    protected static ProxyStatusItem isProxyEnabled(ProxyConfiguration conf)
    {
        ProxyStatusItem result;

        if (Build.VERSION.SDK_INT >= 12)
        {
            // On API version > Honeycomb 3.1 (HONEYCOMB_MR1)
            // Proxy is disabled by default on Mobile connection
            ConnectivityManager cm = APL.getConnectivityManager();
            if (cm != null)
            {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE)
                {
                    result = new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.CHECKED, false, APL.getContext().getString(R.string.status_proxy_mobile_disabled));
                    return result;
                }
            }
        }

        if (conf.proxySetting == ProxySetting.UNASSIGNED || conf.proxySetting == ProxySetting.NONE)
        {
            result = new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.CHECKED, false, APL.getContext().getString(R.string.status_proxy_disabled));
        }
        else
        {
            // if (proxyHost != null && proxyPort != null)
            // {
            // HTTP or SOCKS proxy
            result = new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.CHECKED, true, APL.getContext().getString(R.string.status_proxy_enabled));
            // }
            // else
            // {
            // result = new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED,
            // CheckStatusValues.CHECKED, false);
            // }
        }

        return result;
    }

    protected static ProxyStatusItem isProxyValidHostname(ProxyConfiguration conf)
    {
        try
        {
            String proxyHost = conf.getProxyHostString();

            if (proxyHost == null || proxyHost.length() == 0)
            {
                return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_HOSTNAME, CheckStatusValues.CHECKED, false, APL.getContext().getString(R.string.status_hostname_empty));
            }
            else
            {
                // Test REGEX for Hostname validation
                // http://stackoverflow.com/questions/106179/regular-expression-to-match-hostname-or-ip-address
                //
                String ValidHostnameRegex = "^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$";
                Pattern pattern = Pattern.compile(ValidHostnameRegex);
                Matcher matcher = pattern.matcher(proxyHost);

                if (InetAddressUtils.isIPv4Address(proxyHost)
                        || InetAddressUtils.isIPv6Address(proxyHost)
                        || InetAddressUtils.isIPv6HexCompressedAddress(proxyHost)
                        || InetAddressUtils.isIPv6StdAddress(proxyHost)
                        || URLUtil.isNetworkUrl(proxyHost)
                        || URLUtil.isValidUrl(proxyHost)
                        || matcher.find())
                {
                    String msg = String.format("%s %s", APL.getContext().getString(R.string.status_hostname_valid), proxyHost);
                    return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_HOSTNAME, CheckStatusValues.CHECKED, true, msg);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_HOSTNAME, CheckStatusValues.CHECKED, false, APL.getContext().getString(R.string.status_hostname_notvalid));
    }

    protected static ProxyStatusItem isProxyValidPort(ProxyConfiguration conf)
    {
        if ((conf.getProxyPort() != null) && (conf.getProxyPort() >= 1) && (conf.getProxyPort() <= 65535))
        {
            String msg = String.format("%s %d", APL.getContext().getString(R.string.status_port_valid), conf.getProxyPort());
            return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_PORT, CheckStatusValues.CHECKED, true, msg);
        }
        else
        {
            return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_PORT, CheckStatusValues.CHECKED, false, APL.getContext().getString(R.string.status_port_empty));
        }
    }

    /**
     * Try to PING the HOST specified in the current proxy configuration
     */
    protected static ProxyStatusItem isProxyReachable(ProxyConfiguration conf)
    {
        if (conf.getProxy() != null && conf.getProxyType() != Proxy.Type.DIRECT)
        {
            Boolean result = ProxyUtils.isHostReachable(conf.getProxy());
            if (result)
            {
                return new ProxyStatusItem(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.CHECKED, true, APL.getContext().getString(R.string.status_proxy_reachable));
            }
            else
            {
                return new ProxyStatusItem(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.CHECKED, false, APL.getContext().getString(R.string.status_proxy_not_reachable));
            }
        }
        else
        {
            return new ProxyStatusItem(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.CHECKED, false, APL.getContext().getString(R.string.status_proxy_not_valid_informations));
        }
    }

    protected ProxyStatusItem isWebReachable(ProxyConfiguration conf)
    {
        return isWebReachable(conf, APLConstants.DEFAULT_TIMEOUT);
    }

    protected static ProxyStatusItem isWebReachable(ProxyConfiguration conf, int timeout)
    {
        Boolean result = ProxyUtils.canGetWebResources(conf, timeout);
        if (result)
        {
            return new ProxyStatusItem(ProxyStatusProperties.WEB_REACHABLE, CheckStatusValues.CHECKED, true, APL.getContext().getString(R.string.status_web_reachable));
        }
        else
        {
            return new ProxyStatusItem(ProxyStatusProperties.WEB_REACHABLE, CheckStatusValues.CHECKED, false, APL.getContext().getString(R.string.status_web_not_reachable));
        }
    }

}
