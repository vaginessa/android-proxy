package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.log.LogWrapper;

import java.util.List;
import java.util.Map;

public class MaintenanceService extends IntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = MaintenanceService.class.getSimpleName();
    private boolean isHandling = false;
    private static MaintenanceService instance;

    public MaintenanceService()
    {
        super("MaintenanceService");
        LogWrapper.v(TAG, "MaintenanceService constructor");
    }

    public static MaintenanceService getInstance()
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

        LogWrapper.startTrace(TAG, "maintenanceService", Log.DEBUG);

        handleIntentLogic(intent);

        LogWrapper.stopTrace(TAG, "maintenanceService", Log.DEBUG);
        isHandling = false;
    }

    private void handleIntentLogic(Intent intent)
    {
        if (intent != null && intent.hasExtra(CALLER_INTENT))
        {
            Intent callerIntent = (Intent) intent.getExtras().get(CALLER_INTENT);

            if (callerIntent != null)
            {
                try
                {
                    if (callerIntent.getAction().equals(Intents.PROXY_SETTINGS_STARTED))
                    {
                        checkDBstatus();
                        upsertFoundProxyConfigurations();
                        checkProxiesCountryCodes();
                    }
                    else if (callerIntent.getAction().equals(Intents.PROXY_SAVED))
                    {
                        checkProxiesCountryCodes();
                    }
                    else
                    {
                        LogWrapper.e(TAG,"Intent not handled: " +callerIntent.toString());
                    }
                }
                catch (Exception e)
                {
                    EventReportingUtils.sendException(new Exception("Exception during maintenanceService", e));
                }
            }
        }

        return;
    }

    private void checkDBstatus()
    {
        /**
         * Add IN USE TAG
         */
//        getInUseProxyTag();
    }

//    private TagEntity getInUseProxyTag()
//    {
//        TagEntity inUseTag = null;
//        long id  = ApplicationGlobals.getDBManager().findTag("IN USE");
//        if (id != -1)
//        {
//            inUseTag = ApplicationGlobals.getDBManager().getTag(id);
//        }
//        else
//        {
//            inUseTag = new TagEntity();
//            inUseTag.tag = "IN USE";
//            inUseTag.tagColor = UIUtils.getTagsColor(this, 0);
//            ApplicationGlobals.getDBManager().upsertTag(inUseTag);
//        }
//
//        return inUseTag;
//    }

    private void upsertFoundProxyConfigurations()
    {
        LogWrapper.startTrace(TAG, "upsertFoundProxyConfigurations", Log.DEBUG);

        ApplicationGlobals.getDBManager().clearInUseFlagForAllProxies();

        LogWrapper.getPartial(TAG,"upsertFoundProxyConfigurations", Log.DEBUG);

        List<ProxyConfiguration> configurations = ApplicationGlobals.getProxyManager().getSortedConfigurationsList();

        int foundNew = 0;
        int foundUpdate = 0;

        if (configurations != null && !configurations.isEmpty())
        {
            for (ProxyConfiguration conf : configurations)
            {
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
            }

            long proxiesCount = ApplicationGlobals.getDBManager().getProxiesCount();
            LogWrapper.d(TAG, String.format("Found proxies: NEW: %d, UPDATED: %d, TOT: %d", foundNew, foundUpdate, proxiesCount));
        }

        LogWrapper.stopTrace(TAG, "upsertFoundProxyConfigurations", Log.DEBUG);
    }

    private void checkProxiesCountryCodes()
    {
        List<ProxyEntity> proxies = ApplicationGlobals.getDBManager().getProxyWithEmptyCountryCode();

        for (ProxyEntity proxy : proxies)
        {
            LogWrapper.startTrace(TAG, "Get proxy country code", Log.DEBUG);

            try
            {
                String countryCode = Utils.getProxyCountryCode(proxy);
                if (!TextUtils.isEmpty(countryCode))
                {
                    proxy.setCountryCode(countryCode);
                    ApplicationGlobals.getDBManager().upsertProxy(proxy);
                }
            }
            catch (Exception e)
            {
                EventReportingUtils.sendException(e);
                break;
            }

            LogWrapper.stopTrace(TAG, "Get proxy country code", proxy.toString(), Log.DEBUG);
        }
    }
}