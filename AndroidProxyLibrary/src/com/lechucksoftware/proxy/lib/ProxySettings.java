package com.lechucksoftware.proxy.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

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
	
	public static HttpHost getProxyConfiguration(Context ctx)
	{
    	if (Build.VERSION.SDK_INT >= 11) 
    	{
    		return getProxySdk11(ctx);
		}
    	else
    	{
    		return getProxy(ctx);
    	}
	}
	
	private static HttpHost getProxySdk11(Context ctx)
	{
		HttpHost proxy = null;
		WifiConfiguration conf = new WifiConfiguration();
        
        try
        {           
            Field linkPropertiesField = conf.getClass().getField("linkProperties");
            Object linkProperties = linkPropertiesField.get(conf);
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
            	
            	proxy = new HttpHost(mHost,mPort);
            }
            
            return proxy;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        return proxy;
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
}
