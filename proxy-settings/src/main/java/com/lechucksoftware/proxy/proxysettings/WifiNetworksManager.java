package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.services.SaveWifiNetworkService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import timber.log.Timber;

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

    public void asyncSaveWifiApConfig(WiFiApConfig configuration)
    {
        if (configuration != null)
        {
            Intent serviceIntent = new Intent(context, SaveWifiNetworkService.class);
            serviceIntent.putExtra(Constants.WIFI_AP_NETWORK_ARG, configuration);
            context.startService(serviceIntent);
        }
    }

    public void updateWifiApConfigs()
    {
        synchronized (wifiNetworkStatus)
        {
            App.getTraceUtils().startTrace(TAG,"updateWifiApConfigs", Log.DEBUG, true);

            Map<APLNetworkId,WiFiApConfig> configurations = APL.getWifiAPConfigurations();
            for (APLNetworkId aplNetworkId : configurations.keySet())
            {
                wifiNetworkStatus.put(aplNetworkId,configurations.get(aplNetworkId));
            }

            App.getTraceUtils().partialTrace(TAG,"updateWifiApConfigs", "Got WifiAPConfigurations from APL", Log.DEBUG);

            updateWifiConfigWithScanResults(APL.getWifiManager().getScanResults());

            App.getTraceUtils().partialTrace(TAG,"updateWifiApConfigs", "Updated wifi network status with ScanResults", Log.DEBUG);
            App.getTraceUtils().stopTrace(TAG, "updateWifiApConfigs", Log.DEBUG);
        }
    }

    public void updateWifiConfig(WiFiApConfig updatedConfiguration)
    {
        synchronized (wifiNetworkStatus)
        {
//            if (wifiNetworkStatus.getWifiApConfigsByAPLNetId() != null)
//            {
                APLNetworkId aplNetworkId = updatedConfiguration.getAPLNetworkId();

                if (wifiNetworkStatus.containsKey(aplNetworkId))
                {
                    WiFiApConfig currentConfiguration = wifiNetworkStatus.get(aplNetworkId);
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
        App.getTraceUtils().startTrace(TAG,"updateCurrentWifiInfo", Log.DEBUG);

        synchronized (wifiNetworkStatus)
        {
            if (!wifiNetworkStatus.isEmpty())
            {
                for (WiFiApConfig conf : wifiNetworkStatus.values())
                {
                    conf.updateWifiInfo(currentWifiInfo, null);
                }
            }
        }

        App.getTraceUtils().stopTrace(TAG,"updateCurrentWifiInfo", Log.DEBUG);
    }

    public void updateWifiConfigWithScanResults(List<ScanResult> scanResults)
    {
        List<String> scanResultsStrings = new ArrayList<String>();

        synchronized (wifiNetworkStatus)
        {
            // clear all the savedConfigurations AP status
            if (!wifiNetworkStatus.isEmpty())
            {
                App.getTraceUtils().startTrace(TAG, "Clear scan status from AP configs", Log.DEBUG);
                for (WiFiApConfig conf : wifiNetworkStatus.values())
                {
                    conf.clearScanStatus();
                }
                App.getTraceUtils().stopTrace(TAG, "Clear scan status from AP configs", Log.DEBUG);
            }

            if (scanResults != null)
            {
                for (ScanResult res : scanResults)
                {
                    scanResultsStrings.add(res.SSID + " level: " + res.level);
                    String currSSID = ProxyUtils.cleanUpSSID(res.SSID);
                    SecurityType security = ProxyUtils.getSecurity(res);
                    APLNetworkId aplNetworkId = new APLNetworkId(currSSID, security);

                    if (wifiNetworkStatus.containsKey(aplNetworkId))
                    {
                        WiFiApConfig conf = wifiNetworkStatus.get(aplNetworkId);
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
            else
            {
                Timber.w("No ScanResults available for updateWifiConfigWithScanResults");
            }
        }

       Timber.d("Updating from scanresult: " + TextUtils.join(", ", scanResultsStrings.toArray()));
    }

    public List<WiFiApConfig> getSortedWifiApConfigsList()
    {
        App.getTraceUtils().startTrace(TAG, "getSortedWifiApConfigsList", Log.DEBUG);

        if (wifiNetworkStatus.isEmpty())
        {
            updateWifiApConfigs();
            App.getTraceUtils().partialTrace(TAG, "getSortedWifiApConfigsList", "updateWifiApConfigs", Log.DEBUG);
        }

        List<WiFiApConfig> list = null;

        synchronized (wifiNetworkStatus)
        {
            list = new ArrayList<WiFiApConfig>(wifiNetworkStatus.values());
            App.getTraceUtils().partialTrace(TAG, "getSortedWifiApConfigsList", "new ArrayList", Log.DEBUG);

            try
            {
                Collections.sort(list);
            }
            catch (IllegalArgumentException e)
            {
                Timber.e("config_list", configListToDBG().toString());
                Timber.e(e, "Exception during sort of WiFiAPConfigs");
            }
        }

        App.getTraceUtils().partialTrace(TAG, "getSortedWifiApConfigsList", "Collections.sort", Log.DEBUG);
        App.getTraceUtils().stopTrace(TAG, "getSortedWifiApConfigsList", Log.DEBUG);

        return list;
    }

    public WiFiApConfig getConfiguration(APLNetworkId aplNetworkId)
    {
        WiFiApConfig selected = null;

        synchronized (wifiNetworkStatus)
        {
            if (wifiNetworkStatus.containsKey(aplNetworkId))
            {
                try
                {
                    selected = (WiFiApConfig) wifiNetworkStatus.get(aplNetworkId);
                }
                catch (Exception e)
                {
                    Timber.e(e,"Exception retrieving WiFiApConfig from APLNetworkId");
                }
            }
        }

        return selected;
    }

    public WiFiApConfig updateCurrentConfiguration()
    {
        WiFiApConfig updatedConf = null;

        App.getTraceUtils().startTrace(TAG, "updateCurrentConfiguration", Log.DEBUG);

        try
        {

            if (APL.getWifiManager() != null && APL.getWifiManager().isWifiEnabled())
            {
                WifiInfo info = APL.getWifiManager().getConnectionInfo();

                if (info != null)
                {
                    int networkId = info.getNetworkId();

                    if (networkId != -1)
                    {
                        WifiConfiguration wifiConfiguration = APL.getConfiguredNetwork(networkId);

                        if (wifiConfiguration != null)
                        {
                            WiFiApConfig networkConfig = APL.getWiFiAPConfiguration(wifiConfiguration);

                            synchronized (wifiNetworkStatus)
                            {
                                if (wifiNetworkStatus.containsKey(networkConfig.getAPLNetworkId()))
                                {
                                    updatedConf = wifiNetworkStatus.get(networkConfig.getAPLNetworkId());
                                }

                                mergeWithCurrentConfiguration(updatedConf);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception updating current configuration");
        }

        App.getTraceUtils().stopTrace(TAG, "updateCurrentConfiguration", Log.DEBUG);

        return updatedConf;
    }

    private void mergeWithCurrentConfiguration(WiFiApConfig updated)
    {
        if (wifiNetworkStatus.getCurrentConfiguration() == null)
        {
            if (updated != null)
            {
                wifiNetworkStatus.setCurrentConfiguration(updated);
                Timber.d("updateCurrentConfiguration - Set current configuration (was NULL before)");
            }
            else
            {
                Timber.d("updateCurrentConfiguration - Same configuration: no need to update it (both NULL)");
            }
        }
        else if (updated != null && wifiNetworkStatus.getCurrentConfiguration().compareTo(updated) != 0)
        {
            // Update currentConfiguration only if it's different from the previous
            wifiNetworkStatus.setCurrentConfiguration(updated);
            Timber.d("updateCurrentConfiguration - Updated current configuration");
        }
        else
        {
            Timber.d("updateCurrentConfiguration - Same configuration: no need to update it");
        }
    }

    public WiFiApConfig getCachedConfiguration()
    {
//        if (currentConfiguration == null)
//        {
            return updateCurrentConfiguration();
//        }
//
//        return currentConfiguration;
    }

    public JSONObject configListToDBG()
    {
        JSONObject dbg = new JSONObject();

        try
        {
            JSONArray configurations = new JSONArray();

            synchronized (wifiNetworkStatus)
            {
                for (WiFiApConfig conf : getSortedWifiApConfigsList())
                {
                    configurations.put(conf.toJSON());
                }
            }

            dbg.put("configurations", configurations);
        }
        catch (JSONException e)
        {
            Timber.e(e, "Exception preparing configuration list to JSON");
        }

        return dbg;
    }
}
