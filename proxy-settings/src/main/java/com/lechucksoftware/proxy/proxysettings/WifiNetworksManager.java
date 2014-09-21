package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.utils.ProxyUtils;

/**
 * Created by Marco on 15/09/13.
 */
public class WifiNetworksManager
{
    private static final String TAG = WifiNetworksManager.class.getSimpleName();
    private final WifiNetworkStatus wifiNetworkStatus;
    private final Context context;

    public WifiNetworksManager(Context ctx)
    {
        context = ctx;
        wifiNetworkStatus = new WifiNetworkStatus();
    }

    public void updateWifiApConfigs()
    {
        synchronized (wifiNetworkStatus)
        {
            App.getLogger().startTrace(TAG,"updateWifiApConfigs", Log.DEBUG, true);

//            Map<Long,WiFiAPEntity> persistedWifiAp = App.getDBManager().getAllWifiAp();
//            App.getLogger().partialTrace(TAG, "updateWifiApConfigs", "getAllWifiAp", Log.DEBUG);

            Map<APLNetworkId,WiFiAPConfig> configurations = APL.getWifiAPConfigurations();
            for (APLNetworkId aplNetworkId : configurations.keySet())
            {
                wifiNetworkStatus.put(aplNetworkId,configurations.get(aplNetworkId));
            }

            App.getLogger().partialTrace(TAG,"updateWifiApConfigs", "getWifiAPConfigurations", Log.DEBUG);

//            wifiNetworkStatus.setWifiAPConfigList(new ArrayList<WiFiAPConfig>(wifiNetworkStatus.wifiApConfigsByAPLNetId.values()));
            App.getLogger().partialTrace(TAG,"updateWifiApConfigs", "new ArrayList<WiFiAPConfig>", Log.DEBUG);

            updateWifiConfigWithScanResults(APL.getWifiManager().getScanResults());
            App.getLogger().partialTrace(TAG,"updateWifiApConfigs", "updateWifiConfigWithScanResults", Log.DEBUG);

            App.getLogger().stopTrace(TAG, "updateWifiApConfigs", Log.DEBUG);
        }
    }

    public void updateWifiConfig(WiFiAPConfig updatedConfiguration)
    {
        synchronized (wifiNetworkStatus)
        {
//            if (wifiNetworkStatus.getWifiApConfigsByAPLNetId() != null)
//            {
                APLNetworkId aplNetworkId = updatedConfiguration.getAPLNetworkId();

                if (wifiNetworkStatus.containsKey(aplNetworkId))
                {
                    WiFiAPConfig currentConfiguration = wifiNetworkStatus.get(aplNetworkId);
                    currentConfiguration.updateProxyConfiguration(updatedConfiguration);
                }
                else
                {
                    wifiNetworkStatus.put(aplNetworkId, updatedConfiguration);
                }
//            }
        }
    }

    public void removeWifiConfig(APLNetworkId aplNetworkId)
    {
        synchronized (wifiNetworkStatus)
        {
            if (aplNetworkId != null)
            {
                wifiNetworkStatus.remove(aplNetworkId);
            }
        }
    }

    public void updateCurrentWifiInfo(WifiInfo currentWifiInfo)
    {
        App.getLogger().startTrace(TAG,"updateCurrentWifiInfo", Log.DEBUG);

        synchronized (wifiNetworkStatus)
        {
            if (!wifiNetworkStatus.isEmpty())
            {
                for (WiFiAPConfig conf : wifiNetworkStatus.values())
                {
                    conf.updateWifiInfo(currentWifiInfo, null);
                }
            }
        }

        App.getLogger().stopTrace(TAG,"updateCurrentWifiInfo", Log.DEBUG);
    }

    public void updateWifiConfigWithScanResults(List<ScanResult> scanResults)
    {
        List<String> scanResultsStrings = new ArrayList<String>();

        synchronized (wifiNetworkStatus)
        {
            // clear all the savedConfigurations AP status
            if (!wifiNetworkStatus.isEmpty())
            {
                App.getLogger().startTrace(TAG, "Clear scan status from AP configs", Log.DEBUG);
                for (WiFiAPConfig conf : wifiNetworkStatus.values())
                {
                    conf.clearScanStatus();
                }
                App.getLogger().stopTrace(TAG, "Clear scan status from AP configs", Log.DEBUG);
            }

            for (ScanResult res : scanResults)
            {
                scanResultsStrings.add(res.SSID + " level: " + res.level);
                String currSSID = ProxyUtils.cleanUpSSID(res.SSID);
                SecurityType security = ProxyUtils.getSecurity(res);
                APLNetworkId aplNetworkId = new APLNetworkId(currSSID, security);

                if (wifiNetworkStatus.containsKey(aplNetworkId))
                {
                    WiFiAPConfig conf = wifiNetworkStatus.get(aplNetworkId);
                    if (conf != null)
                    {
                        conf.updateScanResults(res);
                    }
                }
                else
                {
                    if (wifiNetworkStatus.getNotConfiguredWifi().containsKey(aplNetworkId))
                    {
                        wifiNetworkStatus.getNotConfiguredWifi().remove(aplNetworkId);
                    }

                    wifiNetworkStatus.getNotConfiguredWifi().put(aplNetworkId, res);
                }
            }
        }

        App.getLogger().d(TAG, "Updating from scanresult: " + TextUtils.join(", ", scanResultsStrings.toArray()));
    }

//    private Map<APLNetworkId, WiFiAPConfig> getWifiApConfigsByAPLNetId()
//    {
//        return wifiNetworkStatus.wifiApConfigsByAPLNetId;
//    }

    public List<WiFiAPConfig> getSortedWifiApConfigsList()
    {
        App.getLogger().startTrace(TAG, "getSortedWifiApConfigsList", Log.DEBUG);

        if (wifiNetworkStatus.isEmpty())
        {
            updateWifiApConfigs();
            App.getLogger().partialTrace(TAG, "getSortedWifiApConfigsList", "updateWifiApConfigs", Log.DEBUG);
        }

        List<WiFiAPConfig> list = null;

        synchronized (wifiNetworkStatus)
        {
            list = new ArrayList<WiFiAPConfig>(wifiNetworkStatus.values());
            App.getLogger().partialTrace(TAG, "getSortedWifiApConfigsList", "new ArrayList", Log.DEBUG);

            try
            {
                Collections.sort(list);
            }
            catch (IllegalArgumentException e)
            {
                Map<String, String> map = new HashMap<String, String>();
                map.put("config_list", configListToDBG().toString());
                App.getEventsReporter().sendException(e, map);
            }
        }

        App.getLogger().partialTrace(TAG, "getSortedWifiApConfigsList", "Collections.sort", Log.DEBUG);
        App.getLogger().stopTrace(TAG, "getSortedWifiApConfigsList", Log.DEBUG);

        return list;
    }

    public WiFiAPConfig getConfiguration(APLNetworkId aplNetworkId)
    {
        WiFiAPConfig selected = null;

        synchronized (wifiNetworkStatus)
        {
            if (wifiNetworkStatus.containsKey(aplNetworkId))
            {
//                selected = new WiFiAPConfig(wifiNetworkStatus.wifiApConfigsByAPLNetId.get(aplNetworkId));
                try
                {
                    selected = (WiFiAPConfig) wifiNetworkStatus.get(aplNetworkId);
                }
                catch (Exception e)
                {
                    App.getEventsReporter().sendException(e);
                }
            }
        }

        return selected;
    }

//    private String getConfigurationsString()
//    {
//        if (!getWifiApConfigsByAPLNetId().isEmpty())
//        {
//            return TextUtils.join(", ", getWifiApConfigsByAPLNetId().keySet());
//        }
//        else
//        {
//            return "No configured Wi-Fi networks";
//        }
//    }

    public WiFiAPConfig updateCurrentConfiguration()
    {
        WiFiAPConfig updatedConf = null;

        App.getLogger().startTrace(TAG, "updateCurrentConfiguration", Log.INFO);

        if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
        {
            WifiInfo info = APL.getWifiManager().getConnectionInfo();
            if (info != null)
            {
                int networkId = info.getNetworkId();
                if (wifiNetworkStatus.containsKey(networkId))
                {
                    updatedConf = wifiNetworkStatus.get(networkId);
                }

                mergeWithCurrentConfiguration(updatedConf);
            }
        }

        App.getLogger().stopTrace(TAG, "updateCurrentConfiguration", Log.INFO);

        return updatedConf;
    }

    private void mergeWithCurrentConfiguration(WiFiAPConfig updated)
    {
        if (wifiNetworkStatus.getCurrentConfiguration() == null)
        {
            if (updated != null)
            {
                wifiNetworkStatus.setCurrentConfiguration(updated);
                App.getLogger().d(TAG, "updateCurrentConfiguration - Set current configuration (was NULL before)");
            }
            else
            {
                App.getLogger().d(TAG, "updateCurrentConfiguration - Same configuration: no need to update it (both NULL)");
            }
        }
        else if (updated != null && wifiNetworkStatus.getCurrentConfiguration().compareTo(updated) != 0)
        {
            // Update currentConfiguration only if it's different from the previous
            wifiNetworkStatus.setCurrentConfiguration(updated);
            App.getLogger().d(TAG, "updateCurrentConfiguration - Updated current configuration");
        }
        else
        {
            App.getLogger().d(TAG, "updateCurrentConfiguration - Same configuration: no need to update it");
        }
    }

    public WiFiAPConfig getCachedConfiguration()
    {
//        if (currentConfiguration == null)
//        {
            return updateCurrentConfiguration();
//        }
//
//        return currentConfiguration;
    }

//    /**
//     * If necessary updates the configuration list and sort it
//     *
//     * @return the sorted list of current proxy savedConfigurations
//     */
//    private List<WiFiAPConfig> getConfigurationsList()
//    {
//        return wifiAPConfigList;
//    }
    /**
     * Updates the proxy configuration list
     */

//    public synchronized void updateProxyConfigurationList()
//    {
//        App.getLogger().startTrace(TAG, "updateProxyConfigurationList", Log.DEBUG);
//
//        //Get information regarding current saved configuration
//        List<APLNetworkId> internalSavedSSID = getInternalSavedWifiConfigurations();
//
//        //Get latests information regarding configured AP
//        List<APLNetworkId> notMoreConfiguredSSID = updateCachedWifiAP(internalSavedSSID);
//
//        // Remove from current configuration the SSID that are not more configured into Android's Wi-Fi settings
//        removeNotMoreConfiguredSSID(notMoreConfiguredSSID);
//
//        // Update savedConfigurations with latest Wi-Fi scan results
////        updateWifiApConfigs();
//
//        // If the configuration has been updated sort again the list!!
////        if (updatedConfiguration && !getWifiApConfigsByAPLNetId().isEmpty())
//        {
//            App.getLogger().d(TAG, "Configuration updated -> need to create again the sorted list");
//        }
//
//        App.getLogger().d(TAG, "Final savedConfigurations list: " + getConfigurationsString());
//        App.getLogger().stopTrace(TAG, "updateProxyConfigurationList", Log.DEBUG);
//    }

//    private void removeNotMoreConfiguredSSID(List<APLNetworkId> internalSavedSSID)
//    {
////        LogWrapper.startTrace(TAG,"removeNoMoreConfiguredSSID", Log.DEBUG);
//        if (!getWifiApConfigsByAPLNetId().isEmpty())
//        {
//            for (APLNetworkId netId : internalSavedSSID)
//            {
//                if (getWifiApConfigsByAPLNetId().containsKey(netId))
//                {
//                    WiFiAPConfig removed = getWifiApConfigsByAPLNetId().remove(netId);
//                    App.getLogger().w(TAG, "Removing from Proxy Settings configuration a no more configured SSID: " + removed.toShortString());
//                }
//            }
//
////            LogWrapper.d(TAG, "Cleaned up savedConfigurations list: " + getConfigurationsString());
//
//        }
////        LogWrapper.stopTrace(TAG,"removeNoMoreConfiguredSSID", Log.DEBUG);
//    }

//    private List<APLNetworkId> updateCachedWifiAP(List<APLNetworkId> internalSavedSSID)
//    {
////        LogWrapper.startTrace(TAG,"getSavedConfigurations", Log.DEBUG);
//
//        // Get updated list of Proxy savedConfigurations from APL
//        List<WiFiAPConfig> updatedConfigurations = new ArrayList<WiFiAPConfig>(APL.getWifiAPConfigurations().values());
//        if (updatedConfigurations != null)
//        {
//            for (WiFiAPConfig conf : updatedConfigurations)
//            {
//                if (conf != null)
//                {
//                    wifiNetworkStatus.wifiApConfigsByAPLNetId = getWifiApConfigsByAPLNetId();
//                    if (wifiNetworkStatus.wifiApConfigsByAPLNetId != null
//                            && conf.getAPLNetworkId() != null
//                            && wifiNetworkStatus.wifiApConfigsByAPLNetId.containsKey(conf.getAPLNetworkId()))
//                    {
//                        // Updates already saved configuration
//                        WiFiAPConfig originalConf = getWifiApConfigsByAPLNetId().get(conf.getAPLNetworkId());
////                        if (originalConf.updateProxyConfiguration(conf))
////                            updatedConfiguration = true;
//                    }
//                    else
//                    {
//                        // Add new found configuration
////                        App.getLogger().d(TAG, "Adding to list new Wi-Fi AP configuration: " + conf.toShortString());
//                        getWifiApConfigsByAPLNetId().put(conf.getAPLNetworkId(), conf);
//                    }
//
//                    if (internalSavedSSID.contains(conf.getAPLNetworkId()))
//                    {
//                        internalSavedSSID.remove(conf.getAPLNetworkId());
//                    }
//                }
//            }
//        }
//
////        LogWrapper.d(TAG,"Updated savedConfigurations list: " + getConfigurationsString());
////        LogWrapper.d(TAG,"Configurations that need to be removed: " + TextUtils.join(", " , internalSavedSSID));
//
////        LogWrapper.stopTrace(TAG,"getSavedConfigurations", Log.DEBUG);
//
//        return internalSavedSSID;
//    }

//    private List<APLNetworkId> getInternalSavedWifiConfigurations()
//    {
////        LogWrapper.startTrace(TAG,"getSavedConfigurations", Log.DEBUG);
//
//        Collection<APLNetworkId> savedNetworks = null;
//        List<APLNetworkId> internalSavedSSID = new ArrayList<APLNetworkId>();
//
//        if (!getWifiApConfigsByAPLNetId().isEmpty())
//        {
//            savedNetworks = getWifiApConfigsByAPLNetId().keySet();
//            for (APLNetworkId wifiNet : savedNetworks)
//            {
//                internalSavedSSID.add(wifiNet);
//            }
//        }
//
////        LogWrapper.stopTrace(TAG,"getSavedConfigurations", Log.DEBUG);
//
//        return internalSavedSSID;
//    }

    public JSONObject configListToDBG()
    {
        JSONObject dbg = new JSONObject();

        try
        {
            JSONArray configurations = new JSONArray();

            synchronized (wifiNetworkStatus)
            {
                for (WiFiAPConfig conf : getSortedWifiApConfigsList())
                {
                    configurations.put(conf.toJSON());
                }
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
