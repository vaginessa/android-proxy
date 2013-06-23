package com.lechucksoftware.proxy.proxysettings;

import android.app.Application;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.Log;
import com.bugsense.trace.BugSenseHandler;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.android.utils.lib.log.LogWrapper;
import com.shouldit.proxy.lib.*;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;

import java.util.*;


public class ApplicationGlobals extends Application
{
    private static ApplicationGlobals mInstance;

    private Map<WifiNetworkId, ProxyConfiguration> configurations;

    private Map<WifiNetworkId, ProxyConfiguration> getConfigurations()
    {
        return configurations;
    }

    private List<ProxyConfiguration> sortedConfigurationsList;

    public List<ProxyConfiguration> getSortedConfigurationsList()
    {
        if (sortedConfigurationsList == null)
        {
            sortedConfigurationsList = getConfigurationsList();
        }

        return sortedConfigurationsList;
    }

    private List<ProxyConfiguration> getConfigurationsList()
    {
        if (getConfigurations().isEmpty())
            updateProxyConfigurationList();

        buildConfigurationsList();

        return sortedConfigurationsList;
    }

    private void buildConfigurationsList()
    {
        if (!getConfigurations().isEmpty())
        {
            Collection<ProxyConfiguration> values = getConfigurations().values();
            if (values != null && values.size() > 0)
            {
                LogWrapper.startTrace(TAG, "SortConfigurationList", Log.DEBUG);

                sortedConfigurationsList = new ArrayList<ProxyConfiguration>(values);
                Collections.sort(sortedConfigurationsList);

                StringBuilder sb = new StringBuilder();
                for (ProxyConfiguration conf : sortedConfigurationsList)
                {
                    sb.append(conf.ap.ssid + ",");
                }
                LogWrapper.d(TAG, "Sorted proxy configuration list: " + sb.toString());

                LogWrapper.stopTrace(TAG, "SortConfigurationList", Log.DEBUG);
            }
        }
    }

    private String getConfigurationsString()
    {
        if (!getConfigurations().isEmpty())
        {
            return TextUtils.join(", ", getConfigurations().keySet());
        }
        else
        {
            return "No configured Wi-Fi networks";
        }
    }

    // Wi-Fi networks available but still not configured into Android's Wi-Fi settings
    private Map<WifiNetworkId, ScanResult> notConfiguredWifi;

    public Map<WifiNetworkId, ScanResult> getNotConfiguredWifi()
    {
        return notConfiguredWifi;
    }

    public int timeout;
    private ProxyConfiguration currentConfiguration;

    private static final String TAG = "ApplicationGlobals";
    private static ProxyConfiguration selectedConfiguration;

    public static void setSelectedConfiguration(ProxyConfiguration selectedConfiguration)
    {
        ApplicationGlobals.selectedConfiguration = selectedConfiguration;
    }

    public static ProxyConfiguration getSelectedConfiguration()
    {
        return ApplicationGlobals.selectedConfiguration;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        mInstance = this;

        timeout = 10000; // Set default timeout value (10 seconds)

        configurations = Collections.synchronizedMap(new HashMap<WifiNetworkId, ProxyConfiguration>());
        notConfiguredWifi = Collections.synchronizedMap(new HashMap<WifiNetworkId, ScanResult>());

        // SETUP Libraries
        APL.setup(ApplicationGlobals.this);
        Utils.SetupBugSense(ApplicationGlobals.this);

        LogWrapper.d(TAG, "Calling broadcast intent " + Constants.PROXY_SETTINGS_STARTED);
        sendBroadcast(new Intent(Constants.PROXY_SETTINGS_STARTED));
    }

    public static ApplicationGlobals getInstance()
    {
        if (mInstance == null)
        {
            BugSenseHandler.sendException(new Exception("Cannot find valid instance of ApplicationGlobals, trying to instanciate a new one"));
            mInstance = new ApplicationGlobals();
        }

        return mInstance;
    }

    public synchronized void updateProxyConfigurationList()
    {
        LogWrapper.startTrace(TAG, "updateProxyConfigurationList", Log.ASSERT);
        Boolean updatedConfiguration = false;

        /**************************************************************************************************************
         * Get information regarding current saved configuration
         **************************************************************************************************************/
        Collection<WifiNetworkId> savedNetworks = null;
        List<WifiNetworkId> savedSSIDNotMoreConfiguredList = new ArrayList<WifiNetworkId>();
//        LogWrapper.startTrace(TAG,"getSavedConfigurations", Log.DEBUG);
        if (!getConfigurations().isEmpty())
        {
            savedNetworks = getConfigurations().keySet();
            for (WifiNetworkId wifiNet : savedNetworks)
            {
                savedSSIDNotMoreConfiguredList.add(wifiNet);
            }
        }
//        LogWrapper.stopTrace(TAG,"getSavedConfigurations", Log.DEBUG);

        /**************************************************************************************************************
         * Get latests information regarding configured AP
         **************************************************************************************************************/
//        LogWrapper.startTrace(TAG,"getConfigurations", Log.DEBUG);
        List<ProxyConfiguration> updatedConfigurations = APL.getProxiesConfigurations();
        for (ProxyConfiguration conf : updatedConfigurations)
        {
            if (getConfigurations().containsKey(conf.internalWifiNetworkId))
            {
                ProxyConfiguration originalConf = getConfigurations().get(conf.internalWifiNetworkId);
                if (originalConf.updateConfiguration(conf))
                    updatedConfiguration = true;
            }
            else
            {
                LogWrapper.d(TAG, "Adding to list new proxy configurations: " + conf.toShortString());
                getConfigurations().put(conf.internalWifiNetworkId, conf);
            }

            if (savedSSIDNotMoreConfiguredList.contains(conf.internalWifiNetworkId))
            {
                savedSSIDNotMoreConfiguredList.remove(conf.internalWifiNetworkId);
            }
        }
//        LogWrapper.stopTrace(TAG,"getConfigurations", Log.DEBUG);

//        LogWrapper.d(TAG,"Updated configurations list: " + getConfigurationsString());
//        LogWrapper.d(TAG,"Configurations that need to be removed: " + TextUtils.join(", " , savedSSIDNotMoreConfiguredList));


        /**************************************************************************************************************
         * Remove from current configuration the SSID that are not more configured into Android's Wi-Fi settings
         **************************************************************************************************************/
        if (!getConfigurations().isEmpty())
        {
            //        LogWrapper.startTrace(TAG,"removeNoMoreConfiguredSSID", Log.DEBUG);
            for (WifiNetworkId netId : savedSSIDNotMoreConfiguredList)
            {
                if (getConfigurations().containsKey(netId))
                {
                    ProxyConfiguration removed = getConfigurations().remove(netId);
                    updatedConfiguration = true;
                    LogWrapper.w(TAG, "Removing from Proxy Settings configuration a no more configured SSID: " + removed.toShortString());
                }
            }
            //        LogWrapper.stopTrace(TAG,"removeNoMoreConfiguredSSID", Log.DEBUG);
            //        LogWrapper.d(TAG,"Cleaned up configurations list: " + getConfigurationsString());
        }

        /**************************************************************************************************************
         * Update configurations with latest Wi-Fi scan results
         **************************************************************************************************************/
//        LogWrapper.startTrace(TAG,"updateAfterScanResults", Log.DEBUG);
        List<ScanResult> scanResults = APL.getWifiManager().getScanResults();
        if (scanResults != null)
        {
            updatedConfiguration = true;

            // clear all the configurations AP status
            if (!getConfigurations().isEmpty())
            {
                for (ProxyConfiguration conf : getConfigurations().values())
                {
                    conf.ap.clearScanStatus();
                }
            }

            for (ScanResult res : scanResults)
            {
                LogWrapper.d(TAG, "Updating from scanresult: " + res.SSID + " level: " + res.level);
                String currSSID = ProxyUtils.cleanUpSSID(res.SSID);
                APLConstants.SecurityType security = ProxyUtils.getSecurity(res);
                WifiNetworkId currWifiNet = new WifiNetworkId(currSSID, security);

                if (getConfigurations().containsKey(currWifiNet))
                {
                    ProxyConfiguration conf = getConfigurations().get(currWifiNet);
                    if (conf != null && conf.ap != null)
                    {
                        conf.ap.update(res);
                    }
                }
                else
                {
                    if (getNotConfiguredWifi().containsKey(currWifiNet))
                    {
                        getNotConfiguredWifi().remove(currWifiNet);
                    }

                    getNotConfiguredWifi().put(currWifiNet, res);
                }
            }
        }
//        LogWrapper.stopTrace(TAG,"updateAfterScanResults", Log.DEBUG);

        if (updatedConfiguration && !getConfigurations().isEmpty())
        {
            LogWrapper.d(TAG, "Configuration updated -> need to create again the sorted list");
            buildConfigurationsList();
        }

        LogWrapper.d(TAG, "Final configurations list: " + getConfigurationsString());
        LogWrapper.stopTrace(TAG, "updateProxyConfigurationList", Log.ASSERT);
    }

    public ProxyConfiguration getCurrentConfiguration()
    {
        ProxyConfiguration conf = null;

        if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
        {
            WifiInfo info = APL.getWifiManager().getConnectionInfo();
            if (info != null)
            {
                if (getConfigurations().isEmpty())
                    updateProxyConfigurationList();

                List<WifiConfiguration> wificonfigurations = APL.getWifiManager().getConfiguredNetworks();
                if (!wificonfigurations.isEmpty())
                {
                    for (WifiConfiguration wifiConfig : wificonfigurations)
                    {
                        if (wifiConfig.networkId == info.getNetworkId())
                        {
                            String SSID = ProxyUtils.cleanUpSSID(info.getSSID());
                            WifiNetworkId netId = new WifiNetworkId(SSID, ProxyUtils.getSecurity(wifiConfig));
                            if (getConfigurations().containsKey(netId))
                            {
                                conf = getConfigurations().get(netId);
                                break;
                            }
                        }
                    }
                }

                if (currentConfiguration == null || conf != null && currentConfiguration != null && currentConfiguration.compareTo(conf) != 0)
                {
                    getInstance().currentConfiguration = conf;
                }
            }
        }

        // Always return a not null configuration
        if (getInstance().currentConfiguration == null)
        {
            LogWrapper.w(TAG, "Cannot find a valid current configuration: creating an empty one");
            getInstance().currentConfiguration = new ProxyConfiguration(ProxySetting.NONE, null, null, null, null);
        }

        return getInstance().currentConfiguration;
    }

    public ProxyConfiguration getCachedConfiguration()
    {
        if (getInstance().currentConfiguration == null)
        {
            return getCurrentConfiguration();
        }

        return getInstance().currentConfiguration;
    }

    public static void connectToAP(ProxyConfiguration conf)
    {
        if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
        {
            if (conf != null && conf.ap != null && conf.ap.getLevel() > -1)
            {
                // Connect to AP only if it's available
                APL.getWifiManager().enableNetwork(conf.ap.networkId, true);
            }
        }
    }

    public static Boolean isConnected()
    {
        NetworkInfo ni = getCurrentNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Boolean isConnectedToWiFi()
    {
        NetworkInfo ni = getCurrentWiFiInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static NetworkInfo getCurrentNetworkInfo()
    {
        NetworkInfo ni = APL.getConnectivityManager().getActiveNetworkInfo();
        return ni;
    }

    public static NetworkInfo getCurrentWiFiInfo()
    {
        NetworkInfo ni = APL.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ni;
    }

    public static void startWifiScan()
    {
        if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
        {
            APL.getWifiManager().startScan();
        }
    }


}
