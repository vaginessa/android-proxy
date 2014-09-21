package be.shouldit.proxy.lib;

import android.net.wifi.WifiManager;

import java.util.Comparator;

/**
 * Created by Marco on 21/09/14.
 */
public class WifiApConfigComparator implements Comparator<WiFiAPConfig>
{
    @Override
    public int compare(WiFiAPConfig wiFiAPConfig, WiFiAPConfig wiFiAPConfig2)
    {
        return compareWifiAp(wiFiAPConfig, wiFiAPConfig2);
    }

    public static int compareWifiAp(WiFiAPConfig current, WiFiAPConfig other)
    {
        // Active one goes first.
        if (current.isActive() && !other.isActive()) return -1;
        if (!current.isActive() && other.isActive()) return 1;

        // Reachable one goes before unreachable one.
        if (current.isReachable() && !other.isReachable())
        {
            return -1;
        }

        if (!current.isReachable() && other.isReachable())
        {
            return 1;
        }

        // Configured one goes before unconfigured one.
        if (current.getNetworkId() != WiFiAPConfig.INVALID_NETWORK_ID
                && other.getNetworkId() == WiFiAPConfig.INVALID_NETWORK_ID)
        {
            return -1;
        }

        if (current.getNetworkId() == WiFiAPConfig.INVALID_NETWORK_ID
                && other.getNetworkId() != WiFiAPConfig.INVALID_NETWORK_ID)
        {
            return 1;
        }

        // Sort by signal strength.
        int difference = WifiManager.compareSignalLevel(other.getRssi(), current.getRssi());
        if (difference != 0)
        {
            return difference;
        }

        // Sort by ssid
        return current.getSSID().compareToIgnoreCase(other.getSSID());
    }
}
