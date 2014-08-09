package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import java.net.Proxy;
import java.util.List;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLConstants;
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
                        checkDBstatus();
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
//        long id  = App.getDBManager().findTag("IN USE");
//        if (id != -1)
//        {
//            inUseTag = App.getDBManager().getTag(id);
//        }
//        else
//        {
//            inUseTag = new TagEntity();
//            inUseTag.tag = "IN USE";
//            inUseTag.tagColor = UIUtils.getTagsColor(this, 0);
//            App.getDBManager().upsertTag(inUseTag);
//        }
//
//        return inUseTag;
//    }

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
