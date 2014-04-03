package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.enums.SecurityType;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 09/03/14.
 */
public class ProxySyncService extends IntentService
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

        syncProxyConfigurations();

        isHandling = false;
    }

    private void syncProxyConfigurations()
    {
        App.getLogger().startTrace(TAG, "syncProxyConfigurations", Log.DEBUG);

        List<ProxyConfiguration> configurations =  App.getProxyManager().getSortedConfigurationsList();
        List<Long> inUseProxies = new ArrayList<Long>();

        int foundNew = 0;
        int foundUpdate = 0;

        if (configurations != null && !configurations.isEmpty())
        {
            App.getLogger().d(TAG, String.format("Analyzing %d Wi-Fi AP configurations", configurations.size()));

            for (ProxyConfiguration conf : configurations)
            {
                try
                {
                    App.getLogger().d(TAG, "Checking Wi-Fi AP: " + conf.getSSID());

                    if (conf.getProxySettings() == ProxySetting.STATIC && conf.ap.security != SecurityType.SECURITY_EAP)
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
                            App.getLogger().d(TAG, "Found not valid proxy: " + conf.toShortString());
                        }
                    }
                    else
                    {
                        App.getLogger().d(TAG, "Proxy not enabled or cannot be read: " + conf.toShortString());
                    }
                }
                catch (Exception e)
                {
                    EventReportingUtils.sendException(new Exception("Exception during ProxySyncService",e));
                }
            }

            App.getDBManager().setInUseFlag(inUseProxies.toArray(new Long[inUseProxies.size()]));

            long proxiesCount = App.getDBManager().getProxiesCount();
            App.getLogger().d(TAG, String.format("Found proxies: NEW: %d, UPDATED: %d, TOT: %d", foundNew, foundUpdate, proxiesCount));
        }

        App.getLogger().stopTrace(TAG, "syncProxyConfigurations", Log.DEBUG);
    }
}
