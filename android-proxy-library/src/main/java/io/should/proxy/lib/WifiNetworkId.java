package io.should.proxy.lib;

import io.should.proxy.lib.enums.SecurityType;

/**
 * Created by Marco on 08/06/13.
 */
public class WifiNetworkId
{
    public String SSID;
    public SecurityType Security;

    public WifiNetworkId(String ssid, SecurityType sec)
    {
        SSID = ssid;
        Security = sec;
    }

    @Override
    public boolean equals(Object another)
    {
        Boolean result = false;

        if ((another instanceof WifiNetworkId))
        {
            WifiNetworkId anotherWifi = (WifiNetworkId) another;

            if (SSID.equals(anotherWifi.SSID))
            {
                if (Security != null && anotherWifi.Security != null && Security.equals(anotherWifi.Security))
                    result = true;
                else
                    result = false;
            }
        }

        return result;
    }

    @Override
    public int hashCode()
    {
        int ssidHash = SSID.hashCode();
        int secHash = Security.hashCode();

        return ssidHash + secHash;
    }

    @Override
    public String toString()
    {
        return String.format("%s - %s", SSID, Security);
    }
}
