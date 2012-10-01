package com.lechucksoftware.proxy.proxysettings.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ProxySettingsMainActivity;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.Constants.ProxyStatus;
import com.shouldit.proxy.lib.reflection.ReflectionUtils;
import com.shouldit.proxy.lib.reflection.android.RProxySettings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Utils
{
	public static String TAG = "Utils";

	/**
	 * Get proxy configuration for Wi-Fi access point. Valid for API >= 12
	 * */
	public static ProxyConfiguration getProxySdk12(Context ctx, WifiConfiguration wifiConf)
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
					proxyHost = new ProxyConfiguration(proxy, proxy.toString(), null, wifiConf);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return proxyHost;
	}

	public static void SetHTTPAuthentication(final String user, final String password)
	{
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});
	}

	// private static ProxyConfiguration getProxyConfiguration(Context ctx,
	// WifiConfiguration wifiConf)
	// {
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
	// {
	// return getProxySdk12(ctx, wifiConf);
	// }
	// else
	// {
	// return getGlobalProxy(ctx);
	// }
	// }

	// public static List<ProxyConfiguration> getProxiesConfigurations(Context
	// ctx)
	// {
	// List<ProxyConfiguration> proxyHosts = new
	// ArrayList<ProxyConfiguration>();
	// WifiManager wifiManager = (WifiManager)
	// ctx.getSystemService(Context.WIFI_SERVICE);
	// List<WifiConfiguration> configuredNetworks =
	// wifiManager.getConfiguredNetworks();
	//
	// /**
	// * Just for testing on the Emulator
	// * */
	// if (Build.PRODUCT.equals("sdk") && configuredNetworks.size() == 0)
	// {
	// WifiConfiguration fakeWifiConf = new WifiConfiguration();
	// fakeWifiConf.SSID = "Fake_SDK_WI-FI";
	// configuredNetworks.add(fakeWifiConf);
	// }
	//
	// for (WifiConfiguration wifiConf : configuredNetworks)
	// {
	// proxyHosts.add(getProxyConfiguration(ctx, wifiConf));
	// }
	//
	// return proxyHosts;
	// }

}
