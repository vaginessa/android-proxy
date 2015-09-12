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

import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.constants.APLIntents;
import be.shouldit.proxy.lib.constants.APLReflectionConstants;
import be.shouldit.proxy.lib.logging.TraceUtils;
import timber.log.Timber;

public class ProxyChangeReceiver extends BroadcastReceiver
{
    public static String TAG = ProxyChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        App.getTraceUtils().logIntent(TAG, intent, Log.DEBUG);

        switch (intent.getAction())
        {
            case Intents.PROXY_SETTINGS_STARTED:
                // INTERNAL (PS) : Called when Proxy Settings is started
                callWifiSyncService(context, intent);
                callMaintenanceService(context, intent);
                break;

            case Intents.PROXY_SAVED:
                // INTERNAL (PS) : Saved a Proxy configuration on DB
                callMaintenanceService(context, intent);
                break;

            case APLReflectionConstants.CONFIGURED_NETWORKS_CHANGED_ACTION:
                // Called when a Wi-Fi configured networks is changed
                callWifiSyncService(context, intent);
                break;

            case ConnectivityManager.CONNECTIVITY_ACTION: // Connection type change (switch between 3G/WiFi)
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION: // Scan results available information
            case WifiManager.WIFI_STATE_CHANGED_ACTION: // Wifi state changed action
                callUpdatedWifStatusService(context, intent);
                break;

            case Intents.PROXY_REFRESH_UI:
            case APLIntents.APL_UPDATED_PROXY_STATUS_CHECK:
                {
                    TraceUtils.logIntent(TAG, intent, Log.DEBUG);

                    WiFiApConfig wiFiApConfig = App.getWifiNetworksManager().getCachedConfiguration();
                    if (wiFiApConfig == null)
                        wiFiApConfig = App.getWifiNetworksManager().updateCurrentConfiguration();

                    if (wiFiApConfig != null)
                    {
                        UIUtils.UpdateStatusBarNotification(wiFiApConfig, context);
                    }
                }
                break;

            default:
                {
                    TraceUtils.logIntent(TAG, intent, Log.ERROR);
                    Timber.e(TAG, "Intent not found into handled list!");
                }
                break;
        }
    }

    public static void callWifiSyncService(Context context, Intent intent)
    {
        {
            try
            {
                Intent serviceIntent = new Intent(context, WifiSyncService.class);
                serviceIntent.putExtra(WifiSyncService.CALLER_INTENT, intent);
                context.startService(serviceIntent);
            }
            catch (Exception e)
            {
                Timber.e(e,"Exception during callWifiSyncService");
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
            Timber.e(e,"Exception during callUpdatedWifStatusService");
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
//                App.getTraceUtils().d(TAG, "Already checking proxy.. skip another call");
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
            Timber.e(e,"Exception during callMaintenanceService");
        }
    }
}
