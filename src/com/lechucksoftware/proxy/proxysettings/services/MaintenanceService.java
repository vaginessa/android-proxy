package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;

import java.util.List;

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

        LogWrapper.startTrace(TAG, "maintenanceService", Log.ERROR);

        handleIntentLogic(intent);

        LogWrapper.stopTrace(TAG, "maintenanceService", Log.ERROR);
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
                    checkDBstatus();
                    upsertFoundProxyConfigurations();
                    checkProxiesCountryCodes();
                }
                catch (Exception e)
                {
                    BugReportingUtils.sendException(new Exception("Exception during maintenanceService", e));
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
        getInUseProxyTag();
    }

    private DBTag getInUseProxyTag()
    {
        DBTag inUseTag = new DBTag();
        inUseTag.tag = "IN USE";
        inUseTag.tagColor = UIUtils.getTagsColor(this, 0);
        return ApplicationGlobals.getDBManager().upsertTag(inUseTag);
    }

    private void upsertFoundProxyConfigurations()
    {
        LogWrapper.startTrace(TAG, "upsertFoundProxyConfigurations", Log.INFO);

        List<ProxyConfiguration> configurations = ApplicationGlobals.getProxyManager().getSortedConfigurationsList();

        if (!configurations.isEmpty())
        {
            for (ProxyConfiguration conf : configurations)
            {
                if (conf.getProxy() != java.net.Proxy.NO_PROXY && conf.isValidProxyConfiguration())
                {
                    DBProxy pd = new DBProxy();
                    pd.host = conf.getProxyHost();
                    pd.port = conf.getProxyPort();
                    pd.exclusion = conf.getProxyExclusionList();
                    pd.tags.add(getInUseProxyTag());

                    ApplicationGlobals.getDBManager().upsertProxy(pd);
                }
            }

            long proxiesCount = ApplicationGlobals.getDBManager().getProxiesCount();
            LogWrapper.d(TAG, "Saved proxy: " + proxiesCount);
        }

        LogWrapper.stopTrace(TAG, "upsertFoundProxyConfigurations", Log.INFO);
    }

    private void checkProxiesCountryCodes()
    {
        List<DBProxy> proxies = ApplicationGlobals.getDBManager().getProxyWithEmptyCountryCode();

        for (DBProxy proxy : proxies)
        {
            LogWrapper.startTrace(TAG, "Get proxy country code", Log.DEBUG);

            try
            {
                String countryCode = Utils.getProxyCountryCode(proxy);
                if (countryCode != null && countryCode.length() > 0)
                {
                    proxy.setCountryCode(countryCode);
                    ApplicationGlobals.getDBManager().upsertProxy(proxy);
                }
            }
            catch (Exception e)
            {
                BugReportingUtils.sendException(e);
                break;
            }

            LogWrapper.stopTrace(TAG, "Get proxy country code", proxy.toString(), Log.DEBUG);
        }
    }
}