package com.lechucksoftware.proxy.proxysettings.utils;

import java.io.File;
import java.net.Proxy.Type;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ProxySettingsMainActivity;
import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusErrors;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class UIUtils
{
	public static int PROXY_NOTIFICATION_ID = 1;
	public static int URL_DOWNLOADER_COMPLETED_ID = 2;

	public static String GetStatusSummary(ProxyConfiguration conf, Context ctx)
	{
//		if (ApplicationGlobals.getInstance().proxyCheckStatus == ProxyCheckStatus.CHECKING)
		{
			return UIUtils.GetStatusTitle(conf, ctx);
		}
//		else
//		{
			// if (Globals.getInstance().proxyConf.status.getEnabled())
			// {
//			return UIUtils.ProxyConfigToStatusString(ctx);
			// }
			// else
			// {
			// return
			// ctx.getText(R.string.preference_proxy_host_port_summary_default).toString();
			// }
//		}
	}

	public static String GetStatusTitle(ProxyConfiguration conf, Context callerContext)
	{
		String description;

		switch (conf.getCheckingStatus())
		{
			case CHECKED:
			{
				ProxyStatusErrors status = conf.getMostRelevantProxyStatusError();

				switch (status)
				{
					case NO_ERRORS:
						description = callerContext.getResources().getString(R.string.statusbar_notification_title_enabled);
						break;

					case PROXY_NOT_ENABLED:
						description = callerContext.getResources().getString(R.string.statusbar_notification_title_not_enabled);
						break;

					case PROXY_ADDRESS_NOT_VALID:
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

	public static String GetStatusDescription(ProxyConfiguration conf, Context callerContext)
	{
		String description;

		switch (conf.getCheckingStatus())
		{
			case CHECKED:
			{
				ProxyStatusErrors status = conf.getMostRelevantProxyStatusError();

				switch (status)
				{
					case NO_ERRORS:
						description = callerContext.getResources().getString(R.string.statusbar_notification_description_enabled);
						description = description + " " + conf.toShortString();
						break;

					case PROXY_NOT_ENABLED:
						description = callerContext.getResources().getString(R.string.statusbar_notification_description_not_enabled);
						break;

					case PROXY_ADDRESS_NOT_VALID:
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

	public static String ProxyConfigToStatusString(ProxyConfiguration conf, Context callerContext)
	{
		String message = String.format("%s", conf.toShortString());

		message += " - " + GetStatusTitle(conf, callerContext);

		return message;
	}

	/**
	 * @param context
	 * @param proxyConfig
	 * @param status
	 */
	public static void UpdateStatusBarNotification(ProxyConfiguration conf, Context context)
	{
		if (conf.getCheckingStatus() == CheckStatusValues.CHECKED)
		{	
			if (conf.getProxyHost().type() == Type.DIRECT)
			{
				DisableProxyNotification(context);
			}
			else
			{
				SetProxyNotification(conf, context);
			}
		}
		else
		{
			
		}
	}

	/**
	 * Notification related methods
	 * */
	public static void SetProxyNotification(ProxyConfiguration conf, Context callerContext)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callerContext);
		if (prefs.getBoolean("preference_notification_enabled", false))
		{

			String notificationTitle = GetStatusTitle(conf, callerContext);
			String notificationDescription = GetStatusDescription(conf, callerContext);

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

	public static void NotifyCompletedDownload(Context callerContext, String downloadedFilePath)
	{
		// Intent intent = new Intent();
		// intent.setAction(android.content.Intent.ACTION_VIEW);
		File downloadedFile = new File(downloadedFilePath);
		// intent.setData(Uri.fromFile(downloadedFile.getParentFile()));

		// NotificationManager manager = (NotificationManager)
		// callerContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// PendingIntent contentIntent =
		// PendingIntent.getActivity(callerContext, 0, intent, 0);
		//
		// NotificationCompat.Builder builder = new
		// NotificationCompat.Builder(callerContext);
		// builder.setContentIntent(contentIntent).
		// setSmallIcon(R.drawable.ic_stat_proxy_notification).
		// setTicker("Proxy Settings completed a download ...").
		// setWhen(System.currentTimeMillis()).
		// setContentTitle(downloadedFile.getName()).
		// setContentText("Download completed ");
		//
		// Notification n;
		// n = builder.getNotification();
		//
		// manager.notify(URL_DOWNLOADER_COMPLETED_ID, n);

		CharSequence text = downloadedFile.getName() + " " + callerContext.getResources().getText(R.string.preference_test_proxy_urlretriever_dialog_file_saved);
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(callerContext, text, duration);
		toast.show();
	}

	public static void NotifyExceptionOnDownload(Context callerContext, String exceptionDetail)
	{
		CharSequence text = callerContext.getResources().getText(R.string.preference_test_proxy_urlretriever_dialog_file_exception) + "\n\n" + exceptionDetail;
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(callerContext, text, duration);
		toast.show();
	}

	private static void EnableProxyNotification(Context callerContext, Intent intentToCall, String notificationTitle, String notificationDescription)
	{
		NotificationManager manager = (NotificationManager) callerContext.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent contentIntent = PendingIntent.getActivity(callerContext, 0, intentToCall, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(callerContext);
		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.ic_stat_proxy_notification).setTicker(notificationTitle).setWhen(System.currentTimeMillis()).setContentTitle(notificationTitle).setContentText(notificationDescription);

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
