package com.lechucksoftware.proxy.proxysettings;

import android.app.Application;
import android.content.Intent;
import com.lechucksoftware.proxy.proxysettings.db.ProxyDataSource;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.shouldit.proxy.lib.*;


public class ApplicationGlobals extends Application
{
    private static ApplicationGlobals mInstance;
    public int timeout;

    private ProxyManager proxyManager;
    private static final String TAG = "ApplicationGlobals";
    private static ProxyConfiguration selectedConfiguration;

    @Override
    public void onCreate()
    {
        super.onCreate();

        mInstance = this;

        timeout = 10000; // Set default timeout value (10 seconds)

        proxyManager = new ProxyManager(ApplicationGlobals.this);

        // SETUP Libraries
        APL.setup(ApplicationGlobals.this);
        BugReportingUtils.setupBugSense(ApplicationGlobals.this);

        LogWrapper.d(TAG, "Calling broadcast intent " + Constants.PROXY_SETTINGS_STARTED);
        sendBroadcast(new Intent(Constants.PROXY_SETTINGS_STARTED));
    }

    public static ApplicationGlobals getInstance()
    {
        if (mInstance == null)
        {
            BugReportingUtils.sendException(new Exception("Cannot find valid instance of ApplicationGlobals, trying to instanciate a new one"));
            mInstance = new ApplicationGlobals();
        }

        return mInstance;
    }

    public static ProxyManager getProxyManager()
    {
        if (getInstance().proxyManager == null)
        {
            BugReportingUtils.sendException(new Exception("Cannot find valid instance of ProxyManager, trying to instanciate a new one"));
            getInstance().proxyManager = new ProxyManager(getInstance());
        }

        return getInstance().proxyManager;
    }

    public static void setSelectedConfiguration(ProxyConfiguration selectedConfiguration)
    {
        ApplicationGlobals.selectedConfiguration = selectedConfiguration;
    }

    public static ProxyConfiguration getSelectedConfiguration()
    {
        return ApplicationGlobals.selectedConfiguration;
    }
}
