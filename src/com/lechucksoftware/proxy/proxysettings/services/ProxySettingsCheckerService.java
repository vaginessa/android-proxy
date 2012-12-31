package com.lechucksoftware.proxy.proxysettings.services;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;

public class ProxySettingsCheckerService extends IntentService 
{
	public static final String CALLER_INTENT = "CallerIntent";
	public static String TAG = "ProxySettingsCheckerService";
	
    public ProxySettingsCheckerService() 
    {
        super("ProxySettingsCheckerService");
    }
 
    @Override
    protected void onHandleIntent(Intent intent) 
    {
    	Intent callerIntent = (Intent) intent.getExtras().get(CALLER_INTENT);
    	
    	if (callerIntent != null)
    	{
    		LogWrapper.logIntent(TAG, callerIntent, Log.INFO);
    		
    		if (
    			callerIntent.getAction().equals(Constants.PROXY_SETTINGS_STARTED) ||
    			callerIntent.getAction().equals(Constants.PROXY_CONFIGURATION_UPDATED) ||
    			callerIntent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)	
    			)
    		{
    			CheckProxySettings(getApplicationContext(), callerIntent);
    		}
    		else
    		{
    			// TODO: ????
    		}
    	}
    	else
    	{
    		// TODO: ????
    	}
    }
    
    @Override
    public void onDestroy() 
    {
    	//LogWrapper.d(TAG, "ProxySettingsCheckerService destroying");
    };
    
	/**
	 * @param context
	 */
	public void CheckProxySettings(Context context, Intent intent) 
	{
		try
        {   
			ApplicationGlobals.getInstance().proxyCheckStatus = ProxyCheckStatus.CHECKING;
			ToggleApplicationStatus(context);
			
			// Get information regarding current proxy configuration
        	ApplicationGlobals.getInstance().proxyConf = ProxySettings.getCurrentHttpProxyConfiguration(context);
        	ApplicationGlobals.getInstance().proxyConf.acquireProxyStatus(ApplicationGlobals.getInstance().timeout); // Can take some time to execute this task!!
        	
        	// Get information regarding other configured AP
        	List<ProxyConfiguration> confs = ProxySettings.getProxiesConfigurations(getApplicationContext());
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	UIUtils.DisableProxyNotification(context);
        	LogWrapper.d(TAG,"Exception caught: disable proxy notification");
        }
		finally
		{
			ApplicationGlobals.getInstance().proxyCheckStatus = ProxyCheckStatus.CHECKED;
			ToggleApplicationStatus(context);
		}
	}
	
	public void ToggleApplicationStatus(Context context)
	{
    	/**
    	 * Call the update of the UI     	
    	 * */
		LogWrapper.d(TAG, "Sending broadcast intent " + Constants.PROXY_UPDATE_NOTIFICATION);
		Intent intent = new Intent(Constants.PROXY_UPDATE_NOTIFICATION);	
		context.sendBroadcast(intent);
		
    	UIUtils.UpdateStatusBarNotification(context);
	}
}