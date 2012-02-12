package com.lechucksoftware.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.lechucksoftware.proxy.lib.reflection.ReflectionUtils;
import com.lechucksoftware.proxy.lib.reflection.android.RProxySettings;

/**
 * Main class that contains utilities for getting the proxy configuration of the 
 * current or the all configured networks
 * */
public class ProxySettings
{
    public static final String TAG = "ProxySettings";

    /**
     * Returns the current proxy configuration based on the current network 
     * */
    public static ProxyConfiguration getCurrentProxyConfiguration(Context ctx) throws Exception
    {
    	ProxyConfiguration proxy = null;
    	
    	if (Build.VERSION.SDK_INT >= 11)
        {
    		/**
        	 * Android 3.1 and newer versions has a better proxy handling. 
        	 * Starting from this version we can differentiate between proxy 
        	 * for Mobile and proxy for Wi-Fi networks.
        	 * */
    		proxy = getCurrentWifiProxyConfiguration(ctx);
        }
    	else
    	{
    		/**
    		 * Older Android versions doesn't handle properly the proxy 
    		 * settings. One configuration is used for both Mobile and Wi-Fi
    		 * networks. Must pay attention to this strange behaviour!
    		 * */
            ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
            
            switch(activeNetInfo.getType())
            {
                case ConnectivityManager.TYPE_WIFI:
                	proxy = getCurrentWifiProxyConfiguration(ctx);
                	break;
                case ConnectivityManager.TYPE_MOBILE:
                	proxy = getCurrentMobileProxyConfiguration(ctx, activeNetInfo);
                	break;
                default:
                	throw new Exception(activeNetInfo.getTypeName() + "(int: " + activeNetInfo.getType() + ") network type not supported!");
            }
    	}
        
        return proxy;
    }
    
    private static ProxyConfiguration getCurrentWifiProxyConfiguration(Context ctx)
    {
    	ProxyConfiguration proxy = null;
    	WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration wifiConf : configuredNetworks)
        {
            if (wifiConf.networkId == connectionInfo.getNetworkId())
            {
                proxy = getProxy(ctx, wifiConf);
                break;
            }
        }
        
        return proxy;
    }
    
    private static ProxyConfiguration getCurrentMobileProxyConfiguration(Context ctx, NetworkInfo activeNetInfo)
    {
    	ProxyConfiguration proxy = null;
		WifiConfiguration fakeWifi = new WifiConfiguration();
		ProxyConfiguration mobileProxy = getProxy(ctx, fakeWifi);
		mobileProxy.wifiConfiguration = null;
		if (mobileProxy.isValid)
		{
			proxy = ProxyConfiguration.GetMobileProxyConfiguration(mobileProxy.proxy, mobileProxy.exclusionList, activeNetInfo);
		}
    	
    	return proxy;
    }

    public static ProxyConfiguration getProxyConfiguration(Context ctx, WifiConfiguration wifiConf)
    {
        if (Build.VERSION.SDK_INT >= 11)
        {
            return getProxySdk11(ctx, wifiConf);
        } 
        else
        {
            return getProxy(ctx, wifiConf); // Same configuration for every AP
                                            // :(
        }
    }

    public static List<ProxyConfiguration> getProxiesConfigurations(Context ctx)
    {
        List<ProxyConfiguration> proxyHosts = new ArrayList<ProxyConfiguration>();
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        /* Just for testing on the Emulator */
        if (Build.PRODUCT.equals("sdk") && configuredNetworks.size() == 0)
        {
            WifiConfiguration fakeWifiConf = new WifiConfiguration();
            fakeWifiConf.SSID = "Fake_SDK_WI-FI";
            configuredNetworks.add(fakeWifiConf);
        }

        for (WifiConfiguration wifiConf : configuredNetworks)
        {
            proxyHosts.add(getProxyConfiguration(ctx, wifiConf));
        }

        return proxyHosts;
    }

    private static ProxyConfiguration getProxy(Context ctx, WifiConfiguration wifiConf)
    {
        ProxyConfiguration proxyHost = null;

        ContentResolver contentResolver = ctx.getContentResolver();
        String proxyString = Settings.Secure.getString(contentResolver, Settings.Secure.HTTP_PROXY);

        if (proxyString != null && proxyString != "" && proxyString.contains(":"))
        {
            String[] proxyParts = proxyString.split(":");
            if (proxyParts.length == 2)
            {
                String proxyAddress = proxyParts[0];
                try
                {
                    Integer proxyPort = Integer.parseInt(proxyParts[1]);
                    proxyHost = ProxyConfiguration.GetWifiProxyConfiguration(new HttpHost(proxyAddress, proxyPort), "", wifiConf);
                    Log.d(TAG, "ProxyHost created: " + proxyHost.toString());
                } 
                catch (NumberFormatException e)
                {
                    Log.d(TAG, "Port is not a number: " + proxyParts[1]);
                }
            }
        }

        if (proxyHost == null)
        	return ProxyConfiguration.GetWifiProxyConfiguration(null, null, wifiConf);
        else
        	return proxyHost;
    }

    public static ProxyConfiguration getProxySdk11(Context ctx, WifiConfiguration wifiConf)
    {
        ProxyConfiguration proxyHost = null;

        try
        {
            Field proxySettingsField = wifiConf.getClass().getField("proxySettings");
            Object proxySettings = proxySettingsField.get(wifiConf);

            int ordinal = ((Enum) proxySettings).ordinal();

            if (ordinal == RProxySettings.NONE.ordinal() || ordinal == RProxySettings.UNASSIGNED.ordinal())
            {
                proxyHost = ProxyConfiguration.GetWifiProxyConfiguration(null, null, wifiConf);
            } 
            else
            {

                Field linkPropertiesField = wifiConf.getClass().getField("linkProperties");
                Object linkProperties = linkPropertiesField.get(wifiConf);
                Field mHttpProxyField = ReflectionUtils.getField(linkProperties.getClass().getDeclaredFields(), "mHttpProxy");
                mHttpProxyField.setAccessible(true);
                Object mHttpProxy = mHttpProxyField.get(linkProperties);

                /* Just for testing on the Emulator */
                if (Build.PRODUCT.equals("sdk") && mHttpProxy == null)
                {
                    Class ProxyPropertiesClass = mHttpProxyField.getType();
                    Constructor constr = ProxyPropertiesClass.getConstructors()[1];
                    Object ProxyProperties = constr.newInstance("10.11.12.13", 1983, "");
                    mHttpProxyField.set(linkProperties, ProxyProperties);
                    mHttpProxy = mHttpProxyField.get(linkProperties);
                }

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

                    Log.d(TAG, "Proxy configuration: " + mHost + ":" + mPort + " , Exclusion List: " + mExclusionList);

                    proxyHost = ProxyConfiguration.GetWifiProxyConfiguration(new HttpHost(mHost, mPort), mExclusionList, wifiConf);
                }
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return proxyHost;
    }

}
