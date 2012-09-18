package com.lechucksoftware.proxy.proxysettings.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.content.pm.PackageInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Utils
{
	public static String TAG = "Utils";
	public static int PROXY_NOTIFICATION_ID = 1;
	
	
	public static void SetProxyNotification(Context callerContext, ProxyCheckStatus status)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callerContext);
		if (prefs.getBoolean("preference_notification_enabled", false))
		{
			
			String notificationTitle = getNotificationTitle(callerContext, status); 
			String notificationDescription = getNotificationDescription(callerContext, status); 
			
			// The PendingIntent will launch activity if the user selects this
			// notification
			Intent preferencesIntent = new Intent(callerContext, ProxySettingsMainActivity.class);
			EnableProxyNotification(callerContext, preferencesIntent, notificationTitle, notificationDescription);
		}
		else
		{
			DisableProxyNotification(callerContext);
		}
	}
	
	public static String getNotificationTitle(Context callerContext, ProxyCheckStatus checkStatus)
	{
		String description;
		
		switch(checkStatus)
		{
			case CHECKED:
				{
					ProxyStatus status = Globals.getInstance().proxyConf.getProxyStatus();
					
					switch (status)
					{
						case OK:
							description = callerContext.getResources().getString(R.string.statusbar_notification_title_enabled);
							break;
							
						case PROXY_NOT_ENABLED:
							description = callerContext.getResources().getString(R.string.statusbar_notification_title_not_enabled);
							break;
						
						case PROXY_INVALID_ADDRESS:
							description = callerContext.getResources().getString(R.string.statusbar_notification_title_invalid_address);
							break;
							
						case PROXY_NOT_REACHABLE:
							description = callerContext.getResources().getString(R.string.statusbar_notification_title_not_reachable);
							break;
						case WEB_NOT_REACHABLE:
							description = callerContext.getResources().getString(R.string.statusbar_notification_title_web_not_reachable);
							break;
							
						default:
							description = "";
					}
					
				}	
				break;
			
			case CHECKING:
				description = callerContext.getResources().getString(R.string.statusbar_notification_title_checking);
				break;
				
			default:
				description = "";
				break;
		}
		
		return description;
	}
	
	public static String getNotificationDescription(Context callerContext, ProxyCheckStatus checkStatus)
	{
		String description;
		
		switch(checkStatus)
		{
			case CHECKED:
				{
					ProxyStatus status = Globals.getInstance().proxyConf.getProxyStatus();
					
					switch (status)
					{
						case OK:
							description = callerContext.getResources().getString(R.string.statusbar_notification_description_enabled);
							description = description + " " + Globals.getInstance().proxyConf.toShortString();
							break;
							
						case PROXY_NOT_ENABLED:
							description = callerContext.getResources().getString(R.string.statusbar_notification_description_not_enabled);
							break;
						
						case PROXY_INVALID_ADDRESS:
							description = callerContext.getResources().getString(R.string.statusbar_notification_description_invalid_address);
							break;
							
						case PROXY_NOT_REACHABLE:
							description = callerContext.getResources().getString(R.string.statusbar_notification_description_not_reachable);
							break;
						case WEB_NOT_REACHABLE:
							description = callerContext.getResources().getString(R.string.statusbar_notification_description_web_not_reachable);
							break;
							
						default:
							description = "";
					}
					
				}	
				break;
			
			case CHECKING:
				description = callerContext.getResources().getString(R.string.statusbar_notification_description_checking);
				break;
				
			default:
				description = "";
				break;
		}
		
		return description;
	}
	
	
	private static void EnableProxyNotification(Context callerContext, Intent intentToCall, String notificationTitle, String notificationDescription)
	{
		NotificationManager manager = (NotificationManager) callerContext.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent contentIntent = PendingIntent.getActivity(callerContext, 0, intentToCall, 0);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(callerContext);
		builder.setContentIntent(contentIntent).
		setSmallIcon(R.drawable.ic_stat_proxy_notification).
		setTicker(notificationTitle).
		setWhen(System.currentTimeMillis()).
		setContentTitle(notificationTitle).
		setContentText(notificationDescription);
		
		Notification n;
		n = builder.getNotification();
		n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

		manager.notify(PROXY_NOTIFICATION_ID, n);
	}

	public static void DisableProxyNotification(Context callerContext)
	{
		NotificationManager manager = (NotificationManager) callerContext.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(PROXY_NOTIFICATION_ID);
	}

	
	public static String proxyConfigToStatusString(Context callerContext)
	{
		String message = String.format("%s", Globals.getInstance().proxyConf.proxyHost.address().toString());
		
		message += " - " + getNotificationDescription(callerContext, ProxyCheckStatus.CHECKED);
		
		return message;
	}

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
				proxyHost = new ProxyConfiguration(null, null, wifiConf);
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
					proxyHost = new ProxyConfiguration(proxy, null, wifiConf);
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
