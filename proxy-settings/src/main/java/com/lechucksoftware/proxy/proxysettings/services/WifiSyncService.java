package com.lechucksoftware.proxy.proxysettings.services;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;

import java.util.ArrayList;
import java.util.List;

import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.WifiNetworkId;
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
        super("ProxySyncService");
//        LogWrapper.v(TAG, "ProxySyncService constructor");
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

        WiFiAPConfig wiFiAPConfig = null;
        if (intent != null && intent.hasExtra(WifiSyncService.CALLER_INTENT))
        {
            Intent caller = (Intent) intent.getExtras().get(WifiSyncService.CALLER_INTENT);

            if (caller != null)
            {
                if (caller.getAction().equals(Intents.WIFI_AP_UPDATED))
                {
                    if (caller.hasExtra(Intents.UPDATED_WIFI))
                    {
                        WifiNetworkId wifiId = (WifiNetworkId) caller.getExtras().get(Intents.UPDATED_WIFI);
                        if (wifiId != null)
                        {
                            wiFiAPConfig = App.getProxyManager().getConfiguration(wifiId);
                        }
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
                            WifiNetworkId wifiId = new WifiNetworkId(ProxyUtils.cleanUpSSID(wifiConf.SSID), ProxyUtils.getSecurity(wifiConf));
                            wiFiAPConfig = App.getProxyManager().getConfiguration(wifiId);
                        }
                    }

                    if (caller.hasExtra(APLReflectionConstants.EXTRA_MULTIPLE_NETWORKS_CHANGED))
                    {
                        App.getLogger().e(TAG,"EXTRA_MULTIPLE_NETWORKS_CHANGED not handled");
                        App.getLogger().logIntent(TAG,caller,Log.ERROR);
                    }
                }
            }
        }

        List<WiFiAPConfig> configsToCheck;
        // Disable until the Wi-Fi ap will be persisted on DB

        if (wiFiAPConfig != null)
        {
            configsToCheck = new ArrayList<WiFiAPConfig>();
            configsToCheck.add(wiFiAPConfig);
        }
        else
        {
            configsToCheck = App.getProxyManager().getSortedConfigurationsList();
        }

        syncProxyConfigurations(configsToCheck);

        isHandling = false;
    }

    private void syncProxyConfigurations(List<WiFiAPConfig> configurations)
    {
        App.getLogger().startTrace(TAG, "syncProxyConfigurations", Log.ASSERT);

        if (configurations != null && !configurations.isEmpty())
        {
            App.getLogger().d(TAG, String.format("Analyzing %d Wi-Fi AP configurations", configurations.size()));

            for (WiFiAPConfig conf : configurations)
            {
                try
                {
                    App.getLogger().d(TAG, "Checking Wi-Fi AP: " + ProxyUtils.convertToQuotedString(conf.getSSID()));
                    App.getDBManager().upsertWifiAP(conf);

//                    long wifiConfId = App.getDBManager().findWifiAp(conf);
//                    if (wifiConfId != -1)
//                    {
//                        foundUpdateWifiAp++;
//
//                        WiFiAPEntity wiFiAPEntity = App.getDBManager().getWifiAP(wifiConfId);
//
//
//
//
//                        if (wiFiAPEntity.proxySetting == conf.getProxySetting())
//                        {
//                            ProxyEntity linkedProxy = wiFiAPEntity.getProxy();
//                            if (linkedProxy.host == conf.getProxyHost() &&
//                                linkedProxy.port == conf.getProxyPort() &&
//                                linkedProxy.exclusion == conf.getProxyExclusionList())
//                            {
//                                // Same saved configuration -> do nothing!
//                                changedConfiguration = false;
//                            }
//                        }
//
//                        if (changedConfiguration)
//                        {
//                            WiFiAPEntity updatedWifiAp = new WiFiAPEntity(wiFiAPEntity);
//                            updatedWifiAp.proxySetting = conf.getProxySetting();
//
//                            updatedWifiAp.getProxy().host = conf.getProxyHost();
//                            updatedWifiAp.getProxy().port = conf.getProxyPort();
//                            updatedWifiAp.getProxy().exclusion = conf.getProxyExclusionList();
//
//                            App.getDBManager().upsertWifiAP(updatedWifiAp);
//                        }
//                    }
//                    else
//                    {
//                        foundNewWifiAp++;
//
//                        WiFiAPEntity updatedWifiAp = new WiFiAPEntity();
//                        updatedWifiAp.ssid = conf.ssid;
//                        updatedWifiAp.securityType = conf.securityType;
//                        App.getDBManager().createWifiAp()
//
//
//                        if (updatedWifiAp.proxySetting == ProxySetting.STATIC)
//                        {
//                            updatedWifiAp.getProxy().host = conf.getProxyHost();
//                            updatedWifiAp.getProxy().port = conf.getProxyPort();
//                            updatedWifiAp.getProxy().exclusion = conf.getProxyExclusionList();
//                        }
//
//                        App.getDBManager().upsertWifiAP(updatedWifiAp);
//                    }

//                    if (conf.getProxySetting() == ProxySetting.STATIC && conf.securityType != SecurityType.SECURITY_EAP)
//                    {
//                        if (conf.isValidProxyConfiguration())
//                        {
//                            App.getLogger().d(TAG, "Found proxy: " + conf.toShortString());
//
//                            long proxyId = App.getDBManager().findProxy(conf);
//                            ProxyEntity pd = null;
//                            if (proxyId != -1)
//                            {
//                                // Proxy already saved into DB
//                                pd = App.getDBManager().getProxy(proxyId);
//                                inUseProxies.add(pd.getId());
//                                foundUpdateProxy++;
//                            }
//                            else
//                            {
//                                // Found new proxy
//                                pd = new ProxyEntity();
//                                pd.host = conf.getProxyHost();
//                                pd.port = conf.getProxyPort();
//                                pd.exclusion = conf.getProxyExclusionList();
//                                pd.setInUse(true);
//                                pd = App.getDBManager().upsertProxy(pd);
//
//                                foundNewProxy++;
//                            }
//
//                            inUseProxies.add(pd.getId());
//                        }
//                        else
//                        {
////                            App.getLogger().d(TAG, "Found not valid proxy: " + conf.toShortString());
//                        }
//                    }
//                    else
//                    {
////                        App.getLogger().d(TAG, "Proxy not enabled or cannot be read: " + conf.toShortString());
//                    }
                }
                catch (Exception e)
                {
                    App.getEventsReporter().sendException(new Exception("Exception during ProxySyncService", e));
                }
            }
        }

        App.getLogger().stopTrace(TAG, "syncProxyConfigurations", Log.ASSERT);
    }
}
