package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.WifiNetworkId;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;

/**
 * Created by Marco on 09/03/14.
 */
public class ProxySyncService extends EnhancedIntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = ProxySyncService.class.getSimpleName();
    private boolean isHandling = false;
    private static ProxySyncService instance;

    public ProxySyncService()
    {
        super("ProxySyncService");
//        LogWrapper.v(TAG, "ProxySyncService constructor");
    }

    public static ProxySyncService getInstance()
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
        if (intent != null && intent.hasExtra(ProxySyncService.CALLER_INTENT))
        {
            Intent caller = (Intent) intent.getExtras().get(ProxySyncService.CALLER_INTENT);

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

        int foundNew = 0;
        int foundUpdate = 0;

        if (configurations != null && !configurations.isEmpty())
        {
            App.getLogger().d(TAG, String.format("Analyzing %d Wi-Fi AP configurations", configurations.size()));

            for (WiFiAPConfig conf : configurations)
            {
                try
                {
//                    App.getLogger().d(TAG, "Checking Wi-Fi AP: " + conf.getSSID());

                    if (conf.getProxySettings() == ProxySetting.STATIC && conf.security != SecurityType.SECURITY_EAP)
                    {
                        if (conf.isValidProxyConfiguration())
                        {
                            App.getLogger().d(TAG, "Found proxy: " + conf.toShortString());

                            long proxyId = App.getDBManager().findProxy(conf);
                            ProxyEntity pd = null;
                            if (proxyId != -1)
                            {
                                // Proxy already saved into DB
                                pd = App.getDBManager().getProxy(proxyId);
                                inUseProxies.add(pd.getId());
                                foundUpdate++;
                            }
                            else
                            {
                                // Found new proxy
                                pd = new ProxyEntity();
                                pd.host = conf.getProxyHost();
                                pd.port = conf.getProxyPort();
                                pd.exclusion = conf.getProxyExclusionList();
                                pd.setInUse(true);
                                pd = App.getDBManager().upsertProxy(pd);

                                foundNew++;
                            }

                            inUseProxies.add(pd.getId());
                        }
                        else
                        {
//                            App.getLogger().d(TAG, "Found not valid proxy: " + conf.toShortString());
                        }
                    }
                    else
                    {
//                        App.getLogger().d(TAG, "Proxy not enabled or cannot be read: " + conf.toShortString());
                    }
                }
                catch (Exception e)
                {
                    App.getEventsReporter().sendException(new Exception("Exception during ProxySyncService", e));
                }
            }

            App.getDBManager().setInUseFlag(inUseProxies.toArray(new Long[inUseProxies.size()]));

            long proxiesCount = App.getDBManager().getProxiesCount();
            App.getLogger().a(TAG, String.format("Found proxies: NEW: %d, UPDATED: %d, TOT: %d", foundNew, foundUpdate, proxiesCount));
        }

        App.getLogger().stopTrace(TAG, "syncProxyConfigurations", Log.ASSERT);
    }
}
