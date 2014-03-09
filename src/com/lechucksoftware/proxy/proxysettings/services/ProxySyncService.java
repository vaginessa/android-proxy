package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.log.LogWrapper;

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
        LogWrapper.v(TAG, "ProxySyncService constructor");
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

        upsertFoundProxyConfigurations();

        isHandling = false;
    }

    private void upsertFoundProxyConfigurations()
    {
        LogWrapper.startTrace(TAG, "upsertFoundProxyConfigurations", Log.DEBUG);

        ApplicationGlobals.getDBManager().clearInUseFlag(null);

        LogWrapper.getPartial(TAG,"upsertFoundProxyConfigurations", Log.DEBUG);

        List<ProxyConfiguration> configurations = ApplicationGlobals.getProxyManager().getSortedConfigurationsList();

        int foundNew = 0;
        int foundUpdate = 0;

        if (configurations != null && !configurations.isEmpty())
        {
            LogWrapper.d(TAG, String.format("Analyzing %d Wi-Fi AP configurations",configurations.size()));

            for (ProxyConfiguration conf : configurations)
            {
                LogWrapper.d(TAG,conf.toShortString());

                if (conf.getProxy() != java.net.Proxy.NO_PROXY && conf.isValidProxyConfiguration())
                {
                    long proxyId = ApplicationGlobals.getDBManager().findProxy(conf.getProxyHost(),conf.getProxyPort());
                    ProxyEntity pd = null;
                    if (proxyId != -1)
                    {
                        // Proxy already saved into DB
                        pd = ApplicationGlobals.getDBManager().getProxy(proxyId);
                        pd.setInUse(true);
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
                        foundNew++;
                    }

                    ApplicationGlobals.getDBManager().upsertProxy(pd);
                }
                else
                {

                }
            }

            long proxiesCount = ApplicationGlobals.getDBManager().getProxiesCount();
            LogWrapper.d(TAG, String.format("Found proxies: NEW: %d, UPDATED: %d, TOT: %d", foundNew, foundUpdate, proxiesCount));
        }

        LogWrapper.stopTrace(TAG, "upsertFoundProxyConfigurations", Log.DEBUG);
    }
}
