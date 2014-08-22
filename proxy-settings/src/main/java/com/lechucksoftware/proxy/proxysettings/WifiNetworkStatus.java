package com.lechucksoftware.proxy.proxysettings;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiAPConfig;

/**
 * Created by Marco on 21/08/14.
 */
public class WifiNetworkStatus
{
    public Map<APLNetworkId, WiFiAPConfig> wifiApConfigsByAPLNetId;
    //    private Map<APLNetworkId, WiFiAPEntity> wifiApEntitiesByAPLNetId;
    public Map<Integer, WiFiAPConfig> wifiApConfigsByWifiNetworkId;

    public List<WiFiAPConfig> wifiAPConfigList;
    public WiFiAPConfig currentConfiguration;

//    private Boolean updatedConfiguration;

    public Map<APLNetworkId, ScanResult> notConfiguredWifi; // Wi-Fi networks available but still not configured into Android's Wi-Fi settings


    public WifiNetworkStatus()
    {
        wifiApConfigsByWifiNetworkId = new HashMap<Integer, WiFiAPConfig>();
        wifiApConfigsByAPLNetId = new HashMap<APLNetworkId, WiFiAPConfig>();
//        wifiApEntitiesByAPLNetId = Collections.synchronizedMap(new HashMap<APLNetworkId, WiFiAPEntity>());

        notConfiguredWifi = new HashMap<APLNetworkId, ScanResult>();
        wifiAPConfigList = new ArrayList<WiFiAPConfig>();
    }
}
