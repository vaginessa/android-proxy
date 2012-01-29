package com.lechucksoftware.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.http.HttpHost;

import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class ProxySettings
{
	public static final String TAG = "ProxySettings";
	
	public static List<ProxyConfiguration> getProxiesConfigurations(Context ctx)
	{
		List<ProxyConfiguration> proxyHosts = new ArrayList<ProxyConfiguration>();
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
		ListIterator<WifiConfiguration> litr = configuredNetworks.listIterator();
		
		while(litr.hasNext()) 
		{
			proxyHosts.add(getProxyConfiguration(ctx,(WifiConfiguration)litr.next()));
		} 
		
		return proxyHosts;
	}
	
	public static ProxyConfiguration getProxyConfiguration(Context ctx, WifiConfiguration wifiConf)
	{
    	if (Build.VERSION.SDK_INT >= 11) 
    	{
    		return getProxySdk11(ctx, wifiConf);
		}
    	else
    	{
    		return getProxy(ctx, wifiConf); // Same configuration for every AP :(
    	}		
	}
	
	private static ProxyConfiguration getProxy(Context ctx, WifiConfiguration wifiConf)
	{
		ProxyConfiguration proxyHost = null;
		
		ContentResolver contentResolver = ctx.getContentResolver();
		String proxyString = Settings.Secure.getString(contentResolver,Settings.Secure.HTTP_PROXY);
		
		if (proxyString != null && proxyString != "" && proxyString.contains(":"))
		{
			String [] proxyParts = proxyString.split(":");
			if (proxyParts.length == 2)
			{
    			String proxyAddress = proxyParts[0];
    			try
    			{
    				Integer proxyPort = Integer.parseInt(proxyParts[1]);
    				proxyHost = new ProxyConfiguration(new HttpHost(proxyAddress, proxyPort), "" , wifiConf);
    				Log.d(TAG, "ProxyHost created: " + proxyHost.toString());
    			}
    			catch (NumberFormatException e)
    			{
    				Log.d(TAG, "Port is not a number: " + proxyParts[1]);
    			}
			}
		}
		
		return proxyHost;
	}

	public static ProxyConfiguration getProxySdk11(Context ctx, WifiConfiguration wifiConf)
	{
		ProxyConfiguration proxyHost = null;
		      
        try
        {           
            Field linkPropertiesField = wifiConf.getClass().getField("linkProperties");
            Object linkProperties = linkPropertiesField.get(wifiConf);
            Field mHttpProxyField = linkProperties.getClass().getDeclaredFields()[2];
            mHttpProxyField.setAccessible(true);
            Object mHttpProxy = mHttpProxyField.get(linkProperties);
            
            /* Just for testing on the Emulator */
            if (Build.PRODUCT.equals("sdk") && mHttpProxy == null)
            {
            	Class ProxyPropertiesClass = mHttpProxyField.getType();
            	Constructor constr = ProxyPropertiesClass.getConstructors()[1];
            	Object ProxyProperties = constr.newInstance("10.11.12.13",1983,"");
            	mHttpProxyField.set(linkProperties, ProxyProperties);
            	mHttpProxy = mHttpProxyField.get(linkProperties);
            }
            
            if (mHttpProxy != null)
            {
            	Field mHostField = mHttpProxy.getClass().getDeclaredFields()[2];
            	mHostField.setAccessible(true);
            	String mHost = (String) mHostField.get(mHttpProxy);
            	
            	Field mPortField = mHttpProxy.getClass().getDeclaredFields()[4];
            	mPortField.setAccessible(true);
            	Integer mPort = (Integer) mPortField.get(mHttpProxy);
            	
            	Field mExclusionListField = mHttpProxy.getClass().getDeclaredFields()[1];
            	mExclusionListField.setAccessible(true);
            	String mExclusionList = (String) mExclusionListField.get(mHttpProxy);
            	
            	Log.d(TAG, "Proxy configuration: " + mHost + ":" + mPort + " , Exclusion List: " + mExclusionList);
            	
            	proxyHost = new ProxyConfiguration(new HttpHost(mHost,mPort), mExclusionList, wifiConf);
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        return proxyHost;
	}


}
