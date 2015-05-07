package com.lechucksoftware.proxy.proxysettings;

import android.net.wifi.ScanResult;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import timber.log.Timber;

/**
 * Created by Marco on 21/08/14.
 */
public class WifiNetworkStatus
{
    private Map<APLNetworkId, WiFiApConfig> wifiApConfigsByAPLNetId;

    // Wi-Fi networks available but still not configured into Android's Wi-Fi settings
    private Map<APLNetworkId, ScanResult> notConfiguredWifi;

    private WiFiApConfig currentConfiguration;

    public WifiNetworkStatus()
    {
        wifiApConfigsByAPLNetId = new ConcurrentHashMap<APLNetworkId, WiFiApConfig>();
        notConfiguredWifi = new ConcurrentHashMap<APLNetworkId, ScanResult>();
    }

    public boolean isEmpty()
    {
        return wifiApConfigsByAPLNetId != null && wifiApConfigsByAPLNetId.isEmpty();
    }

    public boolean containsKey(APLNetworkId aplNetworkId)
    {
        return wifiApConfigsByAPLNetId != null && wifiApConfigsByAPLNetId.containsKey(aplNetworkId);
    }

    public WiFiApConfig get(APLNetworkId aplNetworkId)
    {
        return wifiApConfigsByAPLNetId.get(aplNetworkId);
    }

    public void put(APLNetworkId aplNetworkId, WiFiApConfig wiFiApConfig)
    {
        if (aplNetworkId == null)
        {
            Timber.e(new Exception(),"Trying to put a Wi-Fi network using a NULL APLNetworkId");
            return;
        }

        if (wiFiApConfig == null)
        {
            Timber.e(new Exception(),"Trying to put a Wi-Fi network using a NULL WiFiApConfig");
            return;
        }

        Timber.d("Adding '%s' Wi-Fi network to WifiNetworkStatus object", wiFiApConfig.getSSID());
        wifiApConfigsByAPLNetId.put(aplNetworkId, wiFiApConfig);
//        wifiApConfigsByWifiNetworkId.put(wiFiApConfig.getNetworkId(), wiFiApConfig);
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
            Timber.w("Trying to remove a Wi-Fi network not available into the wifiApConfigsByAPLNetId MAP");
        }

//        if (toRemove != null && wifiApConfigsByWifiNetworkId.containsKey(toRemove.getNetworkId()))
//        {
//            wifiApConfigsByWifiNetworkId.remove(toRemove.getNetworkId());
//        }
//        else
//        {
//            Timber.w("Trying to remove a Wi-Fi network not available into the wifiApConfigsByWifiNetworkId MAP");
//        }
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
