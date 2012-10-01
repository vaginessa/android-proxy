package com.lechucksoftware.proxy.proxysettings.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.activities.ProxySettingsMainActivity;
import com.shouldit.proxy.lib.Constants.ProxyStatus;

public class UIUtils
{
	public static int PROXY_NOTIFICATION_ID = 1;
	
	public static String GetStatusSummary(Context ctx)
	{
		if (Globals.getInstance().proxyCheckStatus == ProxyCheckStatus.CHECKING)
		{
			return UIUtils.GetStatusTitle(ctx);
		}
		else
		{
			if (Globals.getInstance().proxyConf.isProxyEnabled())
			{
				return UIUtils.ProxyConfigToStatusString(ctx);
			}
			else
			{
				return ctx.getText(R.string.preference_proxy_host_port_summary_default).toString();
			}
		}
	}
	
	public static String GetStatusTitle(Context callerContext)
	{
		String description;
		
		switch(Globals.getInstance().proxyCheckStatus)
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
	
	public static String GetStatusDescription(Context callerContext)
	{
		String description;
		
		switch(Globals.getInstance().proxyCheckStatus)
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
	
	public static String ProxyConfigToStatusString(Context callerContext)
	{
		String message = String.format("%s", Globals.getInstance().proxyConf.toShortString());
		
		message += " - " + GetStatusTitle(callerContext);
		
		return message;
	}
	
	
	/**
	 * Notification related methods
	 * */
	public static void SetProxyNotification(Context callerContext)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callerContext);
		if (prefs.getBoolean("preference_notification_enabled", false))
		{
			
			String notificationTitle = GetStatusTitle(callerContext); 
			String notificationDescription = GetStatusDescription(callerContext); 
			
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
}
