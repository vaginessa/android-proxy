package com.lechucksoftware.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;

import com.lechucksoftware.proxy.lib.reflection.android.RProxyProperties;

import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class ProxySettings
{
	public static final String TAG = "ProxySettings";
	
	public static HttpHost getProxyConfiguration(Context ctx, WifiConfiguration wifiConf)
	{
    	if (Build.VERSION.SDK_INT >= 11) 
    	{
    		return getProxySdk11(ctx, wifiConf);
		}
    	else
    	{
    		return getProxy(ctx); // Same configuration for every AP :(
    	}		
	}
	
	public static List<HttpHost> getProxiesConfigurations(Context ctx)
	{
    	if (Build.VERSION.SDK_INT >= 11) 
    	{
    		return getProxiesSdk11(ctx);
		}
    	else
    	{
    		HttpHost proxy = getProxy(ctx);
    		if (proxy != null)
    		{
    			List<HttpHost> proxyHosts =	new ArrayList<HttpHost>();
    			proxyHosts.add(proxy);
    			return proxyHosts;
    		}
    		else
    			return null;
    	}
	}
	
	private static HttpHost getProxy(Context ctx)
	{
		HttpHost proxyHost = null;
		
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
    				proxyHost = new HttpHost(proxyAddress, proxyPort);
    				Log.d(TAG, "ProxyHost created: " + proxyHost.toHostString());
    			}
    			catch (NumberFormatException e)
    			{
    				Log.d(TAG, "Port is not a number: " + proxyParts[1]);
    			}
			}
		}
		
		return proxyHost;
	}

	private static HttpHost getProxySdk11(Context ctx, WifiConfiguration wifiConf)
	{
		HttpHost proxyHost = null;
		      
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
            	
            	proxyHost = new HttpHost(mHost,mPort);
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        return proxyHost;
	}

	private static List<HttpHost> getProxiesSdk11(Context ctx)
	{
		return null;
	}
}
