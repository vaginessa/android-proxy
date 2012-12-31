package com.lechucksoftware.proxy.proxysettings.services;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
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
    		String callerAction = callerIntent.getAction(); 
    		
    		if (
    			callerAction.equals(Constants.PROXY_SETTINGS_STARTED) 
    			|| callerAction.equals(Constants.PROXY_CONFIGURATION_UPDATED) 
    			|| callerAction.equals(ConnectivityManager.CONNECTIVITY_ACTION) 
    			//|| callerAction.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
    			|| callerAction.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    			|| callerAction.equals(WifiManager.RSSI_CHANGED_ACTION)
    			)
    		{
    			if (callerAction.equals(Constants.PROXY_SETTINGS_STARTED)) 
    				ApplicationGlobals.getWifiManager().startScan();
    				
    			CheckProxySettings(callerIntent);
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
	public void CheckProxySettings(Intent intent) 
	{
		try
        {   
			ApplicationGlobals.getInstance().proxyCheckStatus = ProxyCheckStatus.CHECKING;
			ToggleApplicationStatus();
			
        	// Get information regarding other configured AP
        	List<ProxyConfiguration> confs = ProxySettings.getProxiesConfigurations(getApplicationContext());
        	List<ScanResult> scanResults = ApplicationGlobals.getWifiManager().getScanResults();
        	
        	for (ProxyConfiguration conf : confs)
        	{
        		ApplicationGlobals.addConfiguration(Utils.cleanUpSSID(conf.getSSID()), conf);
        	}
        	
        	if (scanResults != null)
        	{
        		for (ScanResult res : scanResults)
        		{
        			
        		}
        	}
        	
        	ApplicationGlobals.getCurrentConfiguration().acquireProxyStatus(ApplicationGlobals.getInstance().timeout);
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
			ToggleApplicationStatus();
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