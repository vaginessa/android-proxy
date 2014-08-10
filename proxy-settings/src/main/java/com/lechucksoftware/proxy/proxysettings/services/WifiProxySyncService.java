package com.lechucksoftware.proxy.proxysettings.services;

import android.content.Intent;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.WiFiAPEntity;

import java.util.ArrayList;
import java.util.List;

import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.WifiNetworkId;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;

/**
 * Created by Marco on 09/03/14.
 */
public class WifiProxySyncService extends EnhancedIntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = WifiProxySyncService.class.getSimpleName();
    private boolean isHandling = false;
    private static WifiProxySyncService instance;

    public WifiProxySyncService()
    {
        super("ProxySyncService");
//        LogWrapper.v(TAG, "ProxySyncService constructor");
    }

    public static WifiProxySyncService getInstance()
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
        if (intent != null && intent.hasExtra(WifiProxySyncService.CALLER_INTENT))
        {
            Intent caller = (Intent) intent.getExtras().get(WifiProxySyncService.CALLER_INTENT);

            if (caller != null && caller.hasExtra(Intents.UPDATED_WIFI))
            {
                WifiNetworkId wifiId = (WifiNetworkId) caller.getExtras().get(Intents.UPDATED_WIFI);
                if (wifiId != null)
                {
                    wiFiAPConfig = App.getProxyManager().getConfiguration(wifiId);
                }
            }
        }

        List<WiFiAPConfig> configsToCheck;
        // Disable until the Wi-Fi ap will be persisted on DB

//        if (wiFiAPConfig != null)
//        {
//            configsToCheck = new ArrayList<WiFiAPConfig>();
//            configsToCheck.add(wiFiAPConfig);
//        }
//        else
//        {
            configsToCheck = App.getProxyManager().getSortedConfigurationsList();
//        }

        syncProxyConfigurations(configsToCheck);

        isHandling = false;
    }

    private void syncProxyConfigurations(List<WiFiAPConfig> configurations)
    {
        App.getLogger().startTrace(TAG, "syncProxyConfigurations", Log.ASSERT);

        List<Long> inUseProxies = new ArrayList<Long>();

        int foundNewWifiAp = 0;
        int foundUpdateWifiAp = 0;
        int foundNewProxy = 0;
        int foundUpdateProxy = 0;

        if (configurations != null && !configurations.isEmpty())
        {
            App.getLogger().d(TAG, String.format("Analyzing %d Wi-Fi AP configurations", configurations.size()));

            for (WiFiAPConfig conf : configurations)
            {
                try
                {
//                    App.getLogger().d(TAG, "Checking Wi-Fi AP: " + conf.getSSID());
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

            App.getDBManager().setInUseFlag(inUseProxies.toArray(new Long[inUseProxies.size()]));

            long proxiesCount = App.getDBManager().getProxiesCount();
            App.getLogger().a(TAG, String.format("Found proxies: NEW: %d, UPDATED: %d, TOT: %d", foundNewProxy, foundUpdateProxy, proxiesCount));
        }

        App.getLogger().stopTrace(TAG, "syncProxyConfigurations", Log.ASSERT);
    }
}
