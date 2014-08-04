package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.ProxyConfiguration;
import be.shouldit.proxy.lib.WifiNetworkId;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import be.shouldit.proxy.lib.utils.ProxyUtils;

/**
 * Created by Marco on 15/09/13.
 */
public class ProxyManager
{
    private static final String TAG = ProxyManager.class.getSimpleName();
    private final Context context;
    private ProxyConfiguration currentConfiguration;
    private Boolean updatedConfiguration;
    private Map<WifiNetworkId, ProxyConfiguration> savedConfigurations;
    private List<ProxyConfiguration> sortedConfigurationsList;
    private Map<WifiNetworkId, ScanResult> notConfiguredWifi; // Wi-Fi networks available but still not configured into Android's Wi-Fi settings

    public ProxyManager(Context ctx)
    {
        context = ctx;
        updatedConfiguration = false;

        savedConfigurations = Collections.synchronizedMap(new HashMap<WifiNetworkId, ProxyConfiguration>());
        notConfiguredWifi = Collections.synchronizedMap(new HashMap<WifiNetworkId, ScanResult>());
    }

    private Map<WifiNetworkId, ProxyConfiguration> getSavedConfigurations()
    {
        if (savedConfigurations == null)
            savedConfigurations = Collections.synchronizedMap(new HashMap<WifiNetworkId, ProxyConfiguration>());

        return savedConfigurations;
    }

    public Map<WifiNetworkId, ScanResult> getNotConfiguredWifi()
    {
        if (notConfiguredWifi == null)
            notConfiguredWifi = Collections.synchronizedMap(new HashMap<WifiNetworkId, ScanResult>());

        return notConfiguredWifi;
    }

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
            App.getLogger().w(TAG, "Cannot find a valid current configuration: creating an empty one");
            currentConfiguration = new ProxyConfiguration(ProxySetting.NONE, null, null, null, null);
        }

        return currentConfiguration;
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

        buildSortedConfigurationsList();

        return sortedConfigurationsList;
    }

    /**
     * Updates the proxy configuration list
     */
    public synchronized void updateProxyConfigurationList()
    {
        App.getLogger().startTrace(TAG, "updateProxyConfigurationList", Log.DEBUG);

        //Get information regarding current saved configuration
        List<WifiNetworkId> internalSavedSSID = getInternalSavedWifiConfigurations();

        //Get latests information regarding configured AP
        List<WifiNetworkId> notMoreConfiguredSSID = updateInternalWifiAP(internalSavedSSID);

        // Remove from current configuration the SSID that are not more configured into Android's Wi-Fi settings
        removeNotMoreConfiguredSSID(notMoreConfiguredSSID);

        // Update savedConfigurations with latest Wi-Fi scan results
        updateConfigurationsWithWifiScanResults();

        // If the configuration has been updated sort again the list!!
        if (updatedConfiguration && !getSavedConfigurations().isEmpty())
        {
            App.getLogger().d(TAG, "Configuration updated -> need to create again the sorted list");
            buildSortedConfigurationsList();
        }

        App.getLogger().d(TAG, "Final savedConfigurations list: " + getConfigurationsString());
        App.getLogger().stopTrace(TAG, "updateProxyConfigurationList", Log.DEBUG);
    }

    private void updateConfigurationsWithWifiScanResults()
    {
//        LogWrapper.startTrace(TAG,"updateAfterScanResults", Log.DEBUG);

        // TODO: getScanResults() seems to Trigger a query to LocationManager
        // Add a possibility to disable the behaviour in order to avoid problems with KitKat AppOps
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

            List<String> scanResultsStrings = new ArrayList<String>();
            for (ScanResult res : scanResults)
            {
                scanResultsStrings.add(res.SSID + " level: " + res.level);
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

            App.getLogger().d(TAG, "Updating from scanresult: " + TextUtils.join(", ", scanResultsStrings.toArray()));
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
                    App.getLogger().w(TAG, "Removing from Proxy Settings configuration a no more configured SSID: " + removed.toShortString());
                }
            }

//            LogWrapper.d(TAG, "Cleaned up savedConfigurations list: " + getConfigurationsString());

        }
//        LogWrapper.stopTrace(TAG,"removeNoMoreConfiguredSSID", Log.DEBUG);
    }

    private List<WifiNetworkId> updateInternalWifiAP(List<WifiNetworkId> internalSavedSSID)
    {
//        LogWrapper.startTrace(TAG,"getSavedConfigurations", Log.DEBUG);

        // Get updated list of Proxy savedConfigurations from APL
        List<ProxyConfiguration> updatedConfigurations = APL.getProxiesConfigurations();
        if (updatedConfigurations != null)
        {
            for (ProxyConfiguration conf : updatedConfigurations)
            {
                if (conf != null)
                {
                    savedConfigurations = getSavedConfigurations();
                    if (savedConfigurations != null
                            && conf.internalWifiNetworkId != null
                            && savedConfigurations.containsKey(conf.internalWifiNetworkId))
                    {
                        // Updates already saved configuration
                        ProxyConfiguration originalConf = getSavedConfigurations().get(conf.internalWifiNetworkId);
                        if (originalConf.updateConfiguration(conf))
                            updatedConfiguration = true;
                    }
                    else
                    {
                        // Add new found configuration
                        App.getLogger().d(TAG, "Adding to list new Wi-Fi AP configuration: " + conf.toShortString());
                        getSavedConfigurations().put(conf.internalWifiNetworkId, conf);
                    }

                    if (internalSavedSSID.contains(conf.internalWifiNetworkId))
                    {
                        internalSavedSSID.remove(conf.internalWifiNetworkId);
                    }
                }
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

    private void buildSortedConfigurationsList()
    {
        if (!getSavedConfigurations().isEmpty())
        {
            Collection<ProxyConfiguration> values = getSavedConfigurations().values();
            if (values != null && values.size() > 0)
            {
                App.getLogger().startTrace(TAG, "SortConfigurationList", Log.DEBUG);

                sortedConfigurationsList = new ArrayList<ProxyConfiguration>(values);

                try
                {
                    Collections.sort(sortedConfigurationsList);
                }
                catch (IllegalArgumentException e)
                {
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("config_list", configListToDBG().toString());
                    App.getEventsReporter().sendException(e, map);
                }

                StringBuilder sb = new StringBuilder();
                for (ProxyConfiguration conf : sortedConfigurationsList)
                {
                    sb.append(conf.ap.ssid + ",");
                }
                App.getLogger().d(TAG, "Sorted proxy configuration list: " + sb.toString());

                App.getLogger().stopTrace(TAG, "SortConfigurationList", Log.DEBUG);
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

    public ProxyConfiguration getConfiguration(WifiNetworkId wifiNetworkId)
    {
        ProxyConfiguration selected = null;

        List<ProxyConfiguration> configurationList = getConfigurationsList();
        if (configurationList != null)
        {
            for (ProxyConfiguration conf : configurationList)
            {
                if (conf.internalWifiNetworkId.equals(wifiNetworkId))
                {
                    selected = conf;
                    break;
                }
            }
        }

        return selected;
    }

    public JSONObject configListToDBG()
    {
        JSONObject dbg = new JSONObject();

        try
        {
            JSONArray configurations = new JSONArray();

            for (ProxyConfiguration conf : sortedConfigurationsList)
            {
                JSONObject jconf = new JSONObject();
                configurations.put(conf.toJSON());
            }

            dbg.put("configurations", configurations);
        }
        catch (JSONException e)
        {
            APL.getEventsReporter().sendException(e);
        }

        return dbg;
    }
}
