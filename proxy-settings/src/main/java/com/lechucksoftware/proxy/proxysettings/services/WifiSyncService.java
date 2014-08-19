package com.lechucksoftware.proxy.proxysettings.services;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.WiFiAPEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.constants.APLReflectionConstants;
import be.shouldit.proxy.lib.utils.ProxyUtils;

/**
 * Created by Marco on 09/03/14.
 */
public class WifiSyncService extends EnhancedIntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = WifiSyncService.class.getSimpleName();
    private boolean isHandling = false;
    private static WifiSyncService instance;

    public WifiSyncService()
    {
        super("WifiSyncService");
    }

    public static WifiSyncService getInstance()
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

        App.getLogger().startTrace(TAG, "syncAP", Log.ASSERT);
        List<APLNetworkId> configsToCheck = getConfigsToCheck(intent);
        App.getLogger().getPartial(TAG, "syncAP", Log.ASSERT);
        syncProxyConfigurations(configsToCheck);
        App.getLogger().stopTrace(TAG, "syncAP", Log.ASSERT);

        isHandling = false;
    }

    private List<APLNetworkId> getConfigsToCheck(Intent intent)
    {
        List<APLNetworkId> networkIds = new ArrayList<APLNetworkId>();

        if (intent != null && intent.hasExtra(WifiSyncService.CALLER_INTENT))
        {
            Intent caller = (Intent) intent.getExtras().get(WifiSyncService.CALLER_INTENT);

            if (caller != null)
            {
                if (caller.getAction().equals(Intents.WIFI_AP_UPDATED))
                {
                    if (caller.hasExtra(Intents.UPDATED_WIFI))
                    {
                        APLNetworkId wifiId = (APLNetworkId) caller.getExtras().get(Intents.UPDATED_WIFI);
                        networkIds.add(wifiId);
                    }
                }
                else
                if (caller.getAction().equals(APLReflectionConstants.CONFIGURED_NETWORKS_CHANGED_ACTION))
                {
                    if (caller.hasExtra(APLReflectionConstants.EXTRA_WIFI_CONFIGURATION))
                    {
                        WifiConfiguration wifiConf = (WifiConfiguration) caller.getExtras().get(APLReflectionConstants.EXTRA_WIFI_CONFIGURATION);
                        if (wifiConf != null)
                        {
                            APLNetworkId wifiId = new APLNetworkId(ProxyUtils.cleanUpSSID(wifiConf.SSID), ProxyUtils.getSecurity(wifiConf));
                            networkIds.add(wifiId);
                        }
                    }

//                    if (caller.hasExtra(APLReflectionConstants.EXTRA_MULTIPLE_NETWORKS_CHANGED))
//                    {
//                        App.getLogger().e(TAG,"EXTRA_MULTIPLE_NETWORKS_CHANGED not handled");
//                        App.getLogger().logIntent(TAG, caller, Log.ERROR, true);
//                    }
                }
            }
        }

        return networkIds;
    }

    private void syncProxyConfigurations(List<APLNetworkId> configurations)
    {
        Map<APLNetworkId, WifiConfiguration> configuredNetworks = APL.getConfiguredNetworks();

        if (configurations.isEmpty())
        {
            App.getLogger().d(TAG,"No configurations specificed, must sync all of them!");
            configurations.addAll(configuredNetworks.keySet());
        }

        App.getLogger().d(TAG, String.format("Analyzing %d Wi-Fi AP configurations", configurations.size()));

        for (APLNetworkId aplNetworkId : configurations)
        {
            try
            {
//                App.getLogger().d(TAG, "Checking Wi-Fi AP: " + wifiId.toString());
                if (configuredNetworks.containsKey(aplNetworkId))
                {
                    WifiConfiguration wifiConfiguration = configuredNetworks.get(aplNetworkId);
                    WiFiAPConfig wiFiAPConfig = APL.getWiFiAPConfiguration(wifiConfiguration);
                    WiFiAPEntity result = App.getDBManager().upsertWifiAP(wiFiAPConfig);

                    App.getWifiNetworksManager().updateWifiConfig(wiFiAPConfig, result);
                    App.getLogger().getPartial(TAG, "syncAP", "Upserted: " + result.toString(), Log.DEBUG);
                }
                else
                {
                    App.getDBManager().deleteWifiAP(aplNetworkId);
                    App.getWifiNetworksManager().removeWifiConfig(aplNetworkId);
                    App.getLogger().getPartial(TAG, "syncAP", "Deleted: " + aplNetworkId.toString(), Log.DEBUG);
                }
            }
            catch (Exception e)
            {
                App.getEventsReporter().sendException(new Exception("Exception during ProxySyncService", e));
            }
        }

        App.getLogger().d(TAG, "Sending broadcast intent " + Intents.PROXY_REFRESH_UI);
        Intent intent = new Intent(Intents.PROXY_REFRESH_UI);
        getApplicationContext().sendBroadcast(intent);
    }
}
