package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.Date;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLConstants;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.enums.ProxyCheckOptions;
import be.shouldit.proxy.lib.utils.ProxyUtils;

public class ProxySettingsCheckerService extends EnhancedIntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = ProxySettingsCheckerService.class.getSimpleName();
    private boolean isHandling = false;
    private static ProxySettingsCheckerService instance;

    public ProxySettingsCheckerService()
    {
        super("ProxySettingsCheckerService");
//        LogWrapper.v(TAG, "ProxySettingsCheckerService constructor");
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

        App.getLogger().startTrace(TAG, "checkProxySettings", Log.DEBUG);

        handleIntentLogic(intent);

        App.getLogger().stopTrace(TAG, "checkProxySettings", Log.DEBUG);
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
                App.getLogger().logIntent(TAG, "onHandleIntent: ", callerIntent, Log.DEBUG);

                if (callerAction.equals(Intents.PROXY_SETTINGS_STARTED)
                        || callerAction.equals(Intents.PROXY_SETTINGS_MANUAL_REFRESH)
                        || callerAction.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                        || callerAction.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                        || callerAction.equals(Intents.WIFI_AP_UPDATED)
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
                        App.getLogger().d(TAG, "Do not check proxy settings if network is not available!");
                    }
                }
                else
                {
                    App.getLogger().e(TAG, "Intent ACTION not handled: " + callerAction);
                }
            }
            else
            {
                App.getLogger().e(TAG, "Received Intent NULL ACTION");
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
        App.getLogger().startTrace(TAG, "checkProxySettings", Log.DEBUG);

        try
        {
//            callRefreshApplicationStatus();
            App.getProxyManager().updateProxyConfigurationList();
            WiFiAPConfig conf = App.getProxyManager().getCurrentConfiguration();
            NetworkInfo ni = APL.getConnectivityManager().getActiveNetworkInfo();

            if (ni != null && ni.isAvailable() && ni.isConnected())
            {
                boolean checkNewConf = false;
                if (conf != null)
                {
                    App.getLogger().d(TAG, "Checking configuration: " + conf.toShortString());

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
                            App.getLogger().d(TAG, "Same configuration for 30 minutes check again!");
                        }
                    }
                    else
                    {
                        App.getLogger().d(TAG, "Current configuration has not been checked -> needs to check the proxy status");
                        checkNewConf = true;
                    }
                }
                else
                {
                    // newconf cannot be null!!
                    App.getLogger().d(TAG, "Not found valid configuration");
//                    App.getEventsReporter().sendException(new Exception("Cannot have a null WiFiAPConfig"));
                }

                if (checkNewConf)
                {
                    App.getLogger().d(TAG, "Changed current proxy configuration: calling refresh of proxy status");
                    ProxyUtils.acquireProxyStatus(conf, conf.status, ProxyCheckOptions.ALL, APLConstants.DEFAULT_TIMEOUT);
                    App.getLogger().d(TAG, "Acquired refreshed proxy configuration: " + conf.toShortString());
                }
                else
                {
                    // Skip check when configuration is the same
                    App.getLogger().d(TAG, "No need to check the configuration. Skip...");
                }
            }
            else
            {
                App.getLogger().d(TAG, "Network is not available, cannot check proxy settings");
            }

            callRefreshApplicationStatus();
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
            UIUtils.DisableProxyNotification(App.getInstance());
            e.printStackTrace();
        }

        App.getLogger().stopTrace(TAG, "checkProxySettings", Log.DEBUG);
    }

    public void callRefreshApplicationStatus()
    {
        /**
         * Call the update of the UI
         * */
        App.getLogger().d(TAG, "Sending broadcast intent " + Intents.PROXY_REFRESH_UI);
        Intent intent = new Intent(Intents.PROXY_REFRESH_UI);
        getApplicationContext().sendBroadcast(intent);

        WiFiAPConfig wiFiAPConfig = App.getProxyManager().getCachedConfiguration();
        if (wiFiAPConfig == null)
            wiFiAPConfig = App.getProxyManager().getCurrentConfiguration();

        if (wiFiAPConfig != null)
        {
            UIUtils.UpdateStatusBarNotification(wiFiAPConfig, getApplicationContext());
        }
    }
}
