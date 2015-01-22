package com.lechucksoftware.proxy.proxysettings;

import android.net.wifi.ScanResult;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import timber.log.Timber;

/**
 * Created by Marco on 21/08/14.
 */
public class WifiNetworkStatus
{
    private static final String TAG = WifiNetworkStatus.class.getSimpleName();
    private Map<APLNetworkId, WiFiApConfig> wifiApConfigsByAPLNetId;
    private Map<Integer, WiFiApConfig> wifiApConfigsByWifiNetworkId;
    private Map<APLNetworkId, ScanResult> notConfiguredWifi; // Wi-Fi networks available but still not configured into Android's Wi-Fi settings

    private WiFiApConfig currentConfiguration;

    public WifiNetworkStatus()
    {
        wifiApConfigsByWifiNetworkId = new ConcurrentHashMap<Integer, WiFiApConfig>();
        wifiApConfigsByAPLNetId = new ConcurrentHashMap<APLNetworkId, WiFiApConfig>();
        notConfiguredWifi = new ConcurrentHashMap<APLNetworkId, ScanResult>();
    }

    public boolean isEmpty()
    {
        return wifiApConfigsByAPLNetId.isEmpty();
    }

    public boolean containsKey(APLNetworkId aplNetworkId)
    {
        return wifiApConfigsByAPLNetId.containsKey(aplNetworkId);
    }

    public boolean containsKey(int networkId)
    {
        return wifiApConfigsByWifiNetworkId.containsKey(networkId);
    }

    public WiFiApConfig get(APLNetworkId aplNetworkId)
    {
        return wifiApConfigsByAPLNetId.get(aplNetworkId);
    }

    public WiFiApConfig get(int networkId)
    {
        return wifiApConfigsByWifiNetworkId.get(networkId);
    }

    public void put(APLNetworkId aplNetworkId, WiFiApConfig wiFiAPConfig)
    {
        if (aplNetworkId == null)
        {
            Timber.e(new Exception(),"Trying to put a Wi-Fi network using a NULL APLNetworkId");
            return;
        }

        if (wiFiAPConfig == null)
        {
            Timber.e(new Exception(),"Trying to put a Wi-Fi network using a NULL WiFiApConfig");
            return;
        }

        Timber.d("Adding '%s' Wi-Fi network to WifiNetworkStatus object", wiFiAPConfig.getSSID());
        wifiApConfigsByAPLNetId.put(aplNetworkId, wiFiAPConfig);
        wifiApConfigsByWifiNetworkId.put(wiFiAPConfig.getNetworkId(), wiFiAPConfig);
    }

    public void remove(APLNetworkId aplNetworkId)
    {
        if (aplNetworkId == null)
        {
            Timber.e(new Exception(),"Trying to remove a Wi-Fi network using a NULL APLNetworkId");
            return;
        }

        Timber.d("Removing '%s' Wi-Fi network from WifiNetworkStatus object", aplNetworkId.SSID);

        WiFiApConfig toRemove = null;
        if (wifiApConfigsByAPLNetId.containsKey(aplNetworkId))
        {
            toRemove = wifiApConfigsByAPLNetId.remove(aplNetworkId);
        }
        else
        {
            Timber.e(new Exception(),"Trying to remove a Wi-Fi network not available into the wifiApConfigsByAPLNetId MAP");
        }

        if (toRemove != null && wifiApConfigsByWifiNetworkId.containsKey(toRemove.getNetworkId()))
        {
            wifiApConfigsByWifiNetworkId.remove(toRemove.getNetworkId());
        }
        else
        {
            Timber.e(new Exception(),"Trying to remove a Wi-Fi network not available into the wifiApConfigsByWifiNetworkId MAP");
        }
    }

    public Collection<WiFiApConfig> values()
    {
        return wifiApConfigsByAPLNetId.values();
    }

    public Map<APLNetworkId, ScanResult> getNotConfiguredWifi()
    {
        return notConfiguredWifi;
    }

    public WiFiApConfig getCurrentConfiguration()
    {
        return currentConfiguration;
    }

    public void setCurrentConfiguration(WiFiApConfig currentConfiguration)
    {
        this.currentConfiguration = currentConfiguration;
    }
}
