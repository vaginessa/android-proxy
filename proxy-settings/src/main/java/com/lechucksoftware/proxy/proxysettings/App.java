package com.lechucksoftware.proxy.proxysettings;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.DataSource;
import com.lechucksoftware.proxy.proxysettings.logging.CustomCrashlyticsTree;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.EventsReporting;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.logging.TraceUtils;
import timber.log.Timber;


public class App extends Application
{
    private static final String TAG = App.class.getSimpleName();

    private static App mInstance;
    private WifiNetworksManager wifiNetworksManager;
    private DataSource dbManager;
    public AndroidMarket activeMarket;
    private CacheManager cacheManager;
    public Boolean demoMode;
//    public Boolean wifiActionEnabled;
    private TraceUtils traceUtils;
    private EventsReporting eventsReporter;

    public static int getAppMajorVersion()
    {
        return BuildConfig.VERSION_CODE / 100;
    }

    public static int getAppMinorVersion()
    {
        return BuildConfig.VERSION_CODE % 100;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        mInstance = this;

        eventsReporter = new EventsReporting(App.this);
        traceUtils = new TraceUtils();

        CustomCrashlyticsTree customCrashlyticsTree = new CustomCrashlyticsTree();
        Timber.plant(customCrashlyticsTree);

        APL.setup(App.this);

        getTraceUtils().startTrace(TAG, "STARTUP", Log.ERROR, true);

        wifiNetworksManager = new WifiNetworksManager(App.this);
        dbManager = new DataSource(App.this);
        cacheManager = new CacheManager(App.this);

        activeMarket = Utils.getInstallerMarket(App.this);

        demoMode = false;

        // Start ASAP a Wi-Fi scan
//        APL.getWifiManager().startScan();

        getTraceUtils().partialTrace(TAG, "STARTUP", Log.ERROR);

        // TODO: evaluate moving to AsyncUpdateApplicationStatistics
        ApplicationStatistics.updateInstallationDetails(this);

        getTraceUtils().partialTrace(TAG, "STARTUP", Log.ERROR);

        Timber.d("Calling broadcast intent " + Intents.PROXY_SETTINGS_STARTED);
        sendBroadcast(new Intent(Intents.PROXY_SETTINGS_STARTED));
    }

    public static EventsReporting getEventsReporter()
    {
        if (getInstance().eventsReporter == null)
        {
            getInstance().eventsReporter = new EventsReporting(App.getInstance());
        }

        return getInstance().eventsReporter;
    }

    public static TraceUtils getTraceUtils()
    {
        if (getInstance().traceUtils == null)
        {
            getInstance().traceUtils = new TraceUtils();
        }

        return getInstance().traceUtils;
    }

    public static App getInstance()
    {
        if (mInstance == null)
        {
            Timber.e(new Exception(),"Cannot find valid instance of App, trying to instanciate a new one");
            mInstance = new App();
        }

        return mInstance;
    }

    public static WifiNetworksManager getWifiNetworksManager()
    {
        if (getInstance().wifiNetworksManager == null)
        {
            Timber.e(new Exception(),"Cannot find valid instance of WifiNetworksManager, trying to instanciate a new one");
            getInstance().wifiNetworksManager = new WifiNetworksManager(getInstance());
        }

        return getInstance().wifiNetworksManager;
    }

    public static DataSource getDBManager()
    {
        if (getInstance().dbManager == null)
        {
            Timber.e(new Exception(),"Cannot find valid instance of DataSource, trying to instanciate a new one");
            getInstance().dbManager = new DataSource(getInstance());
        }

        return getInstance().dbManager;
    }

//    public static CacheManager getCacheManager()
//    {
//        if (getInstance().cacheManager == null)
//        {
//            getEventsReporter().sendException(new Exception("Cannot find valid instance of CacheManager, trying to instanciate a new one"));
//            getInstance().cacheManager = new CacheManager(getInstance());
//        }
//
//        return getInstance().cacheManager;
//    }
}
