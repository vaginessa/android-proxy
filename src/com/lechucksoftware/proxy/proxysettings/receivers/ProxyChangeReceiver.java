package com.lechucksoftware.proxy.proxysettings.receivers;

import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.services.ProxySettingsCheckerService;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.os.Bundle;
import android.util.Log;

public class ProxyChangeReceiver extends BroadcastReceiver
{
    public static String TAG = "ProxyChangeReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        LogWrapper.d(TAG,"Intent receiver called: " + intent.getAction().toString());

        if (intent.getAction().toString().equals("com.lechucksoftware.proxy.proxysettings.UPDATE_NOTIFICATION"))
        {
        	ProxySettingsCheckerService.CompletedStatusBarNotification(context);
        }
        else if (intent.getAction().equals(Constants.PROXY_CONFIGURATION_UPDATED) 	||
        		 intent.getAction().equals(Constants.PROXY_CONFIGURATION_CHANGED) 	|| 
        		 intent.getAction().equals(Proxy.PROXY_CHANGE_ACTION) 					||
        		 intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))	// Connection type change (switch between 3G/WiFi)
        {
        	//LogWrapper.logIntent(TAG, intent, Log.DEBUG);        	
        	//Intent msgIntent = new Intent(context, ProxySettingsCheckerService.class);
            //context.startService(msgIntent);	
        }
        else
        {
        	LogWrapper.logIntent(TAG, intent, Log.ERROR);  
        	LogWrapper.e(TAG,"Intent not found!");
        }
    }
}

