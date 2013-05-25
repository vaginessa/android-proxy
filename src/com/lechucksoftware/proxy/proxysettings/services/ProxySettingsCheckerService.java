package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.ProxyConfiguration;

import java.util.Date;

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

            if (callerAction.equals(Constants.PROXY_SETTINGS_STARTED)
                    || callerAction.equals(Constants.PROXY_SETTINGS_MANUAL_REFRESH)
                    || callerAction.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                    || callerAction.equals(APLConstants.APL_UPDATED_PROXY_CONFIGURATION))
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

                //TODO : check here
                //int intentNetworkType = callerIntent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_INFO , -1);
                NetworkInfo ni = ApplicationGlobals.getConnectivityManager().getActiveNetworkInfo();

                if (ni != null && ni.isConnected())
                {
                    //if (ni.getType() == intentNetworkType) // Check only for
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
                    LogWrapper.d(TAG, "Do not check proxy settings if network is not available!");
                }
                // else
                // LogWrapper.logIntent(TAG, callerIntent, Log.DEBUG, false);
            }
            else
            {
                LogWrapper.e(TAG, "Intent ACTION not handled: " + callerAction);
            }
        }
        else
        {
            LogWrapper.e(TAG, "Received Intent NULL ACTION");
        }
    }

    @Override
    public void onDestroy()
    {
        LogWrapper.d(TAG, "ProxySettingsCheckerService destroying");
    }

    ;

    public void CheckProxySettings(Intent callerIntent)
    {
        try
        {
            ProxyConfiguration oldconf = null;

            if (!callerIntent.getAction().equals(Constants.PROXY_SETTINGS_STARTED))
            {
                oldconf = ApplicationGlobals.getInstance().getCachedConfiguration();
            }

            CallRefreshApplicationStatus();

            ApplicationGlobals.getInstance().updateProxyConfigurationList();
            ProxyConfiguration newconf = ApplicationGlobals.getInstance().getCurrentConfiguration();

            NetworkInfo ni = ApplicationGlobals.getConnectivityManager().getActiveNetworkInfo();

            if (ni != null && ni.isAvailable() && ni.isConnected())
            {
                boolean checkNewConf = false;
                if (newconf != null)
                {
                    if (oldconf != null)
                    {
                        if (oldconf.compareTo(newconf) != 0)
                        {
                            checkNewConf = true;
                        }
                        else
                        {
                            if (oldconf.status != null
                                    && oldconf.status.checkedDate != null)
                            {

                                long diffMsec = new Date().getTime() - oldconf.status.checkedDate.getTime();
                                long diffSeconds = diffMsec / 1000;
                                long diffMinutes = diffMsec / (60 * 1000);

                                if (diffMinutes > 30)
                                {
                                    checkNewConf = true;
                                    // Skip check when configuration is the same
                                    LogWrapper.d(TAG, "Same configuration for 30 minutes check again!");
                                }
                            }
                        }
                    }
                    else
                    {
                        checkNewConf = true;
                    }
                }
                else
                {
                    // newconf cannot be null!!
                    BugSenseHandler.sendException(new Exception("Cannot have a null ProxyConfiguration"));
                }

                if (checkNewConf)
                {
                    LogWrapper.d(TAG, "Changed current proxy configuration: calling refresh of proxy status");
                    newconf.acquireProxyStatus(ApplicationGlobals.getInstance().timeout);
                    LogWrapper.i(TAG, newconf.toString());
                }
                else
                {
                    // Skip check when configuration is the same
                    LogWrapper.d(TAG, "Same configuration, skip checking...");
                }
            }

//            TEMP DISABLED
//            CallRefreshApplicationStatus();
        }
        catch (Exception e)
        {
            BugSenseHandler.sendException(e);
            UIUtils.DisableProxyNotification(getApplicationContext());
            LogWrapper.d(TAG, "Exception caught: disable proxy notification");
        }

    }

    public void CallRefreshApplicationStatus()
    {
        /**
         * Call the update of the UI
         * */
        LogWrapper.d(TAG, "Sending broadcast intent " + Constants.PROXY_REFRESH_UI);
        Intent intent = new Intent(Constants.PROXY_REFRESH_UI);
        getApplicationContext().sendBroadcast(intent);

        UIUtils.UpdateStatusBarNotification(ApplicationGlobals.getInstance().getCachedConfiguration(), getApplicationContext());
    }
}