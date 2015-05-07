package com.lechucksoftware.proxy.proxysettings;

import android.app.Application;
import android.content.Intent;

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
    private NavigationManager navigationManager;

    public Boolean demoMode;
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

        wifiNetworksManager = new WifiNetworksManager(App.this);
        dbManager = new DataSource(App.this);
        cacheManager = new CacheManager(App.this);
        navigationManager = new NavigationManager(App.this);

        activeMarket = Utils.getInstallerMarket(App.this);

        demoMode = false;

        // Start ASAP a Wi-Fi scan
//        APL.getWifiManager().startScan();

        // TODO: evaluate moving to AsyncUpdateApplicationStatistics
        ApplicationStatistics.updateInstallationDetails(this);

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
            Timber.e(new Exception(),"Cannot find valid instance of App, trying to instantiate a new one");
            mInstance = new App();
        }

        return mInstance;
    }

    public static WifiNetworksManager getWifiNetworksManager()
    {
        if (getInstance().wifiNetworksManager == null)
        {
            Timber.e(new Exception(),"Cannot find valid instance of WifiNetworksManager, trying to instantiate a new one");
            getInstance().wifiNetworksManager = new WifiNetworksManager(getInstance());
        }

        return getInstance().wifiNetworksManager;
    }

    public static DataSource getDBManager()
    {
        if (getInstance().dbManager == null)
        {
            Timber.e(new Exception(),"Cannot find valid instance of DataSource, trying to instantiate a new one");
            getInstance().dbManager = new DataSource(getInstance());
        }

        return getInstance().dbManager;
    }

    public static NavigationManager getNavigationManager()
    {
        if (getInstance().navigationManager == null)
        {
            Timber.e(new Exception(),"Cannot find valid instance of NavigationManager, trying to instantiate a new one");
            getInstance().navigationManager = new NavigationManager(getInstance());
        }

        return getInstance().navigationManager;
    }
}
