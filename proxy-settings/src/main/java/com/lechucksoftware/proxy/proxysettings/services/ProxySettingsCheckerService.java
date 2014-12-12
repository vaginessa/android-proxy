package com.lechucksoftware.proxy.proxysettings.services;

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
import be.shouldit.proxy.lib.constants.APLConstants;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.enums.ProxyCheckOptions;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import timber.log.Timber;

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

        App.getTraceUtils().startTrace(TAG, "checkProxySettings", Log.DEBUG);

        handleIntentLogic(intent);

        App.getTraceUtils().stopTrace(TAG, "checkProxySettings", Log.DEBUG);
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
                App.getTraceUtils().logIntent(TAG, "onHandleIntent: ", callerIntent, Log.DEBUG);

                if (callerAction.equals(Intents.PROXY_SETTINGS_STARTED)
                        || callerAction.equals(Intents.PROXY_SETTINGS_MANUAL_REFRESH)
                        || callerAction.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                        || callerAction.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
//                        || callerAction.equals(Intents.WIFI_AP_UPDATED)
                        || callerAction.equals(Proxy.PROXY_CHANGE_ACTION)
                        || callerAction.equals("android.net.wifi.CONFIGURED_NETWORKS_CHANGED_ACTION"))
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
                        Timber.d("Do not check proxy settings if network is not available!");
                    }
                }
                else
                {
                    Timber.e("Intent ACTION not handled: " + callerAction);
                }
            }
            else
            {
                Timber.e("Received Intent NULL ACTION");
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
        App.getTraceUtils().startTrace(TAG, "checkProxySettings", Log.DEBUG);

        try
        {
//            callRefreshApplicationStatus();
//            App.getWifiNetworksManager().updateProxyConfigurationList();
            WiFiAPConfig conf = App.getWifiNetworksManager().updateCurrentConfiguration();
            NetworkInfo ni = APL.getConnectivityManager().getActiveNetworkInfo();

            if (ni != null && ni.isAvailable() && ni.isConnected())
            {
                boolean checkNewConf = false;
                if (conf != null)
                {
                    Timber.d("Checking configuration: " + conf.toShortString());

                    if (conf.getStatus() != null
                            && conf.getStatus().checkedDate != null)
                    {
                        long diffMsec = new Date().getTime() - conf.getStatus().checkedDate.getTime();
                        long diffSeconds = diffMsec / 1000;
                        long diffMinutes = diffMsec / (60 * 1000);

                        if (diffMinutes > 30)
                        {
                            checkNewConf = true;
                            // Skip check when configuration is the same
                            Timber.d("Same configuration for 30 minutes check again!");
                        }
                    }
                    else
                    {
                        Timber.d("Current configuration has not been checked -> needs to check the proxy status");
                        checkNewConf = true;
                    }
                }
                else
                {
                    // newconf cannot be null!!
                    Timber.d("Not found valid configuration");
//                    App.getEventsReporter().sendException(new Exception("Cannot have a null WiFiAPConfig"));
                }

                if (checkNewConf)
                {
                    Timber.d("Changed current proxy configuration: calling refresh of proxy status");
                    ProxyUtils.acquireProxyStatus(conf, conf.getStatus(), ProxyCheckOptions.ALL, APLConstants.DEFAULT_TIMEOUT);
                    Timber.d("Acquired refreshed proxy configuration: " + conf.toShortString());
                }
                else
                {
                    // Skip check when configuration is the same
                    Timber.d("No need to check the configuration. Skip...");
                }
            }
            else
            {
                Timber.d("Network is not available, cannot check proxy settings");
            }

            callRefreshApplicationStatus();
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
            UIUtils.DisableProxyNotification(App.getInstance());
            e.printStackTrace();
        }

        App.getTraceUtils().stopTrace(TAG, "checkProxySettings", Log.DEBUG);
    }

    public void callRefreshApplicationStatus()
    {
        /**
         * Call the update of the UI
         * */
        Timber.d("Sending broadcast intent " + Intents.PROXY_REFRESH_UI);
        Intent intent = new Intent(Intents.PROXY_REFRESH_UI);
        getApplicationContext().sendBroadcast(intent);

        WiFiAPConfig wiFiAPConfig = App.getWifiNetworksManager().getCachedConfiguration();
        if (wiFiAPConfig == null)
            wiFiAPConfig = App.getWifiNetworksManager().updateCurrentConfiguration();

        if (wiFiAPConfig != null)
        {
            UIUtils.UpdateStatusBarNotification(wiFiAPConfig, getApplicationContext());
        }
    }
}
