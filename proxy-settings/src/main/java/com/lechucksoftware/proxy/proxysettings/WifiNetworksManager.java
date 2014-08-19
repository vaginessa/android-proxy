package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.db.WiFiAPEntity;

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
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.WifiNetworkId;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.utils.ProxyUtils;

/**
 * Created by Marco on 15/09/13.
 */
public class WifiNetworksManager
{
    private static final String TAG = WifiNetworksManager.class.getSimpleName();

    private Map<WifiNetworkId, WiFiAPConfig> wifiApConfigs;
    private Map<WifiNetworkId, WiFiAPEntity> wifiApEntities;
    private final Map<Integer, WifiConfiguration> wifiConfigurations;

    private WiFiAPConfig currentConfiguration;
    private Boolean updatedConfiguration;

    private List<WiFiAPConfig> wifiAPConfigList;

    private Map<WifiNetworkId, ScanResult> notConfiguredWifi; // Wi-Fi networks available but still not configured into Android's Wi-Fi settings

    public WifiNetworksManager(Context ctx)
    {
        updatedConfiguration = false;

        wifiConfigurations = Collections.synchronizedMap(new HashMap<Integer, WifiConfiguration>());
        wifiApConfigs = Collections.synchronizedMap(new HashMap<WifiNetworkId, WiFiAPConfig>());
        wifiApEntities = Collections.synchronizedMap(new HashMap<WifiNetworkId, WiFiAPEntity>());
        notConfiguredWifi = Collections.synchronizedMap(new HashMap<WifiNetworkId, ScanResult>());
    }

    private Map<WifiNetworkId, WiFiAPConfig> getWifiApConfigs()
    {
        if (wifiApConfigs == null)
            wifiApConfigs = Collections.synchronizedMap(new HashMap<WifiNetworkId, WiFiAPConfig>());

        return wifiApConfigs;
    }

    public Map<WifiNetworkId, ScanResult> getNotConfiguredWifi()
    {
        if (notConfiguredWifi == null)
            notConfiguredWifi = Collections.synchronizedMap(new HashMap<WifiNetworkId, ScanResult>());

        return notConfiguredWifi;
    }

    public List<WiFiAPConfig> getSortedWifiApConfigsList()
    {
        if (wifiAPConfigList == null)
        {
            wifiAPConfigList = getConfigurationsList();
        }

        return wifiAPConfigList;
    }

    private String getConfigurationsString()
    {
        if (!getWifiApConfigs().isEmpty())
        {
            return TextUtils.join(", ", getWifiApConfigs().keySet());
        }
        else
        {
            return "No configured Wi-Fi networks";
        }
    }

    public WiFiAPConfig getCurrentConfiguration()
    {
        WiFiAPConfig conf = null;

        App.getLogger().startTrace(TAG, "getCurrentConfiguration", Log.INFO);

        if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
        {
            WifiInfo info = APL.getWifiManager().getConnectionInfo();
            if (info != null)
            {
                WifiConfiguration wifiConfiguration = APL.getConfiguredNetwork(info.getNetworkId());
                conf = APL.getWiFiAPConfiguration(wifiConfiguration);

                if (currentConfiguration == null)
                {
                    if (conf != null)
                    {
                        currentConfiguration = conf;
                        App.getLogger().d(TAG, "getCurrentConfiguration - Set current configuration (was NULL before)");
                    }
                    else
                    {
                        App.getLogger().d(TAG, "getCurrentConfiguration - Same configuration: no need to update it (both NULL)");
                    }
                }
                else if ((currentConfiguration == null) || (conf != null && currentConfiguration != null && currentConfiguration.compareTo(conf) != 0))
                {
                    // Update currentConfiguration only if it's different from the previous
                    currentConfiguration = conf;
                    App.getLogger().d(TAG, "getCurrentConfiguration - Updated current configuration");
                }
                else
                {
                    App.getLogger().d(TAG, "getCurrentConfiguration - Same configuration: no need to update it");
                }
            }
        }

        App.getLogger().stopTrace(TAG, "getCurrentConfiguration", Log.INFO);

        return currentConfiguration;
    }

    public WiFiAPConfig getCachedConfiguration()
    {
//        if (currentConfiguration == null)
//        {
        return getCurrentConfiguration();
//        }
//        return currentConfiguration;
    }

    /**
     * If necessary updates the configuration list and sort it
     *
     * @return the sorted list of current proxy savedConfigurations
     */
    private List<WiFiAPConfig> getConfigurationsList()
    {
//        if (getSavedConfigurations().isEmpty())
//            updateProxyConfigurationList();
//
        buildSortedConfigurationsList();

        return wifiAPConfigList;
    }

    public void updateWifiConfig(WiFiAPConfig wiFiAPConfig, WiFiAPEntity result)
    {
        if (wifiApConfigs != null)
        {
            WifiNetworkId networkId = wiFiAPConfig.getInternalWifiNetworkId();
            if (wifiApConfigs.containsKey(networkId))
            {
                wifiApConfigs.get(networkId).updateProxyConfiguration(wiFiAPConfig);
            }
            else
            {
                wifiApConfigs.put(networkId,wiFiAPConfig);
                wifiApEntities.put(networkId,result);
                wifiConfigurations.put(wiFiAPConfig.getNetworkId(),wiFiAPConfig.getWifiConfig());
            }
        }
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
        List<WifiNetworkId> notMoreConfiguredSSID = updateCachedWifiAP(internalSavedSSID);

        // Remove from current configuration the SSID that are not more configured into Android's Wi-Fi settings
        removeNotMoreConfiguredSSID(notMoreConfiguredSSID);

        // Update savedConfigurations with latest Wi-Fi scan results
        updateWifiApConfigs();

        // If the configuration has been updated sort again the list!!
        if (updatedConfiguration && !getWifiApConfigs().isEmpty())
        {
            App.getLogger().d(TAG, "Configuration updated -> need to create again the sorted list");
            buildSortedConfigurationsList();
        }

        App.getLogger().d(TAG, "Final savedConfigurations list: " + getConfigurationsString());
        App.getLogger().stopTrace(TAG, "updateProxyConfigurationList", Log.DEBUG);
    }

    private void updateWifiApConfigs()
    {
//        LogWrapper.startTrace(TAG,"updateAfterScanResults", Log.DEBUG);
        WifiInfo currentWifiInfo = APL.getWifiManager().getConnectionInfo();

        // update current WifiInfo information for each WifiApConfig
        if (!getWifiApConfigs().isEmpty())
        {
            for (WiFiAPConfig conf : getWifiApConfigs().values())
            {
                conf.updateWifiInfo(currentWifiInfo, null);
            }
        }

        // TODO: getScanResults() seems to Trigger a query to LocationManager
        // Add a possibility to disable the behaviour in order to avoid problems with KitKat AppOps
        List<ScanResult> scanResults = APL.getWifiManager().getScanResults();
        if (scanResults != null)
        {
            updatedConfiguration = true;

            // clear all the savedConfigurations AP status
            if (!getWifiApConfigs().isEmpty())
            {
                for (WiFiAPConfig conf : getWifiApConfigs().values())
                {
                    conf.clearScanStatus();
                }
            }

            List<String> scanResultsStrings = new ArrayList<String>();


            for (ScanResult res : scanResults)
            {
                scanResultsStrings.add(res.SSID + " level: " + res.level);
                String currSSID = ProxyUtils.cleanUpSSID(res.SSID);
                SecurityType security = ProxyUtils.getSecurity(res);
                WifiNetworkId currWifiNet = new WifiNetworkId(currSSID, security);

                if (getWifiApConfigs().containsKey(currWifiNet))
                {
                    WiFiAPConfig conf = getWifiApConfigs().get(currWifiNet);
                    if (conf != null)
                    {
                        conf.updateScanResults(res);
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
        if (!getWifiApConfigs().isEmpty())
        {
            for (WifiNetworkId netId : internalSavedSSID)
            {
                if (getWifiApConfigs().containsKey(netId))
                {
                    WiFiAPConfig removed = getWifiApConfigs().remove(netId);
                    updatedConfiguration = true;
                    App.getLogger().w(TAG, "Removing from Proxy Settings configuration a no more configured SSID: " + removed.toShortString());
                }
            }

//            LogWrapper.d(TAG, "Cleaned up savedConfigurations list: " + getConfigurationsString());

        }
//        LogWrapper.stopTrace(TAG,"removeNoMoreConfiguredSSID", Log.DEBUG);
    }

    private List<WifiNetworkId> updateCachedWifiAP(List<WifiNetworkId> internalSavedSSID)
    {
//        LogWrapper.startTrace(TAG,"getSavedConfigurations", Log.DEBUG);

        // Get updated list of Proxy savedConfigurations from APL
        List<WiFiAPConfig> updatedConfigurations = APL.getWifiAPConfigurations();
        if (updatedConfigurations != null)
        {
            for (WiFiAPConfig conf : updatedConfigurations)
            {
                if (conf != null)
                {
                    wifiApConfigs = getWifiApConfigs();
                    if (wifiApConfigs != null
                            && conf.getInternalWifiNetworkId() != null
                            && wifiApConfigs.containsKey(conf.getInternalWifiNetworkId()))
                    {
                        // Updates already saved configuration
                        WiFiAPConfig originalConf = getWifiApConfigs().get(conf.getInternalWifiNetworkId());
                        if (originalConf.updateProxyConfiguration(conf))
                            updatedConfiguration = true;
                    }
                    else
                    {
                        // Add new found configuration
//                        App.getLogger().d(TAG, "Adding to list new Wi-Fi AP configuration: " + conf.toShortString());
                        getWifiApConfigs().put(conf.getInternalWifiNetworkId(), conf);
                    }

                    if (internalSavedSSID.contains(conf.getInternalWifiNetworkId()))
                    {
                        internalSavedSSID.remove(conf.getInternalWifiNetworkId());
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

        if (!getWifiApConfigs().isEmpty())
        {
            savedNetworks = getWifiApConfigs().keySet();
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
        if (!getWifiApConfigs().isEmpty())
        {
            Collection<WiFiAPConfig> values = getWifiApConfigs().values();
            if (values != null && values.size() > 0)
            {
                App.getLogger().startTrace(TAG, "SortConfigurationList", Log.DEBUG);

                wifiAPConfigList = new ArrayList<WiFiAPConfig>(values);

                try
                {
                    Collections.sort(wifiAPConfigList);
                }
                catch (IllegalArgumentException e)
                {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("config_list", configListToDBG().toString());
                    App.getEventsReporter().sendException(e, map);
                }

                StringBuilder sb = new StringBuilder();
                for (WiFiAPConfig conf : wifiAPConfigList)
                {
                    sb.append(conf.getSSID() + ",");
                }

                App.getLogger().d(TAG, "Sorted proxy configuration list: " + sb.toString());

                App.getLogger().stopTrace(TAG, "SortConfigurationList", Log.DEBUG);
            }
        }
    }

    public WiFiAPConfig getConfiguration(WifiNetworkId wifiNetworkId)
    {
        WiFiAPConfig selected = null;

        List<WiFiAPConfig> configurationList = getConfigurationsList();
        if (configurationList != null)
        {
            for (WiFiAPConfig conf : configurationList)
            {
                if (conf.getInternalWifiNetworkId().equals(wifiNetworkId))
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

            for (WiFiAPConfig conf : wifiAPConfigList)
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
