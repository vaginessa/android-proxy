package com.shouldit.proxy.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.webkit.URLUtil;
import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;
import com.shouldit.proxy.lib.reflection.ReflectionUtils;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;
import org.apache.http.conn.util.InetAddressUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyConfiguration implements Comparable<ProxyConfiguration>
{
    public static final String TAG = "ProxyConfiguration";

    public Context context;
    public ProxyStatus status;
    public AccessPoint ap;
    public NetworkInfo currentNetworkInfo;

    public ProxySetting proxySetting;
    private String proxyHost;
    private Integer proxyPort;
    private String stringProxyExclusionList;
    private String[] parsedProxyExclusionList;

    public int deviceVersion;
    private ConnectivityManager connManager;

    private WifiManager wifiManager;

    public Proxy getProxy()
    {
        if (proxySetting == ProxySetting.STATIC && proxyHost != null && proxyPort != null)
            return new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyHost, proxyPort));
        else
            return Proxy.NO_PROXY;
    }

    public void setProxyHost(String host)
    {
        proxyHost = host;
    }

    public void setProxyPort(int port)
    {
        proxyPort = port;
    }

    public void setProxyExclusionList(String exList)
    {
        parseExclusionList(exList);
    }

    private void parseExclusionList(String exclusionList)
    {
        stringProxyExclusionList = exclusionList;
        if (stringProxyExclusionList == null)
        {
            parsedProxyExclusionList = new String[0];
        }
        else
        {
            String splitExclusionList[] = exclusionList.toLowerCase().split(",");
            parsedProxyExclusionList = new String[splitExclusionList.length * 2];
            for (int i = 0; i < splitExclusionList.length; i++)
            {
                String s = splitExclusionList[i].trim();
                if (s.startsWith("."))
                    s = s.substring(1);
                parsedProxyExclusionList[i * 2] = s;
                parsedProxyExclusionList[(i * 2) + 1] = "." + s;
            }
        }
    }

    public ProxyConfiguration(Context ctx, ProxySetting proxyEnabled, String host, Integer port, String exclusionList, WifiConfiguration wifiConf)
    {
        context = ctx;

        proxySetting = proxyEnabled;
        proxyHost = host;
        proxyPort = port;
        setProxyExclusionList(exclusionList);

        connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        currentNetworkInfo = connManager.getActiveNetworkInfo();

        if (wifiConf != null)
            ap = new AccessPoint(wifiConf);

        deviceVersion = Build.VERSION.SDK_INT;
        status = new ProxyStatus();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Proxy setting: %s\n", proxySetting.toString()));
        sb.append(String.format("Proxy: %s\n", toShortString()));
        sb.append(String.format("Is current network: %B\n", isCurrentNetwork()));
        sb.append(String.format("Proxy status checker results:\n"));
        sb.append(status.toString());

        if (currentNetworkInfo != null)
        {
            sb.append(String.format("Network Info: %s\n", currentNetworkInfo));
        }

        if (ap != null && ap.wifiConfig != null)
            sb.append(String.format("Wi-Fi Configuration Info: %s\n", ap.wifiConfig.SSID.toString()));

        return sb.toString();
    }

    public Boolean isCurrentNetwork()
    {
        if (currentNetworkInfo != null && this.getSSID() != null && this.getSSID().equals(currentNetworkInfo.getExtraInfo()))
            return true;
        else
            return false;
    }

    public String toShortString()
    {
        if (proxySetting == ProxySetting.NONE || proxySetting == ProxySetting.UNASSIGNED)
        {
            return context.getResources().getString(R.string.direct_connection);
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null && proxyPort > 0)
                sb.append(String.format("%s:%s", proxyHost, proxyPort));
            else
            {
                sb.append(context.getResources().getString(R.string.not_set));
            }

            return sb.toString();
        }
    }

    public Proxy.Type getProxyType()
    {
        return getProxy().type();
    }

    /**
     * Can take a long time to execute this task. - Check if the proxy is
     * enabled - Check if the proxy address is valid - Check if the proxy is
     * reachable (using a PING) - Check if is possible to retrieve an URI
     * resource using the proxy
     */
    public void acquireProxyStatus(int timeout)
    {
        status.clear();
        status.startchecking();
        broadCastUpdatedStatus();

        if (Build.VERSION.SDK_INT >= 12)
        {
            acquireProxyStatusSDK12();
        }
        else
        {
            acquireProxyStatusSDK1_11();
        }

        // Always check if WEB is reachable
        LogWrapper.d(TAG, "Checking if web is reachable ...");
        status.set(isWebReachable(timeout));
        broadCastUpdatedStatus();
    }

    private void acquireProxyStatusSDK1_11()
    {
        // API version <= 11 (Older devices)
        LogWrapper.d(TAG, "Checking if proxy is enabled ...");
        status.set(isProxyEnabled());
        broadCastUpdatedStatus();

        if (status.getProperty(ProxyStatusProperties.PROXY_ENABLED).result)
        {
            LogWrapper.d(TAG, "Checking if proxy is valid hostname ...");
            status.set(isProxyValidHostname());
            broadCastUpdatedStatus();

            LogWrapper.d(TAG, "Checking if proxy is valid port ...");
            status.set(isProxyValidPort());
            broadCastUpdatedStatus();

            if (status.getProperty(ProxyStatusProperties.PROXY_VALID_HOSTNAME).result
                    && status.getProperty(ProxyStatusProperties.PROXY_VALID_PORT).result)
            {
                LogWrapper.d(TAG, "Checking if proxy is reachable ...");
                status.set(isProxyReachable());
                broadCastUpdatedStatus();
            }
            else
            {
                status.set(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.NOT_CHECKED, false, false);
            }
        }
        else
        {
            wifiNotEnabled_DisableChecking();
        }
    }

    private void acquireProxyStatusSDK12()
    {
        LogWrapper.d(TAG, "Checking if Wi-Fi is enabled ...");
        status.set(isWifiEnabled());
        broadCastUpdatedStatus();

        if (status.getProperty(ProxyStatusProperties.WIFI_ENABLED).result)
        {
            LogWrapper.d(TAG, "Checking if Wi-Fi is selected ...");
            status.set(isWifiSelected());
            broadCastUpdatedStatus();

            if (status.getProperty(ProxyStatusProperties.WIFI_SELECTED).result)
            {
                // Wi-Fi enabled & selected
                LogWrapper.d(TAG, "Checking if proxy is enabled ...");
                status.set(isProxyEnabled());
                broadCastUpdatedStatus();

                if (status.getProperty(ProxyStatusProperties.PROXY_ENABLED).result)
                {
                    LogWrapper.d(TAG, "Checking if proxy is valid hostname ...");
                    status.set(isProxyValidHostname());
                    broadCastUpdatedStatus();

                    LogWrapper.d(TAG, "Checking if proxy is valid port ...");
                    status.set(isProxyValidPort());
                    broadCastUpdatedStatus();

                    if (status.getProperty(ProxyStatusProperties.PROXY_VALID_HOSTNAME).result
                            && status.getProperty(ProxyStatusProperties.PROXY_VALID_PORT).result)
                    {
                        LogWrapper.d(TAG, "Checking if proxy is reachable ...");
                        status.set(isProxyReachable());
                        broadCastUpdatedStatus();
                    }
                    else
                    {
                        status.set(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.NOT_CHECKED, false, false);
                    }
                }
                else
                {
                    wifiNotEnabled_DisableChecking();
                }
            }
            else
            {
                wifiNotEnabled_DisableChecking();
            }
        }
        else
        {
            status.set(ProxyStatusProperties.WIFI_SELECTED, CheckStatusValues.NOT_CHECKED, false, false);
            wifiNotEnabled_DisableChecking();
        }
    }

    private void wifiNotEnabled_DisableChecking()
    {
        status.set(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.NOT_CHECKED, false, false);
        status.set(ProxyStatusProperties.PROXY_VALID_HOSTNAME, CheckStatusValues.NOT_CHECKED, false, false);
        status.set(ProxyStatusProperties.PROXY_VALID_PORT, CheckStatusValues.NOT_CHECKED, false, false);
        status.set(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.NOT_CHECKED, false, false);
    }


    private void broadCastUpdatedStatus()
    {
        LogWrapper.d(TAG, "Sending broadcast intent: " + APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
        Intent intent = new Intent(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
        // intent.putExtra(APLConstants.ProxyStatus, status);
        context.sendBroadcast(intent);
    }

    private ProxyStatusItem isWifiEnabled()
    {
        ProxyStatusItem result = null;

        if (wifiManager.isWifiEnabled())
        {
            NetworkInfo ni = connManager.getActiveNetworkInfo();
            if (ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI)
            {
                String status = String.format("%s %s", context.getString(R.string.status_wifi_connected), ProxyUtils.cleanUpSSID(getSSID()));
                result = new ProxyStatusItem(ProxyStatusProperties.WIFI_ENABLED, CheckStatusValues.CHECKED, true, true, status);
            }
            else
            {
                result = new ProxyStatusItem(ProxyStatusProperties.WIFI_ENABLED, CheckStatusValues.CHECKED, false, true, context.getString(R.string.status_wifi_enabled_disconnected));
            }
        }
        else
        {
            result = new ProxyStatusItem(ProxyStatusProperties.WIFI_ENABLED, CheckStatusValues.CHECKED, false, true, context.getString(R.string.status_wifi_not_enabled));
        }

        return result;
    }

    private ProxyStatusItem isWifiSelected()
    {
        ProxyStatusItem result = null;

        if (isCurrentNetwork())
        {
            result = new ProxyStatusItem(ProxyStatusProperties.WIFI_SELECTED, CheckStatusValues.CHECKED, true, true, context.getString(R.string.status_wifi_selected,this.ap.ssid));
        }
        else
        {
            result = new ProxyStatusItem(ProxyStatusProperties.WIFI_SELECTED, CheckStatusValues.CHECKED, false, true, context.getString(R.string.status_wifi_not_selected));
        }

        return result;
    }


    private ProxyStatusItem isProxyEnabled()
    {
        ProxyStatusItem result;

        if (Build.VERSION.SDK_INT >= 12)
        {
            // On API version > Honeycomb 3.1 (HONEYCOMB_MR1)
            // Proxy is disabled by default on Mobile connection
            if (currentNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                result = new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.CHECKED, false, context.getString(R.string.status_proxy_mobile_disabled));
            }
        }

        if (proxySetting == ProxySetting.UNASSIGNED || proxySetting == ProxySetting.NONE)
        {
            result = new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.CHECKED, false, context.getString(R.string.status_proxy_disabled));
        }
        else
        {
            // if (proxyHost != null && proxyPort != null)
            // {
            // HTTP or SOCKS proxy
            result = new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED, CheckStatusValues.CHECKED, true, context.getString(R.string.status_proxy_enabled));
            // }
            // else
            // {
            // result = new ProxyStatusItem(ProxyStatusProperties.PROXY_ENABLED,
            // CheckStatusValues.CHECKED, false);
            // }
        }

        return result;
    }

    private ProxyStatusItem isProxyValidHostname()
    {
        try
        {
            String proxyHost = getProxyHostString();

            if (proxyHost == null || proxyHost.length() == 0)
            {
                return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_HOSTNAME, CheckStatusValues.CHECKED, false, context.getString(R.string.status_hostname_empty));
            }
            else
            {
                // Test REGEX for Hostname validation
                // http://stackoverflow.com/questions/106179/regular-expression-to-match-hostname-or-ip-address
                //
                String ValidHostnameRegex = "^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$";
                Pattern pattern = Pattern.compile(ValidHostnameRegex);
                Matcher matcher = pattern.matcher(proxyHost);

                if (InetAddressUtils.isIPv4Address(proxyHost)
                        || InetAddressUtils.isIPv6Address(proxyHost)
                        || InetAddressUtils.isIPv6HexCompressedAddress(proxyHost)
                        || InetAddressUtils.isIPv6StdAddress(proxyHost)
                        || URLUtil.isNetworkUrl(proxyHost)
                        || URLUtil.isValidUrl(proxyHost)
                        || matcher.find())
                {
                    String msg = String.format("%s %s", context.getString(R.string.status_hostname_valid), proxyHost);
                    return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_HOSTNAME, CheckStatusValues.CHECKED, true, msg);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_HOSTNAME, CheckStatusValues.CHECKED, false, context.getString(R.string.status_hostname_notvalid));
    }

    private ProxyStatusItem isProxyValidPort()
    {
        if ((proxyPort != null) && (proxyPort > 0))
        {
            String msg = String.format("%s %d", context.getString(R.string.status_port_valid), proxyPort);
            return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_PORT, CheckStatusValues.CHECKED, true, msg);
        }
        else
        {
            return new ProxyStatusItem(ProxyStatusProperties.PROXY_VALID_PORT, CheckStatusValues.CHECKED, false, context.getString(R.string.status_port_empty));
        }
    }

    /**
     * Try to PING the HOST specified in the current proxy configuration
     */
    private ProxyStatusItem isProxyReachable()
    {
        if (getProxy() != null && getProxyType() != Proxy.Type.DIRECT)
        {
            Boolean result = ProxyUtils.isHostReachable(getProxy());
            if (result)
            {
                return new ProxyStatusItem(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.CHECKED, true, context.getString(R.string.status_proxy_reachable));
            }
            else
            {
                return new ProxyStatusItem(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.CHECKED, false, context.getString(R.string.status_proxy_not_reachable));
            }
        }
        else
        {
            return new ProxyStatusItem(ProxyStatusProperties.PROXY_REACHABLE, CheckStatusValues.CHECKED, false, context.getString(R.string.status_proxy_not_valid_informations));
        }
    }

    /**
     * Try to download a webpage using the current proxy configuration
     */
    public static int DEFAULT_TIMEOUT = 60000; // 60 seconds

    private ProxyStatusItem isWebReachable()
    {
        return isWebReachable(DEFAULT_TIMEOUT);
    }

    private ProxyStatusItem isWebReachable(int timeout)
    {
        Boolean result = ProxyUtils.isWebReachable(this, timeout);
        if (result)
        {
            return new ProxyStatusItem(ProxyStatusProperties.WEB_REACHABLE, CheckStatusValues.CHECKED, true, context.getString(R.string.status_web_reachable));
        }
        else
        {
            return new ProxyStatusItem(ProxyStatusProperties.WEB_REACHABLE, CheckStatusValues.CHECKED, false, context.getString(R.string.status_web_not_reachable));
        }
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
        return stringProxyExclusionList;
    }

    public CheckStatusValues getCheckingStatus()
    {
        return status.getCheckingStatus();
    }

    public int getNetworkType()
    {
        return currentNetworkInfo.getType();
    }

    @Override
    public int compareTo(ProxyConfiguration another)
    {
        int result = 0;

        if (currentNetworkInfo != null && another.currentNetworkInfo != null)
        {
            if (currentNetworkInfo.getType() == another.currentNetworkInfo.getType())
            {
                if (ap != null && another.ap != null)
                {
                    result = ap.compareTo(another.ap);
                }

                if (result == 0)
                {
                    if (proxySetting == ProxySetting.NONE || proxySetting == ProxySetting.UNASSIGNED)
                    {
                        if (another.proxySetting == ProxySetting.NONE || another.proxySetting == ProxySetting.UNASSIGNED)
                        {
                            // Both DIRECT connection
                            result = 0;
                        }
                        else
                        {
                            result = -1;
                        }
                    }
                    else
                    {
                        if (getProxy() != another.getProxy())
                        {
                            String proxystring = getProxy().toString();
                            String anotherstring = another.getProxy().toString();
                            result = proxystring.compareTo(anotherstring);
                        }
                        else
                            result = 0;
                    }

                }

                // // Same network types
                // if (currentNetworkInfo.getType() ==
                // ConnectivityManager.TYPE_WIFI)
                // {
                // // TYPE_WIFI
                //
                // result = ap.compareTo(another.ap);
                // if (result == 0)
                // {
                // if (getProxy() != another.getProxy())
                // {
                // result =
                // getProxy().toString().compareTo(another.getProxy().toString());
                // }
                // }
                // }
                // else
                // {
                // // TYPE_MOBILE or No connection
                // result = 0;
                // }
            }
            else
            {
                // Different network types
                if (currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                {
                    // Give priority to TYPE_WIFI
                    result = -1;
                }
                else
                {
                    result = +1;
                }
            }
        }

        return result;
    }

    public String getAPDescription(Context ctx)
    {
//		StringBuilder sb = new StringBuilder();
//		sb.append(ap.ssid);
//		sb.append(" - ");
//		sb.append(ap.getSecurityString(ctx, false));
//		sb.append(" - ");
//		sb.append(toShortString());
//
//		return sb.toString();

        return String.format("%s (%s)", ap.ssid, ap.getSecurityString(ctx, false));
    }

    public String getSSID()
    {
        if (ap != null && ap.wifiConfig != null && ap.wifiConfig.SSID != null)
        {
            return ap.wifiConfig.SSID;
        }
        else
            return null;
    }

    public boolean isValidConfiguration()
    {
        if (ap != null)
            return true;
        else
            return false;
    }

    @Deprecated
    @TargetApi(12)
    public void writeConfigurationToDevice()
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try
        {
            Field proxySettingsField = ap.wifiConfig.getClass().getField("proxySettings");
            proxySettingsField.set(ap.wifiConfig, (Object) proxySettingsField.getType().getEnumConstants()[proxySetting.ordinal()]);
            Object proxySettings = proxySettingsField.get(ap.wifiConfig);
            int ordinal = ((Enum) proxySettings).ordinal();
            if (ordinal != proxySetting.ordinal())
                throw new Exception("Cannot set proxySettings variable");

            Field linkPropertiesField = ap.wifiConfig.getClass().getField("linkProperties");
            Object linkProperties = linkPropertiesField.get(ap.wifiConfig);
            Field mHttpProxyField = ReflectionUtils.getField(linkProperties.getClass().getDeclaredFields(), "mHttpProxy");
            mHttpProxyField.setAccessible(true);

            if (proxySetting == ProxySetting.NONE || proxySetting == ProxySetting.UNASSIGNED)
            {
                mHttpProxyField.set(linkProperties, null);
            }
            else if (proxySetting == ProxySetting.STATIC)
            {
                Class ProxyPropertiesClass = mHttpProxyField.getType();
                Integer port = getProxyPort();

                if (port == null)
                {
                    Constructor constr = ProxyPropertiesClass.getConstructors()[0];
                    Object ProxyProperties = constr.newInstance((Object) null);
                    mHttpProxyField.set(linkProperties, ProxyProperties);
                }
                else
                {
                    Constructor constr = ProxyPropertiesClass.getConstructors()[1];
                    Object ProxyProperties = constr.newInstance(getProxyHostString(), port, getProxyExclusionList());
                    mHttpProxyField.set(linkProperties, ProxyProperties);
                }
            }

            Object mHttpProxy = mHttpProxyField.get(linkProperties);
            mHttpProxy = mHttpProxyField.get(linkProperties);

            int result = wifiManager.updateNetwork(ap.wifiConfig);
            if (result == -1)
                throw new Exception("Can't update network configuration");

            LogWrapper.d(TAG, "Sending broadcast intent: " + APLConstants.APL_UPDATED_PROXY_CONFIGURATION);
            Intent intent = new Intent(APLConstants.APL_UPDATED_PROXY_CONFIGURATION);
            context.sendBroadcast(intent);
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public String getAPStatus()
    {
        if (isCurrentNetwork())
        {
            return context.getString(R.string.connected);
        }
        else if (ap.getLevel() > 0)
        {
            return context.getString(R.string.available);
        }
        else
        {
            return context.getString(R.string.not_available);
        }
    }

}
