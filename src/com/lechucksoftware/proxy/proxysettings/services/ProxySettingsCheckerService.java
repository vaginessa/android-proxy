package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.ProxyConfiguration;

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
    		String callerAction = callerIntent.getAction(); 
    		
    		if (   callerAction.equals(Constants.PROXY_SETTINGS_STARTED) 
    			|| callerAction.equals(Constants.PROXY_CONFIGURATION_UPDATED)
    			|| callerAction.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)		)
    		{
    			LogWrapper.logIntent(TAG, callerIntent, Log.WARN);
    			
    			NetworkInfo ni = ApplicationGlobals.getConnectivityManager().getActiveNetworkInfo();
        		
    			if (ni != null && ni.isConnected())
    			{
    				CheckProxySettings(callerIntent);
    			}
    		}
    		else if (callerAction.equals(ConnectivityManager.CONNECTIVITY_ACTION))
    		{
    			Boolean noConnectivity = callerIntent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
    			if(noConnectivity)
    				return;
    			
    			NetworkInfo ni = ApplicationGlobals.getConnectivityManager().getActiveNetworkInfo();
    		
    			if (ni != null && ni.isConnected())
    			{
    				if (ni.getType() == callerIntent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1))
    				{
    					LogWrapper.logIntent(TAG, callerIntent, Log.ERROR, true);
    					CheckProxySettings(callerIntent);
    				}
    				else
    					LogWrapper.logIntent(TAG, callerIntent, Log.DEBUG, true);
    			}
    			else
    				LogWrapper.logIntent(TAG, callerIntent, Log.DEBUG, true);
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
    	LogWrapper.d(TAG, "ProxySettingsCheckerService destroying");
    };
    
	/**
	 * @param context
	 */
	public void CheckProxySettings(Intent callerIntent) 
	{
		try
        {   
			ProxyConfiguration oldconf = null;
			
			ApplicationGlobals.getInstance().proxyCheckStatus = ProxyCheckStatus.CHECKING;
			
			if (!callerIntent.getAction().equals(Constants.PROXY_SETTINGS_STARTED))
			{
				oldconf = ApplicationGlobals.getCurrentConfiguration();
			}
			
			ToggleApplicationStatus();
			
			ApplicationGlobals.updateProxyConfigurationList();
			
        	ProxyConfiguration newconf = ApplicationGlobals.getCurrentConfiguration();
        	
        	if (oldconf == null || oldconf.compareTo(newconf) != 0)  	// Only at first start of ProxySettings or if it's different from previous configuration
        	{
        		newconf.acquireProxyStatus(ApplicationGlobals.getInstance().timeout);	 
        	}
        	
        	LogWrapper.logIntent(TAG, callerIntent, Log.ERROR);
        	LogWrapper.i(TAG, newconf.toString());
        	ToggleApplicationStatus();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	UIUtils.DisableProxyNotification(getApplicationContext());
        	LogWrapper.d(TAG,"Exception caught: disable proxy notification");
        }
		finally
		{
			ApplicationGlobals.getInstance().proxyCheckStatus = ProxyCheckStatus.CHECKED;
		}
	}
	
	public void ToggleApplicationStatus()
	{
    	/**
    	 * Call the update of the UI     	
    	 * */
		LogWrapper.d(TAG, "Sending broadcast intent " + Constants.PROXY_UPDATE_NOTIFICATION);
		Intent intent = new Intent(Constants.PROXY_UPDATE_NOTIFICATION);	
		getApplicationContext().sendBroadcast(intent);
		
    	UIUtils.UpdateStatusBarNotification(getApplicationContext());
	}
}