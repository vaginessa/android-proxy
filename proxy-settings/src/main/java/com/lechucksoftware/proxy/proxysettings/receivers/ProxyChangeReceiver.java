package com.lechucksoftware.proxy.proxysettings.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.services.MaintenanceService;
import com.lechucksoftware.proxy.proxysettings.services.WifiStatusUpdateService;
import com.lechucksoftware.proxy.proxysettings.services.WifiSyncService;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import be.shouldit.proxy.lib.constants.APLIntents;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.constants.APLReflectionConstants;
import timber.log.Timber;

public class ProxyChangeReceiver extends BroadcastReceiver
{
    public static String TAG = ProxyChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        App.getLogutils().logIntent(TAG, intent, Log.DEBUG, true);

        if (intent.getAction().equals(Intents.PROXY_SETTINGS_STARTED))
        {
            // INTERNAL (PS) : Called when Proxy Settings is started
//            callProxySettingsChecker(context, intent);
            callWifiSyncService(context, intent);
            callMaintenanceService(context, intent);
        }
//        else if (intent.getAction().equals(Intents.WIFI_AP_UPDATED))
//        {
//            // INTERNAL (PS): Called when a Wi-Fi configuration is written to the device
//            //App.getLogutils().logIntent(TAG, intent, Log.DEBUG);
//            //callProxySettingsChecker(context, intent);
//            //callWifiSyncService(context, intent);
//        }
        else if (intent.getAction().equals(Intents.PROXY_SAVED))
        {
            // INTERNAL (PS) : Saved a Proxy configuration on DB
            callMaintenanceService(context, intent);
        }
        else if (intent.getAction().equals(APLReflectionConstants.CONFIGURED_NETWORKS_CHANGED_ACTION))
        {
            // Called when a Wi-Fi configured networks is changed
            callWifiSyncService(context, intent);
        }
        else if (
                    // Connection type change (switch between 3G/WiFi)
                    intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)

                    // Scan results available information
                    || intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

                    // Wifi state changed action
                    || intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                )
        {
            callUpdatedWifStatusService(context, intent);
        }
        else if (
                    // INTERNAL (PS) : Called to refreshUI the UI of Proxy Settings
                    intent.getAction().equals(Intents.PROXY_REFRESH_UI)

                    // INTERNAL (APL): Called when an updated status on the check of a configuration is available
                    || intent.getAction().equals(APLIntents.APL_UPDATED_PROXY_STATUS_CHECK)
                )
        {
            App.getLogutils().logIntent(TAG, intent, Log.DEBUG);

            WiFiAPConfig wiFiAPConfig = App.getWifiNetworksManager().getCachedConfiguration();
            if (wiFiAPConfig == null)
                wiFiAPConfig = App.getWifiNetworksManager().updateCurrentConfiguration();

            if (wiFiAPConfig != null)
            {
                UIUtils.UpdateStatusBarNotification(wiFiAPConfig, context);
            }
        }
        else
        {
            App.getLogutils().logIntent(TAG, intent, Log.ERROR);
            Timber.e(TAG, "Intent not found into handled list!");
        }
    }

    public static void callWifiSyncService(Context context, Intent intent)
    {
//        if (App.getInstance().wifiActionEnabled)
        {
            try
            {
                Intent serviceIntent = new Intent(context, WifiSyncService.class);
                serviceIntent.putExtra(WifiSyncService.CALLER_INTENT, intent);
                context.startService(serviceIntent);
            }
            catch (Exception e)
            {
                App.getEventsReporter().sendException(e);
            }
        }
    }

    public static void callUpdatedWifStatusService(Context context, Intent intent)
    {
        try
        {
            Intent serviceIntent = new Intent(context, WifiStatusUpdateService.class);
            serviceIntent.putExtra(WifiStatusUpdateService.CALLER_INTENT, intent);
            context.startService(serviceIntent);
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }

    private void callProxySettingsChecker(Context context, Intent intent)
    {
        //Call the ProxySettingsCheckerService for update the network status
        Log.e(TAG,"PROXY SETTINGS CHECKER DISABLED");
        return;

//        ProxySettingsCheckerService instance = ProxySettingsCheckerService.getInstance();
//        if (instance != null)
//        {
//            if (instance.isHandlingIntent())
//            {
//                App.getLogutils().d(TAG, "Already checking proxy.. skip another call");
//                return;
//            }
//        }
//
//        if (App.getInstance().wifiActionEnabled)
//        {
//            try
//            {
//                Intent serviceIntent = new Intent(context, ProxySettingsCheckerService.class);
//                serviceIntent.putExtra(ProxySettingsCheckerService.CALLER_INTENT, intent);
//                context.startService(serviceIntent);
//            }
//            catch (Exception e)
//            {
//                App.getEventsReporter().sendException(e);
//            }
//        }
    }

    private void callMaintenanceService(Context context, Intent intent)
    {
        //Call the MaintenanceService for maintenance tasks
        MaintenanceService instance = MaintenanceService.getInstance();
        if (instance != null)
        {
            if (instance.isHandlingIntent())
            {
                Timber.d("Already working.. skip another call");
                return;
            }
        }

        try
        {
            Intent serviceIntent = new Intent(context, MaintenanceService.class);
            serviceIntent.putExtra(MaintenanceService.CALLER_INTENT, intent);
            context.startService(serviceIntent);
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }
}
