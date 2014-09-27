package com.lechucksoftware.proxy.proxysettings.services;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import java.net.Proxy;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.constants.APLConstants;
import be.shouldit.proxy.lib.utils.ProxyUtils;

public class MaintenanceService extends EnhancedIntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = MaintenanceService.class.getSimpleName();
    private boolean isHandling = false;
    private static MaintenanceService instance;

    public MaintenanceService()
    {
        super("MaintenanceService");
        App.getLogger().v(TAG, "MaintenanceService constructor");
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

        App.getLogger().startTrace(TAG, "maintenanceService", Log.DEBUG);

        handleIntentLogic(intent);

        App.getLogger().stopTrace(TAG, "maintenanceService", Log.DEBUG);
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
                        checkDBConsistence();
                        checkProxiesCountryCodes();
                        Utils.checkDemoMode(getApplicationContext());
                    }
                    else if (callerIntent.getAction().equals(Intents.PROXY_SAVED))
                    {
                        checkProxiesCountryCodes();
                    }
                    else
                    {
                        App.getLogger().e(TAG, "Intent not handled: " + callerIntent.toString());
                    }
                }
                catch (Exception e)
                {
                    App.getEventsReporter().sendException(new Exception("Exception during maintenanceService", e));
                }
            }
        }

        return;
    }

    private void checkDBConsistence()
    {
        /**
         * Check in Use proxy flag
         */
        checkInUseProxyFlag();
    }

    private void checkInUseProxyFlag()
    {
        try
        {
            Map<Long, ProxyEntity> proxiesMap = App.getDBManager().getAllProxiesWithTAGs();
            if (proxiesMap != null)
            {
                App.getLogger().startTrace(TAG,"checkInUseProxyTag",Log.DEBUG);

                for (Long proxyID : proxiesMap.keySet())
                {
                    App.getDBManager().updateInUseFlag(proxyID);
                }

                App.getLogger().stopTrace(TAG,"checkInUseProxyTag", String.format("Checked %d proxies",proxiesMap.size()), Log.DEBUG);
            }
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }

    private void checkProxiesCountryCodes()
    {
        Proxy proxy = null;

        try
        {
            proxy = APL.getCurrentHttpProxyConfiguration();
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }

        if (proxy != null && ProxyUtils.canGetWebResources(proxy, APLConstants.DEFAULT_TIMEOUT))
        {
            List<ProxyEntity> proxies = App.getDBManager().getProxyWithEmptyCountryCode();

            for (ProxyEntity pe : proxies)
            {
                App.getLogger().startTrace(TAG, "Get proxy country code", Log.DEBUG);

                try
                {
                    String countryCode = Utils.getProxyCountryCode(pe);
                    if (!TextUtils.isEmpty(countryCode))
                    {
                        pe.setCountryCode(countryCode);
                        App.getDBManager().upsertProxy(pe);
                    }
                }
                catch (Exception e)
                {
                    App.getEventsReporter().sendException(e);
                    break;
                }

                App.getLogger().stopTrace(TAG, "Get proxy country code", pe.toString(), Log.DEBUG);
            }
        }
    }
}
