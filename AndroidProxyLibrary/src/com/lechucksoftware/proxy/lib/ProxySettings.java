package com.lechucksoftware.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
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
     * Returns the current proxy configuration based on the URI, this implementation
     * is a wrapper of the Android's ProxySelector class. Just add some other
     * informations that can be useful to the developer.
     * */
    public static ProxyConfiguration getCurrentProxyConfiguration(Context ctx, URI uri) throws Exception
    {
    	ProxySelector defaultProxySelector = ProxySelector.getDefault();

    	Proxy proxy = null;
    	
    	List<Proxy> proxyList = defaultProxySelector.select(uri);
    	if(proxyList.size() > 0)
    	{
    		proxy = proxyList.get(0);
    		Log.d(TAG,"Current Proxy Configuration: " + proxy.toString());
    	}
    	else
    		throw new Exception("Not found valid proxy configuration!");
    	

        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();  
        ProxyConfiguration proxyConfig = new ProxyConfiguration(proxy, "", activeNetInfo, null);
    			        
        return proxyConfig;
    }
    
    /**
     * Return the current proxy configuration for HTTP protocol
     * */
    public static ProxyConfiguration getCurrentHttpProxyConfiguration(Context ctx) throws Exception
    {
    	URI uri = new URI("http","wwww.google.it",null,null);
    	return getCurrentProxyConfiguration(ctx, uri);
    }
    
    /**
     * Return the current proxy configuration for HTTPS protocol
     * */
    public static ProxyConfiguration getCurrentHttpsProxyConfiguration(Context ctx) throws Exception
    {
    	URI uri = new URI("https","wwww.google.it",null,null);
    	return getCurrentProxyConfiguration(ctx, uri);
    }
     

    /**
     * Set the current proxy configuration for a specific URI scheme
     * */
    public static void setCurrentProxyConfiguration(ProxyConfiguration proxyConf, String scheme)
    {
    	String exclusionList = null;
    	String host = null;
    	String port = null;
    	
    	if (proxyConf != null)
    	{
            exclusionList = proxyConf.exclusionList;
            host = ((InetSocketAddress) proxyConf.proxyHost.address()).getHostName();
            port = String.valueOf(((InetSocketAddress) proxyConf.proxyHost.address()).getPort());
            Log.d(TAG, "setHttpProxySystemProperty : " + host + ":" + port + " - "+ exclusionList);
    	}
    	
        if (exclusionList != null) exclusionList = exclusionList.replace(",", "|");
        
        
        
        if (host != null) 
        {
            System.setProperty(scheme+".proxyHost", host);
        } 
        else
        {
        	System.clearProperty(scheme+".proxyHost");
        }
        
        if (port != null) 
        {
            System.setProperty(scheme+".proxyPort", port);
        }
        else
        {
        	System.clearProperty(scheme+".proxyPort");
        }
        
        if (exclusionList != null) 
        {
            System.setProperty(scheme+".nonProxyHosts", exclusionList);
        }
        else
        {
        	System.clearProperty(scheme+".nonProxyHosts");
        }
    }
    
    public static void setCurrentProxyHttpConfiguration(ProxyConfiguration proxyConf)
    {
    	setCurrentProxyConfiguration(proxyConf, "http");
    }
    
    public static void setCurrentProxyHttpsConfiguration(ProxyConfiguration proxyConf)
    {
    	setCurrentProxyConfiguration(proxyConf, "https");
    }
    
    public static ProxyConfiguration getProxyConfiguration(Context ctx, WifiConfiguration wifiConf)
    {
        if (Build.VERSION.SDK_INT >= 11)
        {
            return getProxySdk11(ctx, wifiConf);
        } 
        else
        {
        	// Same configuration for every AP
	        // :(
            throw new UnsupportedOperationException("Proxy not defined for each AP"); 
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
                proxyHost = new ProxyConfiguration(null, null, null, wifiConf);
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

                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new Socket(mHost, mPort).getRemoteSocketAddress());
                    proxyHost = new ProxyConfiguration(proxy, mExclusionList, null, wifiConf);
                }
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return proxyHost;
    }

//    private static ProxyConfiguration getGlobalProxy(Context ctx, WifiConfiguration wifiConf)
//    {
//        ProxyConfiguration proxyHost = null;
//
//        ContentResolver contentResolver = ctx.getContentResolver();
//        String proxyString = Settings.Secure.getString(contentResolver, Settings.Secure.HTTP_PROXY);
//
//        if (proxyString != null && proxyString != "" && proxyString.contains(":"))
//        {
//            String[] proxyParts = proxyString.split(":");
//            if (proxyParts.length == 2)
//            {
//                String proxyAddress = proxyParts[0];
//                try
//                {
//                    Integer proxyPort = Integer.parseInt(proxyParts[1]);
//                    proxyHost = ProxyConfiguration.GetWifiProxyConfiguration(new HttpHost(proxyAddress, proxyPort), "", wifiConf);
//                    Log.d(TAG, "ProxyHost created: " + proxyHost.toString());
//                } 
//                catch (NumberFormatException e)
//                {
//                    Log.d(TAG, "Port is not a number: " + proxyParts[1]);
//                }
//            }
//        }
//
//        if (proxyHost == null)
//        	return ProxyConfiguration.GetWifiProxyConfiguration(null, null, wifiConf);
//        else
//        	return proxyHost;
//    }
}
