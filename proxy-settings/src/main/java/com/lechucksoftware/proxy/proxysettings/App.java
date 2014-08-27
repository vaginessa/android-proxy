package com.lechucksoftware.proxy.proxysettings;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.DataSource;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.EventsReporter;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.log.LogWrapper;


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
    private LogWrapper logger;
    private EventsReporter eventsReporter;

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

        // TODO: evaluate implementation of Logback library
//        // SLF4J
//        Logger LOG = LoggerFactory.getLogger(App.class);
//        LOG.info("hello world");

        mInstance = this;

        if (BuildConfig.DEBUG)
        {
            logger = new LogWrapper(Log.VERBOSE);
        }
        else
        {
            // Disable all LOGS on RELEASE
            logger = new LogWrapper(Integer.MAX_VALUE);
        }

        getLogger().startTrace(TAG, "STARTUP", Log.ERROR, true);

        eventsReporter = new EventsReporter(App.this);

        wifiNetworksManager = new WifiNetworksManager(App.this);
        dbManager = new DataSource(App.this);
        cacheManager = new CacheManager(App.this);

        activeMarket = Utils.getInstallerMarket(App.this);

        demoMode = false;
//        wifiActionEnabled = true;

        // READ configuration file
//        readAppConfigurationFile();

        // SETUP Libraries
        APL.setup(App.this, getLogger().getLogLevel(), getEventsReporter());
        // Start ASAP a Wi-Fi scan
//        APL.getWifiManager().startScan();

        getLogger().partialTrace(TAG, "STARTUP", Log.ERROR);

        // TODO: evaluate moving to AsyncUpdateApplicationStatistics
        ApplicationStatistics.updateInstallationDetails(this);

        getLogger().partialTrace(TAG, "STARTUP", Log.ERROR);

        getLogger().d(TAG, "Calling broadcast intent " + Intents.PROXY_SETTINGS_STARTED);
        sendBroadcast(new Intent(Intents.PROXY_SETTINGS_STARTED));
    }

    public static EventsReporter getEventsReporter()
    {
        if (getInstance().eventsReporter == null)
        {
            getInstance().eventsReporter = new EventsReporter(App.getInstance());
        }

        return getInstance().eventsReporter;
    }

    public static LogWrapper getLogger()
    {
        if (getInstance().logger == null)
        {
            getInstance().logger = new LogWrapper(Log.VERBOSE);
        }

        return getInstance().logger;
    }

    public static App getInstance()
    {
        if (mInstance == null)
        {
            getEventsReporter().sendException(new Exception("Cannot find valid instance of App, trying to instanciate a new one"));
            mInstance = new App();
        }

        return mInstance;
    }

    public static WifiNetworksManager getWifiNetworksManager()
    {
        if (getInstance().wifiNetworksManager == null)
        {
            getEventsReporter().sendException(new Exception("Cannot find valid instance of WifiNetworksManager, trying to instanciate a new one"));
            getInstance().wifiNetworksManager = new WifiNetworksManager(getInstance());
        }

        return getInstance().wifiNetworksManager;
    }

    public static DataSource getDBManager()
    {
        if (getInstance().dbManager == null)
        {
            getEventsReporter().sendException(new Exception("Cannot find valid instance of DataSource, trying to instanciate a new one"));
            getInstance().dbManager = new DataSource(getInstance());
        }

        return getInstance().dbManager;
    }

    public static CacheManager getCacheManager()
    {
        if (getInstance().cacheManager == null)
        {
            getEventsReporter().sendException(new Exception("Cannot find valid instance of CacheManager, trying to instanciate a new one"));
            getInstance().cacheManager = new CacheManager(getInstance());
        }

        return getInstance().cacheManager;
    }
}
