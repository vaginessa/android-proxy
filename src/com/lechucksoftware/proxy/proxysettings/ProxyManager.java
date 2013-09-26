package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.db.ProxyData;
import com.lechucksoftware.proxy.proxysettings.db.ProxyDataSource;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.shouldit.proxy.lib.*;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;

import java.net.Proxy;
import java.util.*;

/**
 * Created by Marco on 15/09/13.
 */
public class ProxyManager
{
    private static final String TAG = "ProxyManager";
    private final Context context;
    private ProxyConfiguration currentConfiguration;
    //    private List<WifiNetworkId> internalSavedSSID;
    Boolean updatedConfiguration;

    public ProxyManager(Context ctx)
    {
        context = ctx;
        updatedConfiguration = false;

        savedConfigurations = Collections.synchronizedMap(new HashMap<WifiNetworkId, ProxyConfiguration>());
        notConfiguredWifi = Collections.synchronizedMap(new HashMap<WifiNetworkId, ScanResult>());
    }

    private Map<WifiNetworkId, ProxyConfiguration> savedConfigurations;

    private Map<WifiNetworkId, ProxyConfiguration> getSavedConfigurations()
    {
        if (savedConfigurations == null)
            savedConfigurations = Collections.synchronizedMap(new HashMap<WifiNetworkId, ProxyConfiguration>());

        return savedConfigurations;
    }

    // Wi-Fi networks available but still not configured into Android's Wi-Fi settings
    private Map<WifiNetworkId, ScanResult> notConfiguredWifi;

    public Map<WifiNetworkId, ScanResult> getNotConfiguredWifi()
    {
        if (notConfiguredWifi == null)
            notConfiguredWifi = Collections.synchronizedMap(new HashMap<WifiNetworkId, ScanResult>());

        return notConfiguredWifi;
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

    private String getConfigurationsString()
    {
        if (!getSavedConfigurations().isEmpty())
        {
            return TextUtils.join(", ", getSavedConfigurations().keySet());
        }
        else
        {
            return "No configured Wi-Fi networks";
        }
    }

    public ProxyConfiguration getCurrentConfiguration()
    {
        ProxyConfiguration conf = null;

        if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
        {
            WifiInfo info = APL.getWifiManager().getConnectionInfo();
            if (info != null)
            {
                if (getSavedConfigurations().isEmpty())
                    updateProxyConfigurationList();

                List<WifiConfiguration> wificonfigurations = APL.getWifiManager().getConfiguredNetworks();
                if (wificonfigurations != null && !wificonfigurations.isEmpty())
                {
                    for (WifiConfiguration wifiConfig : wificonfigurations)
                    {
                        if (wifiConfig.networkId == info.getNetworkId())
                        {
                            String SSID = ProxyUtils.cleanUpSSID(info.getSSID());
                            WifiNetworkId netId = new WifiNetworkId(SSID, ProxyUtils.getSecurity(wifiConfig));
                            if (getSavedConfigurations().containsKey(netId))
                            {
                                conf = getSavedConfigurations().get(netId);
                                break;
                            }
                        }
                    }
                }

                if (currentConfiguration == null || conf != null && currentConfiguration != null && currentConfiguration.compareTo(conf) != 0)
                {
                    currentConfiguration = conf;
                }
            }
        }

        // Always return a not null configuration
        if (currentConfiguration == null)
        {
            LogWrapper.w(TAG, "Cannot find a valid current configuration: creating an empty one");
            currentConfiguration = new ProxyConfiguration(ProxySetting.NONE, null, null, null, null);
        }

        return currentConfiguration;
    }

    private ProxyDataSource proxyDataSource;

    public ProxyDataSource getProxyDataSource()
    {
        if (proxyDataSource == null)
        {
            proxyDataSource = new ProxyDataSource(context);
        }

        return proxyDataSource;
    }

    /**
     * If necessary updates the configuration list and sort it
     *
     * @return the sorted list of current proxy savedConfigurations
     */
    private List<ProxyConfiguration> getConfigurationsList()
    {
        if (getSavedConfigurations().isEmpty())
            updateProxyConfigurationList();

        buildConfigurationsList();

        return sortedConfigurationsList;
    }

    /**
     * Updates the proxy configuration list
     */
    public synchronized void updateProxyConfigurationList()
    {
        LogWrapper.startTrace(TAG, "updateProxyConfigurationList", Log.ASSERT);

        //Get information regarding current saved configuration
        List<WifiNetworkId> internalSavedSSID = getInternalSavedWifiConfigurations();

        //Get latests information regarding configured AP
        List<WifiNetworkId> notMoreConfiguredSSID = updateInternalSavedConfiguration(internalSavedSSID);

        // Remove from current configuration the SSID that are not more configured into Android's Wi-Fi settings
        removeNotMoreConfiguredSSID(notMoreConfiguredSSID);

        // Update savedConfigurations with latest Wi-Fi scan results
        updateConfigurationsWithWifiScanResults();

        // If the configuration has been updated sort again the list!!
        if (updatedConfiguration && !getSavedConfigurations().isEmpty())
        {
            LogWrapper.d(TAG, "Configuration updated -> need to create again the sorted list");
            buildConfigurationsList();

            // Save or update on the DB all the found proxy
            try
            {
                upsertFoundProxyConfigurations();
            }
            catch (Exception e)
            {
                BugReportingUtils.sendException(new Exception("Exception during upsertFoundProxyConfigurations",e));
            }
        }

        LogWrapper.d(TAG, "Final savedConfigurations list: " + getConfigurationsString());
        LogWrapper.stopTrace(TAG, "updateProxyConfigurationList", Log.ASSERT);
    }

    private void upsertFoundProxyConfigurations()
    {
        if (!getSavedConfigurations().isEmpty())
        {
            ProxyDataSource pds = getProxyDataSource();
            pds.openWritable();

            for (ProxyConfiguration conf : getSavedConfigurations().values())
            {
                if (conf.getProxy() != Proxy.NO_PROXY && conf.isValidProxyConfiguration())
                {
                    ProxyData pd = new ProxyData();
                    pd.host = conf.getProxyHost();
                    pd.port = conf.getProxyPort();
                    pd.exclusion = conf.getProxyExclusionList();
                    pd.description = null;

                    pds.upsertProxy(pd);
                }
            }

            List<ProxyData> savedProxies = pds.getAllProxies();
            for (ProxyData p : savedProxies)
            {
                LogWrapper.d(TAG,"Saved proxy: " + p.getDebugInfo());
            }

            pds.close();
        }
    }

    private void updateConfigurationsWithWifiScanResults()
    {
//        LogWrapper.startTrace(TAG,"updateAfterScanResults", Log.DEBUG);

        List<ScanResult> scanResults = APL.getWifiManager().getScanResults();
        if (scanResults != null)
        {
            updatedConfiguration = true;

            // clear all the savedConfigurations AP status
            if (!getSavedConfigurations().isEmpty())
            {
                for (ProxyConfiguration conf : getSavedConfigurations().values())
                {
                    conf.ap.clearScanStatus();
                }
            }

            for (ScanResult res : scanResults)
            {
                LogWrapper.d(TAG, "Updating from scanresult: " + res.SSID + " level: " + res.level);
                String currSSID = ProxyUtils.cleanUpSSID(res.SSID);
                SecurityType security = ProxyUtils.getSecurity(res);
                WifiNetworkId currWifiNet = new WifiNetworkId(currSSID, security);

                if (getSavedConfigurations().containsKey(currWifiNet))
                {
                    ProxyConfiguration conf = getSavedConfigurations().get(currWifiNet);
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
    }

    private void removeNotMoreConfiguredSSID(List<WifiNetworkId> internalSavedSSID)
    {
//        LogWrapper.startTrace(TAG,"removeNoMoreConfiguredSSID", Log.DEBUG);
        if (!getSavedConfigurations().isEmpty())
        {
            for (WifiNetworkId netId : internalSavedSSID)
            {
                if (getSavedConfigurations().containsKey(netId))
                {
                    ProxyConfiguration removed = getSavedConfigurations().remove(netId);
                    updatedConfiguration = true;
                    LogWrapper.w(TAG, "Removing from Proxy Settings configuration a no more configured SSID: " + removed.toShortString());
                }
            }

//            LogWrapper.d(TAG, "Cleaned up savedConfigurations list: " + getConfigurationsString());

        }
//        LogWrapper.stopTrace(TAG,"removeNoMoreConfiguredSSID", Log.DEBUG);
    }

    private List<WifiNetworkId> updateInternalSavedConfiguration(List<WifiNetworkId> internalSavedSSID)
    {
//        LogWrapper.startTrace(TAG,"getSavedConfigurations", Log.DEBUG);

        // Get updated list of Proxy savedConfigurations from APL
        List<ProxyConfiguration> updatedConfigurations = APL.getProxiesConfigurations();
        for (ProxyConfiguration conf : updatedConfigurations)
        {
            if (getSavedConfigurations().containsKey(conf.internalWifiNetworkId))
            {
                // Updates already saved configuration
                ProxyConfiguration originalConf = getSavedConfigurations().get(conf.internalWifiNetworkId);
                if (originalConf.updateConfiguration(conf))
                    updatedConfiguration = true;
            }
            else
            {
                // Add new found configuration
                LogWrapper.d(TAG, "Adding to list new proxy savedConfigurations: " + conf.toShortString());
                getSavedConfigurations().put(conf.internalWifiNetworkId, conf);
            }

            if (internalSavedSSID.contains(conf.internalWifiNetworkId))
            {
                internalSavedSSID.remove(conf.internalWifiNetworkId);
            }
        }

//        LogWrapper.d(TAG,"Updated savedConfigurations list: " + getConfigurationsString());
//        LogWrapper.d(TAG,"Configurations that need to be removed: " + TextUtils.join(", " , internalSavedSSID));

//        LogWrapper.stopTrace(TAG,"getSavedConfigurations", Log.DEBUG);

        return internalSavedSSID;
    }

    private List<WifiNetworkId> getInternalSavedWifiConfigurations()
    {
//        LogWrapper.startTrace(TAG,"getSavedConfigurations", Log.DEBUG);

        Collection<WifiNetworkId> savedNetworks = null;
        List<WifiNetworkId> internalSavedSSID = new ArrayList<WifiNetworkId>();

        if (!getSavedConfigurations().isEmpty())
        {
            savedNetworks = getSavedConfigurations().keySet();
            for (WifiNetworkId wifiNet : savedNetworks)
            {
                internalSavedSSID.add(wifiNet);
            }
        }

//        LogWrapper.stopTrace(TAG,"getSavedConfigurations", Log.DEBUG);

        return internalSavedSSID;
    }

    private void buildConfigurationsList()
    {
        if (!getSavedConfigurations().isEmpty())
        {
            Collection<ProxyConfiguration> values = getSavedConfigurations().values();
            if (values != null && values.size() > 0)
            {
                LogWrapper.startTrace(TAG, "SortConfigurationList", Log.DEBUG);

                sortedConfigurationsList = new ArrayList<ProxyConfiguration>(values);

                try
                {
                    Collections.sort(sortedConfigurationsList);
                }
                catch (IllegalArgumentException e)
                {
                    try
                    {
                        for (ProxyConfiguration conf : sortedConfigurationsList)
                        {
                            BugReportingUtils.addCrashExtraData(conf.getSSID(), conf.toString());
                        }
                    }
                    catch (Exception innerexception)
                    {
                        BugReportingUtils.sendException(innerexception);
                    }

                    BugReportingUtils.sendException(e);
                }

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

    public ProxyConfiguration getCachedConfiguration()
    {
        if (currentConfiguration == null)
        {
            return getCurrentConfiguration();
        }

        return currentConfiguration;
    }

}
