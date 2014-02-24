package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.*;
import com.shouldit.proxy.lib.enums.ProxyCheckOptions;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.utils.ProxyUtils;

import java.util.Date;

public class ProxySettingsCheckerService extends IntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = ProxySettingsCheckerService.class.getSimpleName();
    private boolean isHandling = false;
    private static ProxySettingsCheckerService instance;

    public ProxySettingsCheckerService()
    {
        super("ProxySettingsCheckerService");
        LogWrapper.v(TAG, "ProxySettingsCheckerService constructor");
    }

    public static ProxySettingsCheckerService getInstance()
    {
        return instance;
    }

    public boolean isHandlingIntent()
    {
        return isHandling;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        instance = this;
        isHandling = true;

        LogWrapper.startTrace(TAG, "checkProxySettings", Log.DEBUG);

        handleIntentLogic(intent);

        LogWrapper.stopTrace(TAG, "checkProxySettings", Log.DEBUG);
        isHandling = false;
    }

    private void handleIntentLogic(Intent intent)
    {
        //        LogWrapper.logIntent(TAG, "onHandleIntent: ", intent, Log.VERBOSE);
        if (intent != null && intent.hasExtra(CALLER_INTENT))
        {
            Intent callerIntent = (Intent) intent.getExtras().get(CALLER_INTENT);

            if (callerIntent != null)
            {
                String callerAction = callerIntent.getAction();
                LogWrapper.logIntent(TAG, "onHandleIntent: ", callerIntent, Log.DEBUG);

                if (callerAction.equals(Intents.PROXY_SETTINGS_STARTED)
                        || callerAction.equals(Intents.PROXY_SETTINGS_MANUAL_REFRESH)
                        || callerAction.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                        || callerAction.equals(APLIntents.APL_UPDATED_PROXY_CONFIGURATION)
                        || callerAction.equals(Proxy.PROXY_CHANGE_ACTION)
                        || callerAction.equals("android.net.wifi.CONFIGURED_NETWORKS_CHANGE"))
                {
                    checkProxySettings();
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
                    NetworkInfo ni = APL.getConnectivityManager().getActiveNetworkInfo();

                    if (ni != null && ni.isConnected())
                    {
                        checkProxySettings();
                    }
                    else
                    {
                        LogWrapper.d(TAG, "Do not check proxy settings if network is not available!");
                    }
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

        return;
    }

    @Override
    public void onDestroy()
    {
//        LogWrapper.d(TAG, "ProxySettingsCheckerService destroying");
    }

    private void checkProxySettings()
    {
        LogWrapper.startTrace(TAG,"checkProxySettings", Log.DEBUG);

        try
        {
//            CallRefreshApplicationStatus();

            ApplicationGlobals.getProxyManager().updateProxyConfigurationList();
            ProxyConfiguration conf = ApplicationGlobals.getProxyManager().getCurrentConfiguration();

            NetworkInfo ni = APL.getConnectivityManager().getActiveNetworkInfo();

            if (ni != null && ni.isAvailable() && ni.isConnected())
            {
                boolean checkNewConf = false;
                if (conf != null)
                {
                    LogWrapper.d(TAG, "Checking configuration: " + conf.toShortString());

                    if (conf.status != null
                            && conf.status.checkedDate != null)
                    {
                        long diffMsec = new Date().getTime() - conf.status.checkedDate.getTime();
                        long diffSeconds = diffMsec / 1000;
                        long diffMinutes = diffMsec / (60 * 1000);

                        if (diffMinutes > 30)
                        {
                            checkNewConf = true;
                            // Skip check when configuration is the same
                            LogWrapper.d(TAG, "Same configuration for 30 minutes check again!");
                        }
                    }
                    else
                    {
                        LogWrapper.d(TAG, "Current configuration has not been checked -> needs to check the proxy status");
                        checkNewConf = true;
                    }
                }
                else
                {
                    // newconf cannot be null!!
                    LogWrapper.d(TAG, "Not found new configuration -> needs to check the proxy status");
                    EventReportingUtils.sendException(new Exception("Cannot have a null ProxyConfiguration"));
                }

                if (checkNewConf)
                {
                    LogWrapper.d(TAG, "Changed current proxy configuration: calling refresh of proxy status");
                    ProxyUtils.acquireProxyStatus(conf, conf.status, ProxyCheckOptions.ALL, APLConstants.DEFAULT_TIMEOUT);
                    LogWrapper.d(TAG, "Acquired refreshed proxy configuration: " + conf.toShortString());
                }
                else
                {
                    // Skip check when configuration is the same
                    LogWrapper.d(TAG, "No need to check the configuration. Skip...");
                }
            }
            else
            {
                LogWrapper.d(TAG, "Network is not available, cannot check proxy settings");
            }

            CallRefreshApplicationStatus();
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
            UIUtils.DisableProxyNotification(ApplicationGlobals.getInstance());
            e.printStackTrace();
        }

        LogWrapper.stopTrace(TAG,"checkProxySettings", Log.DEBUG);
    }

    public void CallRefreshApplicationStatus()
    {
        /**
         * Call the update of the UI
         * */
        LogWrapper.d(TAG, "Sending broadcast intent " + Intents.PROXY_REFRESH_UI);
        Intent intent = new Intent(Intents.PROXY_REFRESH_UI);
        getApplicationContext().sendBroadcast(intent);

        UIUtils.UpdateStatusBarNotification(ApplicationGlobals.getProxyManager().getCachedConfiguration(), getApplicationContext());
    }
}