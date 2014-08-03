package com.lechucksoftware.proxy.proxysettings.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.services.MaintenanceService;
import com.lechucksoftware.proxy.proxysettings.services.ProxySettingsCheckerService;
import com.lechucksoftware.proxy.proxysettings.services.ProxySyncService;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import be.shouldit.proxy.lib.APLIntents;

public class ProxyChangeReceiver extends BroadcastReceiver
{
    public static String TAG = ProxyChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intents.PROXY_SETTINGS_STARTED))
        {
            // INTERNAL (PS) : Called when Proxy Settings is started

            App.getLogger().logIntent(TAG, intent, Log.DEBUG);
            callProxySettingsChecker(context, intent);
            callSyncProxyService(context, intent);
            callMaintenanceService(context, intent);
        }
        else if (intent.getAction().equals(Intents.WIFI_AP_UPDATED))
        {
            // INTERNAL (PS): Called when a proxy configuration is written
            App.getLogger().logIntent(TAG, intent, Log.DEBUG);
            callProxySettingsChecker(context, intent);
            callSyncProxyService(context, intent);
        }
        else if (intent.getAction().equals(Intents.PROXY_SAVED))
        {
            // INTERNAL (PS) : Saved a Proxy configuration on DB
            App.getLogger().logIntent(TAG, intent, Log.DEBUG);
            callMaintenanceService(context, intent);
        }
        else if (
                    // INTERNAL (PS) : Called when Proxy Settings needs to refreshUI the Proxy status
                    intent.getAction().equals(Intents.PROXY_SETTINGS_MANUAL_REFRESH)

                    // Connection type change (switch between 3G/WiFi)
                    || intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)

                    // Scan results available information
                    || intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

                    // Wifi state changed action
                    || intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)

                    // Called when a Proxy Configuration is changed
                    || intent.getAction().equals(Proxy.PROXY_CHANGE_ACTION)

                    || intent.getAction().equals("android.net.wifi.CONFIGURED_NETWORKS_CHANGE")
                )
        {
            App.getLogger().logIntent(TAG, intent, Log.DEBUG);
            callProxySettingsChecker(context, intent);
        }
        else if (
                    // INTERNAL (PS) : Called to refreshUI the UI of Proxy Settings
                    intent.getAction().equals(Intents.PROXY_REFRESH_UI)

                    // INTERNAL (APL): Called when an updated status on the check of a configuration is available
                    || intent.getAction().equals(APLIntents.APL_UPDATED_PROXY_STATUS_CHECK)
                )
        {
            App.getLogger().logIntent(TAG, intent, Log.DEBUG);
            UIUtils.UpdateStatusBarNotification(App.getProxyManager().getCachedConfiguration(), context);
        }
        else
        {
            App.getLogger().logIntent(TAG, intent, Log.ERROR);
            App.getLogger().e(TAG, "Intent not found into handled list!");
        }
    }

    private void callSyncProxyService(Context context, Intent intent)
    {
        if (App.getInstance().wifiActionEnabled)
        {
            try
            {
                Intent serviceIntent = new Intent(context, ProxySyncService.class);
                serviceIntent.putExtra(ProxySyncService.CALLER_INTENT, intent);
                context.startService(serviceIntent);
            }
            catch (Exception e)
            {
                App.getEventsReporter().sendException(e);
            }
        }
    }

    private void callProxySettingsChecker(Context context, Intent intent)
    {
        //Call the ProxySettingsCheckerService for update the network status
        ProxySettingsCheckerService instance = ProxySettingsCheckerService.getInstance();
        if (instance != null)
        {
            if (instance.isHandlingIntent())
            {
                App.getLogger().d(TAG, "Already checking proxy.. skip another call");
                return;
            }
        }

        if (App.getInstance().wifiActionEnabled)
        {
            try
            {
                Intent serviceIntent = new Intent(context, ProxySettingsCheckerService.class);
                serviceIntent.putExtra(ProxySettingsCheckerService.CALLER_INTENT, intent);
                context.startService(serviceIntent);
            }
            catch (Exception e)
            {
                App.getEventsReporter().sendException(e);
            }
        }
    }

    private void callMaintenanceService(Context context, Intent intent)
    {
        //Call the MaintenanceService for maintenance tasks
        MaintenanceService instance = MaintenanceService.getInstance();
        if (instance != null)
        {
            if (instance.isHandlingIntent())
            {
                App.getLogger().d(TAG, "Already working.. skip another call");
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
