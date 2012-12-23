package com.lechucksoftware.proxy.proxysettings.services;

import java.net.Proxy.Type;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.ProxySettings;

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
    	LogWrapper.d(TAG, "ProxySettingsCheckerService destroying");
    };
    
	/**
	 * @param context
	 */
	public void CheckProxySettings(Context context) 
	{
		try
        {   
			Globals.getInstance().addApplicationContext(context);
			Globals.getInstance().proxyCheckStatus = ProxyCheckStatus.CHECKING;
			ToggleApplicationStatus(context);
			
        	Globals.getInstance().proxyConf = ProxySettings.getCurrentHttpProxyConfiguration(context);
        	Globals.getInstance().proxyConf.acquireProxyStatus(Globals.getInstance().timeout); // Can take some time to execute this task!!
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	UIUtils.DisableProxyNotification(context);
        	LogWrapper.d(TAG,"Exception caught: disable proxy notification");
        }
		finally
		{
			Globals.getInstance().proxyCheckStatus = ProxyCheckStatus.CHECKED;
			ToggleApplicationStatus(context);
		}
	}

	public void StartedStatusBarNotification(Context context)
	{
		if (Build.VERSION.SDK_INT < 11)
		{
			UIUtils.SetProxyNotification(context);
		}
	}
	
	
	public void ToggleApplicationStatus(Context context)
	{
    	/**
    	 * Trigger status update        	
    	 * */
		LogWrapper.d(TAG, "Sending broadcast intent UPDATE_PROXY");
		Intent intent = new Intent("com.lechucksoftware.proxy.proxysettings.UPDATE_PROXY");	
		context.sendBroadcast(intent);
		
    	CompletedStatusBarNotification(context);
	}
	
	/**
	 * @param context
	 * @param proxyConfig
	 * @param status 
	 */
	public static void CompletedStatusBarNotification(Context context) 
	{
		if (Build.VERSION.SDK_INT < 11)
		{
			if(Globals.getInstance().proxyConf.proxyHost.type() == Type.DIRECT)
			{
				// Do nothing
//				LogWrapper.d(TAG, "Proxy is DIRECT");
				UIUtils.DisableProxyNotification(context);
			}
			else
			{
				// Show notification when the proxy is set
//				LogWrapper.d(TAG, "Proxy enabled: " + Globals.getInstance().proxyConf.toShortString());
				UIUtils.SetProxyNotification(context);
			}
		}
	}
}