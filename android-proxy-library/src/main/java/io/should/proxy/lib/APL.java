package io.should.proxy.lib;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import io.should.proxy.lib.log.DefaultEventReport;
import io.should.proxy.lib.log.IEventReporting;
import io.should.proxy.lib.log.LogWrapper;
import io.should.proxy.lib.reflection.ReflectionUtils;
import io.should.proxy.lib.reflection.android.ProxySetting;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class that contains utilities for getting the proxy configuration of the
 * current or the all configured networks
 */
public class APL
{
    public static final String TAG = "APL";

    private static ConnectivityManager mConnManager;
    private static WifiManager mWifiManager;
    private static Context gContext;
    private static boolean sSetupCalled;
    private static int deviceVersion;
    private static IEventReporting eventReport;

    public static boolean setup(Context context)
    {
        return setup(context, null);
    }

    public static boolean setup(Context context, IEventReporting eRep)
    {
        gContext = context;
        deviceVersion = Build.VERSION.SDK_INT;

        // Make sure this is only called once.
        if (sSetupCalled)
        {
            return false;
        }

        sSetupCalled = true;
        LogWrapper.d(TAG,"APL setup executed");

        if (eRep != null)
        {
            eventReport = eRep;
        }
        else
        {
            eventReport = new DefaultEventReport();
        }

        return sSetupCalled;
    }

    public static IEventReporting getEventReport()
    {
        return eventReport;
    }

    public static Context getContext()
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        return gContext;
    }

    public static int getDeviceVersion()
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        return deviceVersion;
    }

    public static WifiManager getWifiManager()
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        if (mWifiManager == null)
        {
            mWifiManager = (WifiManager) gContext.getSystemService(Context.WIFI_SERVICE);
        }

        return mWifiManager;
    }

    public static void enableWifi() throws Exception
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        WifiManager wm = getWifiManager();
        wm.setWifiEnabled(true);
    }

    public static ConnectivityManager getConnectivityManager()
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        if (mConnManager == null)
        {
            mConnManager = (ConnectivityManager) gContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        return mConnManager;
    }

    /**
     * Main entry point to access the proxy settings
     */
    public static ProxyConfiguration getCurrentProxyConfiguration(URI uri) throws Exception
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        ProxyConfiguration proxyConfig;

        if (deviceVersion >= 12) // Honeycomb 3.1
        {
            proxyConfig = getProxySelectorConfiguration(uri);
        }
        else
        {
            proxyConfig = getGlobalProxy();
        }

        /**
         * Set direct connection if no proxyConfig received
         * */
        if (proxyConfig == null)
        {
            proxyConfig = new ProxyConfiguration(ProxySetting.NONE, null, null, null, null);
        }

        /**
         * Add connection details
         * */
        ConnectivityManager connManager = (ConnectivityManager) gContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
//		proxyConfig.currentNetworkInfo = activeNetInfo;

        if (activeNetInfo != null)
        {
            switch (activeNetInfo.getType())
            {
                case ConnectivityManager.TYPE_WIFI:
                    WifiManager wifiManager = (WifiManager) gContext.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
                    for (WifiConfiguration wc : wifiConfigurations)
                    {
                        if (wc.networkId == wifiInfo.getNetworkId())
                        {
                            proxyConfig.ap = new AccessPoint(wc);
                            break;
                        }
                    }
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    break;
                default:
                    throw new UnsupportedOperationException("Not yet implemented support for" + activeNetInfo.getTypeName() + " network type");
            }
        }

        return proxyConfig;
    }

    /**
     * For API >= 12: Returns the current proxy configuration based on the URI,
     * this implementation is a wrapper of the Android's ProxySelector class.
     * Just add some other details that can be useful to the developer.
     */
    public static ProxyConfiguration getProxySelectorConfiguration(URI uri) throws Exception
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        ProxySelector defaultProxySelector = ProxySelector.getDefault();

        Proxy proxy = null;

        List<Proxy> proxyList = defaultProxySelector.select(uri);
        if (proxyList.size() > 0)
        {
            proxy = proxyList.get(0);
            LogWrapper.d(TAG, "Current Proxy Configuration: " + proxy.toString());
        }
        else
            throw new Exception("Not found valid proxy configuration!");

        ConnectivityManager connManager = (ConnectivityManager) gContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        ProxyConfiguration proxyConfig = null;
        if (proxy != Proxy.NO_PROXY)
        {
            proxyConfig = new ProxyConfiguration(ProxySetting.STATIC, null, null, null, null);
        }
        else
        {
            InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();
            proxyConfig = new ProxyConfiguration(ProxySetting.NONE, proxyAddress.getHostName(), proxyAddress.getPort(), null, null);
        }

        return proxyConfig;
    }

    /**
     * Return the current proxy configuration for HTTP protocol
     */
    public static ProxyConfiguration getCurrentHttpProxyConfiguration() throws Exception
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        URI uri = new URI("http", "wwww.google.it", null, null);
        return getCurrentProxyConfiguration(uri);
    }

    /**
     * Return the current proxy configuration for HTTPS protocol
     */
    public static ProxyConfiguration getCurrentHttpsProxyConfiguration() throws Exception
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        URI uri = new URI("https", "wwww.google.it", null, null);
        return getCurrentProxyConfiguration(uri);
    }

    /**
     * Return the current proxy configuration for FTP protocol
     */
    public static ProxyConfiguration getCurrentFtpProxyConfiguration() throws Exception
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        URI uri = new URI("ftp", "google.it", null, null);
        return getCurrentProxyConfiguration(uri);
    }

    /**
     * For API < 12: Get global proxy configuration.
     */
    @Deprecated
    public static ProxyConfiguration getGlobalProxy()
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        ProxyConfiguration proxyConfig = null;

        ContentResolver contentResolver = gContext.getContentResolver();
        String proxyString = Settings.Secure.getString(contentResolver, Settings.Secure.HTTP_PROXY);

        if (!TextUtils.isEmpty(proxyString) && proxyString.contains(":"))
        {
            String[] proxyParts = proxyString.split(":");
            if (proxyParts.length == 2)
            {
                String proxyAddress = proxyParts[0];
                try
                {
                    int proxyPort = Integer.parseInt(proxyParts[1]);
                    proxyConfig = new ProxyConfiguration(ProxySetting.STATIC, proxyAddress, proxyPort, null, null);
                }
                catch (NumberFormatException e)
                {
                    APL.getEventReport().send(new Exception("Port is not a number: " + proxyParts[1],e));
                }
            }
        }

        return proxyConfig;
    }

    /**
     * Get proxy configuration for Wi-Fi access point. Valid for API >= 12
     */
    @Deprecated
    @TargetApi(12)
    public static ProxyConfiguration getProxySdk12(WifiConfiguration wifiConf)
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

        ProxyConfiguration proxyHost = null;

        ConnectivityManager connManager = (ConnectivityManager) gContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        try
        {
            Field proxySettingsField = wifiConf.getClass().getField("proxySettings");
            Object proxySettings = proxySettingsField.get(wifiConf);

            int ordinal = ((Enum) proxySettings).ordinal();

            if (ordinal == ProxySetting.NONE.ordinal() || ordinal == ProxySetting.UNASSIGNED.ordinal())
            {
                proxyHost = new ProxyConfiguration(ProxySetting.NONE, null, null, "", wifiConf);
            }
            else
            {
                Field linkPropertiesField = wifiConf.getClass().getField("linkProperties");
                Object linkProperties = linkPropertiesField.get(wifiConf);
                Field mHttpProxyField = ReflectionUtils.getField(linkProperties.getClass().getDeclaredFields(), "mHttpProxy");
                mHttpProxyField.setAccessible(true);
                Object mHttpProxy = mHttpProxyField.get(linkProperties);

                if (mHttpProxy != null)
                {
                    Field mHostField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mHost");
                    mHostField.setAccessible(true);
                    String mHost = (String) mHostField.get(mHttpProxy);

                    Field mPortField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mPort");
                    mPortField.setAccessible(true);
                    Integer mPort = (Integer) mPortField.get(mHttpProxy);

                    Field mExclusionListField = ReflectionUtils.getField(mHttpProxy.getClass().getDeclaredFields(), "mExclusionList");
                    mExclusionListField.setAccessible(true);
                    String mExclusionList = (String) mExclusionListField.get(mHttpProxy);

                    //LogWrapper.d(TAG, "Proxy configuration: " + mHost + ":" + mPort + " , Exclusion List: " + mExclusionList);

                    proxyHost = new ProxyConfiguration(ProxySetting.STATIC, mHost, mPort, mExclusionList, wifiConf);
                }
            }
        }
        catch (Exception e)
        {
            APL.getEventReport().send(e);
        }

        return proxyHost;
    }

    @Deprecated
    @TargetApi(12)
    public static List<ProxyConfiguration> getProxiesConfigurations()
    {
        if (!sSetupCalled && gContext == null)
            throw new RuntimeException("you need to call setup() first");

//        LogWrapper.startTrace(TAG,"getConfiguredNetworks", Log.DEBUG);
        List<ProxyConfiguration> proxyConfigurations = new ArrayList<ProxyConfiguration>();
        List<WifiConfiguration> configuredNetworks = getWifiManager().getConfiguredNetworks();
//        LogWrapper.stopTrace(TAG,"getConfiguredNetworks", Log.DEBUG);

//        LogWrapper.startTrace(TAG,"getProxySdk12", Log.DEBUG);
        if (configuredNetworks != null)
        {
            for (WifiConfiguration wifiConf : configuredNetworks)
            {
                ProxyConfiguration conf = getProxySdk12(wifiConf);
                proxyConfigurations.add(conf);
            }
        }
//        LogWrapper.stopTrace(TAG,"getProxySdk12", Log.DEBUG);


        // Commented out sorting, not useful here
//        LogWrapper.startTrace(TAG,"sortConfigurations", Log.DEBUG);
//        if (proxyConfigurations.size() > 0)
//            Collections.sort(proxyConfigurations);
//        LogWrapper.stopTrace(TAG,"sortConfigurations", Log.DEBUG);

        return proxyConfigurations;
    }
}
