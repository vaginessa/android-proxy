package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class ProxySettingsCheckerService extends IntentService
{
	public static final String CALLER_INTENT = "CallerIntent";
	public static String TAG = "ProxySettingsCheckerService";

	public ProxySettingsCheckerService()
	{
		super("ProxySettingsCheckerService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Intent callerIntent = (Intent) intent.getExtras().get(CALLER_INTENT);

		if (callerIntent != null)
		{
			String callerAction = callerIntent.getAction();
			LogWrapper.logIntent(TAG, callerIntent, Log.DEBUG);

			if (callerAction.equals(Constants.PROXY_SETTINGS_STARTED) || callerAction.equals(Constants.PROXY_CONFIGURATION_UPDATED) || callerAction.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
			{
				// LogWrapper.logIntent(TAG, callerIntent, Log.WARN);
				CheckProxySettings(callerIntent);
			}
			else if (callerAction.equals(ConnectivityManager.CONNECTIVITY_ACTION))
			{
				Boolean noConnectivity = callerIntent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				if (noConnectivity)
				{
					return;
				}

				int intentNetworkType = callerIntent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
				NetworkInfo ni = ApplicationGlobals.getConnectivityManager().getActiveNetworkInfo();

				if (ni != null && ni.isConnected())
				{
					if (ni.getType() == intentNetworkType) // Check only for
															// intent related to
															// active network
					{
						// LogWrapper.logIntent(TAG, callerIntent, Log.DEBUG,
						// true);
						CheckProxySettings(callerIntent);
					}
				}
				else
				{
					
				}
				// else
				// LogWrapper.logIntent(TAG, callerIntent, Log.DEBUG, false);

			}
			else
			{
				// TODO: ????
			}
		}
		else
		{
			// TODO: ????
		}
	}

	@Override
	public void onDestroy()
	{
		LogWrapper.d(TAG, "ProxySettingsCheckerService destroying");
	};

	/**
	 * @param context
	 */
	public void CheckProxySettings(Intent callerIntent)
	{
		try
		{
			ProxyConfiguration oldconf = null;

			if (!callerIntent.getAction().equals(Constants.PROXY_SETTINGS_STARTED))
			{
				oldconf = ApplicationGlobals.getCachedConfiguration();
			}

			ToggleApplicationStatus();
			ApplicationGlobals.updateProxyConfigurationList();
			ProxyConfiguration newconf = ApplicationGlobals.getCurrentConfiguration();

			NetworkInfo ni = ApplicationGlobals.getConnectivityManager().getActiveNetworkInfo();
			if (ni != null && ni.isAvailable() && ni.isConnected())
			{
				if (oldconf == null || oldconf.compareTo(newconf) != 0)
				{
					newconf.acquireProxyStatus(ApplicationGlobals.getInstance().timeout);
					LogWrapper.i(TAG, newconf.toString());
				}				
			}

			ToggleApplicationStatus();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			UIUtils.DisableProxyNotification(getApplicationContext());
			LogWrapper.d(TAG, "Exception caught: disable proxy notification");
		}
	}

	public void ToggleApplicationStatus()
	{
		/**
		 * Call the update of the UI
		 * */
		LogWrapper.d(TAG, "Sending broadcast intent " + Constants.PROXY_UPDATE_NOTIFICATION);
		Intent intent = new Intent(Constants.PROXY_UPDATE_NOTIFICATION);
		getApplicationContext().sendBroadcast(intent);

		UIUtils.UpdateStatusBarNotification(ApplicationGlobals.getCachedConfiguration(), getApplicationContext());
	}
}