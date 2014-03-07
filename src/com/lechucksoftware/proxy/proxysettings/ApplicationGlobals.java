package com.lechucksoftware.proxy.proxysettings;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.DataSource;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.log.LogWrapper;


public class ApplicationGlobals extends Application
{
    private static final String TAG = ApplicationGlobals.class.getSimpleName();

    private static ApplicationGlobals mInstance;
    private ProxyManager proxyManager;
    private DataSource dbManager;
    public AndroidMarket activeMarket;
    private CacheManager cacheManager;
    public Boolean demoMode;
    public Boolean wifiActionEnabled;

    @Override
    public void onCreate()
    {
        super.onCreate();

        LogWrapper.startTrace(TAG, "STARTUP", Log.ERROR);

        mInstance = this;

        proxyManager = new ProxyManager(ApplicationGlobals.this);
        dbManager = new DataSource(ApplicationGlobals.this);
        cacheManager = new CacheManager(ApplicationGlobals.this);

        activeMarket = Utils.getInstallerMarket(ApplicationGlobals.this);

        demoMode = false;
        wifiActionEnabled = true;

        // SETUP Libraries
        EventReportingUtils.setup(ApplicationGlobals.this);
        APL.setup(ApplicationGlobals.this, EventReportingUtils.getInstance());

        LogWrapper.d(TAG, "Calling broadcast intent " + Intents.PROXY_SETTINGS_STARTED);
        sendBroadcast(new Intent(Intents.PROXY_SETTINGS_STARTED));
    }

    public static ApplicationGlobals getInstance()
    {
        if (mInstance == null)
        {
            EventReportingUtils.sendException(new Exception("Cannot find valid instance of ApplicationGlobals, trying to instanciate a new one"));
            mInstance = new ApplicationGlobals();
        }

        return mInstance;
    }

    public static ProxyManager getProxyManager()
    {
        if (getInstance().proxyManager == null)
        {
            EventReportingUtils.sendException(new Exception("Cannot find valid instance of ProxyManager, trying to instanciate a new one"));
            getInstance().proxyManager = new ProxyManager(getInstance());
        }

        return getInstance().proxyManager;
    }

    public static DataSource getDBManager()
    {
        if (getInstance().dbManager == null)
        {
            EventReportingUtils.sendException(new Exception("Cannot find valid instance of DataSource, trying to instanciate a new one"));
            getInstance().dbManager = new DataSource(getInstance());
        }

        return getInstance().dbManager;
    }

    public static CacheManager getCacheManager()
    {
        if (getInstance().cacheManager == null)
        {
            EventReportingUtils.sendException(new Exception("Cannot find valid instance of CacheManager, trying to instanciate a new one"));
            getInstance().cacheManager = new CacheManager(getInstance());
        }

        return getInstance().cacheManager;
    }
}
