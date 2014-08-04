package be.shouldit.proxy.lib;

import android.annotation.TargetApi;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.UUID;

import be.shouldit.proxy.lib.enums.CheckStatusValues;
import be.shouldit.proxy.lib.enums.PskType;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import be.shouldit.proxy.lib.utils.ProxyUtils;

public class WiFiAPConfig implements Comparable<WiFiAPConfig>, Serializable
{
    public static final String TAG = WiFiAPConfig.class.getSimpleName();

    public final UUID id;
    public final WifiNetworkId internalWifiNetworkId;

    public ProxyStatus status;

    private String apDescription;

    private ProxySetting proxySetting;
    private String proxyHost;
    private Integer proxyPort;
    private String stringProxyExclusionList;
    private String[] parsedProxyExclusionList;

    /* AccessPoint class fields */
//    public AccessPoint ap;
    public static final int[] STATE_SECURED = {R.attr.state_encrypted};
    public static final int[] STATE_NONE = {};

    public static final int INVALID_NETWORK_ID = -1;

    public String ssid;
    public String bssid;
    public SecurityType security;
    public int networkId;
    public PskType pskType = PskType.UNKNOWN;
    public transient WifiConfiguration wifiConfig;
    private WifiInfo mInfo;
    private int mRssi;
    private NetworkInfo.DetailedState mState;


    public WiFiAPConfig(ProxySetting setting, String host, Integer port, String exclusionList, WifiConfiguration wifiConf)
    {
        id = UUID.randomUUID();

        setProxySetting(setting);
        proxyHost = host;
        proxyPort = port;
        setProxyExclusionString(exclusionList);

//        ap = new AccessPoint(wifiConf);
        ssid = (wifiConf.SSID == null ? "" : removeDoubleQuotes(wifiConf.SSID));
        bssid = wifiConf.BSSID;
        security = ProxyUtils.getSecurity(wifiConf);
        networkId = wifiConf.networkId;
        mRssi = Integer.MAX_VALUE;
        wifiConfig = wifiConf;

        internalWifiNetworkId = new WifiNetworkId(ssid, security);

        status = new ProxyStatus();
    }

    public boolean updateScanResults(ScanResult result)
    {
        if (ssid.equals(result.SSID) && security == ProxyUtils.getSecurity(result))
        {
            if (WifiManager.compareSignalLevel(result.level, mRssi) > 0)
            {
                int oldLevel = getLevel();
                mRssi = result.level;
            }
            // This flag only comes from scans, is not easily saved in config
            if (security == SecurityType.SECURITY_PSK)
            {
                pskType = ProxyUtils.getPskType(result);
            }

            return true;
        }
        return false;
    }

    public void updateWifiInfo(WifiInfo info, NetworkInfo.DetailedState state)
    {
        if (info != null
            && networkId != INVALID_NETWORK_ID
            && networkId == info.getNetworkId())
        {
            mRssi = info.getRssi();
            mInfo = info;
            mState = state;
        }
        else if (mInfo != null)
        {
            mInfo = null;
            mState = null;
        }
    }

    public boolean updateProxyConfiguration(WiFiAPConfig updated)
    {
        //TODO: Add all required fields for updating an old configuration with an updated version
        if (!this.isSameConfiguration(updated))
        {
            APL.getLogger().d(TAG, "Updating proxy configuration: \n" + this.toShortString() + "\n" + updated.toShortString());

            setProxySetting(updated.getProxySettings());
            proxyHost = updated.proxyHost;
            proxyPort = updated.proxyPort;
            stringProxyExclusionList = updated.stringProxyExclusionList;
            parsedProxyExclusionList = ProxyUtils.parseExclusionList(stringProxyExclusionList);

            status.clear();

            APL.getLogger().d(TAG, "Updated proxy configuration: \n" + this.toShortString() + "\n" + updated.toShortString());

            return true;
        }
        else
        {
//            LogWrapper.d(TAG,"No need to update proxy configuration: " + this.toShortString());
            return false;
        }
    }

    public Proxy getProxy()
    {
        if (getProxySettings() == ProxySetting.STATIC && proxyHost != null && proxyPort != null)
        {
            SocketAddress sa = null;

            if (isValidProxyConfiguration())
            {
                try
                {
                    sa = InetSocketAddress.createUnresolved(proxyHost, proxyPort);
                }
                catch (Exception e)
                {
                    APL.getEventsReporter().sendException(new Exception("Failed creating unresolved", e));
                }
            }

            if (sa != null)
                return new Proxy(Proxy.Type.HTTP, sa);
            else
                return Proxy.NO_PROXY;
        }
        else
        {
            return Proxy.NO_PROXY;
        }
    }

    public boolean isValidProxyConfiguration()
    {
        boolean result = false;
        ProxyStatusItem hostStatus = ProxyUtils.isProxyValidHostname(this);
        ProxyStatusItem portStatus = ProxyUtils.isProxyValidPort(this);
        ProxyStatusItem exclStatus = ProxyUtils.isProxyValidExclusionList(this);

        if (hostStatus.effective && hostStatus.status == CheckStatusValues.CHECKED && hostStatus.result
                && portStatus.effective && portStatus.status == CheckStatusValues.CHECKED && portStatus.result
                && exclStatus.effective && exclStatus.status == CheckStatusValues.CHECKED && exclStatus.result)
        {
            result = true;
        }

        return result;
    }

    public void setProxySetting(ProxySetting setting)
    {
        synchronized (id)
        {
            proxySetting = setting;
        }
    }

    public ProxySetting getProxySettings()
    {
        synchronized (id)
        {
            return proxySetting;
        }
    }

    public void setProxyHost(String host)
    {
        proxyHost = host;
    }

    public void setProxyPort(Integer port)
    {
        proxyPort = port;
    }

    public void setProxyExclusionString(String exList)
    {
        stringProxyExclusionList = exList;
        parsedProxyExclusionList = ProxyUtils.parseExclusionList(exList);
    }

    public boolean isSameConfiguration(Object another)
    {
        if (!(another instanceof WiFiAPConfig))
        {
            APL.getLogger().e(TAG, "Not a WiFiAPConfig object");
            return false;
        }

        WiFiAPConfig anotherConf = (WiFiAPConfig) another;

        if (!this.proxySetting.equals(anotherConf.proxySetting))
        {
            APL.getLogger().d(TAG, String.format("Different proxy settings toggle status: '%s' - '%s'", this.proxySetting, anotherConf.proxySetting));
            return false;
        }

        if (this.proxyHost != null && anotherConf.proxyHost != null)
        {
            if (!this.proxyHost.equalsIgnoreCase(anotherConf.proxyHost))
            {
                APL.getLogger().d(TAG, String.format("Different proxy host value: '%s' - '%s'", this.proxyHost, anotherConf.proxyHost));
                return false;
            }
        }
        else if (this.proxyHost != anotherConf.proxyHost)
        {
            if ((this.proxyHost == null || this.proxyHost.equals("") && (anotherConf.proxyHost == null || anotherConf.proxyHost.equals(""))))
            {
                /** Can happen when a partial configuration is written on the device:
                 *  - ProxySettings enabled but no proxyHost and proxyPort are filled
                 */
            }
            else
            {
                APL.getLogger().d(TAG, String.format("Different proxy host set"));
                APL.getLogger().d(TAG, TextUtils.isEmpty(this.proxyHost) ? "" : this.proxyHost);
                APL.getLogger().d(TAG, TextUtils.isEmpty(anotherConf.proxyHost) ? "" : anotherConf.proxyHost);
                return false;
            }
        }

        if (this.proxyPort != null && anotherConf.proxyPort != null)
        {
            if (!this.proxyPort.equals(anotherConf.proxyPort))
            {
                APL.getLogger().d(TAG, String.format("Different proxy port value: '%d' - '%d'", this.proxyPort, anotherConf.proxyPort));
                return false;
            }
        }
        else if (this.proxyPort != anotherConf.proxyPort)
        {
            if ((this.proxyPort == null || this.proxyPort == 0) && (anotherConf.proxyPort == null || anotherConf.proxyPort == 0))
            {
                /** Can happen when a partial configuration is written on the device:
                 *  - ProxySettings enabled but no proxyHost and proxyPort are filled
                 */
            }
            else
            {
                APL.getLogger().d(TAG, "Different proxy port set");
                return false;
            }
        }

        if (this.stringProxyExclusionList != null && anotherConf.stringProxyExclusionList != null)
        {
            if (!this.stringProxyExclusionList.equalsIgnoreCase(anotherConf.stringProxyExclusionList))
            {
                APL.getLogger().d(TAG, String.format("Different proxy exclusion list value: '%s' - '%s'", this.stringProxyExclusionList, anotherConf.stringProxyExclusionList));
                return false;
            }
        }
        else if (this.stringProxyExclusionList != anotherConf.stringProxyExclusionList)
        {
            if (TextUtils.isEmpty(this.stringProxyExclusionList) && TextUtils.isEmpty(anotherConf.stringProxyExclusionList))
            {
                /** Can happen when a partial configuration is written on the device:
                 *  - ProxySettings enabled but no proxyHost and proxyPort are filled
                 */
            }
            else
            {
                APL.getLogger().d(TAG, "Different proxy exclusion list set");
                return false;
            }
        }

//        LogWrapper.d(TAG,"Same proxy configuration: \n" +  this.toShortString() + "\n" +  anotherConf.toShortString());
        return true;
    }

    @Override
    public int compareTo(WiFiAPConfig wiFiAPConfig) {

        if (!(wiFiAPConfig instanceof WiFiAPConfig))
        {
            return 1;
        }

        WiFiAPConfig other = (WiFiAPConfig) wiFiAPConfig;

        // Active one goes first.
        if (mInfo != null && other.mInfo == null) return -1;
        if (mInfo == null && other.mInfo != null) return 1;

        // Reachable one goes before unreachable one.
        if (mRssi != Integer.MAX_VALUE && other.mRssi == Integer.MAX_VALUE)
        {
            return -1;
        }

        if (mRssi == Integer.MAX_VALUE && other.mRssi != Integer.MAX_VALUE)
        {
            return 1;
        }

        // Configured one goes before unconfigured one.
        if (networkId != INVALID_NETWORK_ID
                && other.networkId == INVALID_NETWORK_ID)
        {
            return -1;
        }

        if (networkId == INVALID_NETWORK_ID
                && other.networkId != INVALID_NETWORK_ID)
        {
            return 1;
        }

        // Sort by signal strength.
        int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
        if (difference != 0)
        {
            return difference;
        }

        // Sort by ssid.
        return ssid.compareToIgnoreCase(other.ssid);
    }

//    @Override
//    public int compareTo(WiFiAPConfig another)
//    {
//        int result = 0;
//
//        if (this.isCurrentNetwork())
//        {
//            if (another.isCurrentNetwork())
//            {
//                result = 0;
//            }
//            else
//            {
//                result = -1;
//            }
//        }
//        else
//        {
//            if (another.isCurrentNetwork())
//            {
//                result = +1;
//            }
//            else
//            {
//                result = 0;
//            }
//        }
//
//        if (result == 0)
//        {
//            if (ap != null)
//            {
//                if (another.ap != null)
//                {
//                    result = ap.compareTo(another.ap);
//                }
//                else
//                {
//                    result = -1;
//                }
//            }
//            else
//            {
//                if (another.ap != null)
//                {
//                    result = +1;
//                }
//                else
//                {
//                    result = 0;
//                }
//            }
//        }
//
//        return result;
//    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ID: %s\n", id.toString()));
        sb.append(String.format("Wi-Fi Configuration Info: %s\n", ssid));
        sb.append(String.format("Proxy setting: %s\n", getProxySettings().toString()));
        sb.append(String.format("Proxy: %s\n", toStatusString()));
        sb.append(String.format("Is current network: %B\n", isCurrentNetwork()));
        sb.append(String.format("Proxy status checker results: %s\n", status.toString()));

        if (APL.getConnectivityManager().getActiveNetworkInfo() != null)
        {
            sb.append(String.format("Network Info: %s\n", APL.getConnectivityManager().getActiveNetworkInfo()));
        }

        return sb.toString();
    }

    public JSONObject toJSON()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("ID", id.toString());
            jsonObject.put("SSID", ssid);

            jsonObject.put("proxy_setting", getProxySettings().toString());
            jsonObject.put("proxy_status", toStatusString());
            jsonObject.put("is_current", isCurrentNetwork());
            jsonObject.put("status", status.toJSON());

            if (APL.getConnectivityManager().getActiveNetworkInfo() != null)
            {
                jsonObject.put("network_info", APL.getConnectivityManager().getActiveNetworkInfo());
            }
        }
        catch (JSONException e)
        {
            APL.getEventsReporter().sendException(e);
        }

        return jsonObject;
    }

    public String toShortString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(id.toString());

        sb.append(String.format("SSID: %s, RSSI: %d, LEVEL: %d, NETID: %d", ssid, mRssi, getLevel(), networkId));

        sb.append(" - " + toStatusString());
        sb.append(" " + getProxyExclusionList());

        if (status != null)
            sb.append(" - " + status.toShortString());

        return sb.toString();
    }

    public String toStatusString()
    {
        ProxySetting setting = getProxySettings();

        if (setting == null)
        {
            return APL.getContext().getResources().getString(R.string.not_available);
        }

        if (setting == ProxySetting.NONE || setting == ProxySetting.UNASSIGNED)
        {
            return APL.getContext().getResources().getString(R.string.direct_connection);
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(proxyHost) && proxyPort != null && proxyPort > 0)
                sb.append(String.format("%s:%s", proxyHost, proxyPort));
            else
            {
                sb.append(APL.getContext().getResources().getString(R.string.not_set));
            }

            return sb.toString();
        }
    }

    public Boolean isCurrentNetwork()
    {
        WifiInfo connectionInfo = APL.getWifiManager().getConnectionInfo();

        if (mInfo != null)
        {
            if (networkId == connectionInfo.getNetworkId())
            {
                return true;
            }
            else
            {
                APL.getLogger().d(TAG, "isCurrentNetwork: mInfo not null but different from active network");
                return false;
            }
        }
        else
            return false;
    }

    public Proxy.Type getProxyType()
    {
        return getProxy().type();
    }

    public String getProxyHostString()
    {
        return proxyHost;
    }

    public String getProxyIPHost()
    {
        return proxyHost;
    }

    public String getProxyHost()
    {
        return proxyHost;
    }

    public Integer getProxyPort()
    {
        return proxyPort;
    }

    public String getProxyExclusionList()
    {
        if (stringProxyExclusionList == null)
            return "";
        else
            return stringProxyExclusionList;
    }

    public CheckStatusValues getCheckingStatus()
    {
        return status.getCheckingStatus();
    }

    public void setAPDescription(String value)
    {
        apDescription = value;
    }

    public String getAPDescription()
    {
        if (!TextUtils.isEmpty(apDescription))
            return apDescription;
        else
        {
            return ProxyUtils.cleanUpSSID(getSSID());
        }
    }

    public String getSSID()
    {
        return ssid;
    }

//    public String getSecurityString()
//    {
//        if (ap != null)
//        {
//            return ap.getSecurity();
//        }
//    }

    public String getBSSID()
    {
        return bssid;
    }

    @Deprecated
    @TargetApi(12)
    public void writeConfigurationToDevice() throws Exception
    {
        APL.writeWifiAPConfig(this);
    }

    public String getAPConnectionStatus()
    {
        if (isCurrentNetwork())
        {
            return APL.getContext().getString(R.string.connected);
        }
        else if (getLevel() > 0)
        {
            return APL.getContext().getString(R.string.available);
        }
        else
        {
            return APL.getContext().getString(R.string.not_available);
        }
    }

    private static String removeDoubleQuotes(String string)
    {
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"'))
        {
            return string.substring(1, length - 1);
        }
        return string;
    }

    private static String convertToQuotedString(String string)
    {
        return "\"" + string + "\"";
    }

    public int getLevel()
    {
        if (mRssi == Integer.MAX_VALUE)
        {
            return -1;
        }
        return WifiManager.calculateSignalLevel(mRssi, 4);
    }

    public void clearScanStatus()
    {
        mRssi = Integer.MAX_VALUE;
        pskType = PskType.UNKNOWN;
    }
}
