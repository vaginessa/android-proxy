package com.lechucksoftware.proxy.proxysettings;

import java.net.Proxy.Type;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ProxySettingsCheckerService extends IntentService 
{
	public static String TAG = "ProxySettingsCheckerServices";
	
    public ProxySettingsCheckerService() 
    {
        super("ProxySettingsCheckerService");
    }
 
    @Override
    protected void onHandleIntent(Intent intent) 
    {
        CheckProxySettings(getApplicationContext());
    }
    
    @Override
    public void onDestroy() 
    {
    	Log.d(TAG, "ProxySettingsCheckerService destroying");
    };
    
	/**
	 * @param context
	 */
	public void CheckProxySettings(Context context) 
	{
		try
        {
			//StartedStatusBarNotification(getApplicationContext());
			
        	ProxyConfiguration proxyConfig = null;
        	
        	try 
        	{
        		proxyConfig = ProxySettings.getCurrentHttpProxyConfiguration(context);
    		} 
        	catch (Exception e) 
        	{
    			e.printStackTrace();
    		}
        	
        	proxyConfig.acquireProxyStatus();
        	       	
        	/**
        	 * Enable status bar notification when the proxy is enabled        	
        	 * */
        	CompletedStatusBarNotification(context, proxyConfig);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	Utils.DisableProxyNotification(context);
        	Log.d(TAG,"Exception caught: disable proxy notification");
        }
	}

	public void StartedStatusBarNotification(Context context)
	{
		if (Build.VERSION.SDK_INT < 11)
		{
			Utils.SetProxyNotification(context, null, ProxyCheckStatus.CHECKING);
		}
	}
	
	
	/**
	 * @param context
	 * @param proxyConfig
	 * @param status 
	 */
	public void CompletedStatusBarNotification(Context context, ProxyConfiguration proxyConfig) 
	{
		if (Build.VERSION.SDK_INT < 11)
		{
			if(proxyConfig.proxyHost.type() == Type.DIRECT)
			{
				// Do nothing
				Log.d(TAG, "Proxy is DIRECT");
				Utils.DisableProxyNotification(context);
			}
			else
			{
				// Show notification when the proxy is set
				Log.d(TAG, "Proxy enabled: " + proxyConfig.toShortString());
				Utils.SetProxyNotification(context, proxyConfig, ProxyCheckStatus.CHECKED);
			}
		}
	}
}