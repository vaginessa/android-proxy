package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.enums.SecurityType;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;

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

        syncProxyConfigurations();

        isHandling = false;
    }

    private void syncProxyConfigurations()
    {
        LogWrapper.startTrace(TAG, "syncProxyConfigurations", Log.DEBUG);

        // TODO : improve the clear and set of in use flag, adding to a single sql transaction
        ApplicationGlobals.getDBManager().clearInUseFlag(null);

        LogWrapper.getPartial(TAG,"syncProxyConfigurations", Log.DEBUG);

        List<ProxyConfiguration> configurations = ApplicationGlobals.getProxyManager().getSortedConfigurationsList();

        int foundNew = 0;
        int foundUpdate = 0;

        if (configurations != null && !configurations.isEmpty())
        {
            LogWrapper.d(TAG, String.format("Analyzing %d Wi-Fi AP configurations",configurations.size()));

            for (ProxyConfiguration conf : configurations)
            {
                if (conf.getProxySettings() == ProxySetting.STATIC && conf.ap.security != SecurityType.SECURITY_EAP)
                {
                    if (conf.isValidProxyConfiguration())
                    {
                        LogWrapper.d(TAG, "Found proxy: " + conf.toShortString());

                        long proxyId = ApplicationGlobals.getDBManager().findProxy(conf.getProxyHost(), conf.getProxyPort());
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
                        LogWrapper.d(TAG, "Found not valid proxy: " + conf.toShortString());
                    }
                }
                else
                {

                }
            }

            long proxiesCount = ApplicationGlobals.getDBManager().getProxiesCount();
            LogWrapper.d(TAG, String.format("Found proxies: NEW: %d, UPDATED: %d, TOT: %d", foundNew, foundUpdate, proxiesCount));
        }

        LogWrapper.stopTrace(TAG, "syncProxyConfigurations", Log.DEBUG);
    }
}
