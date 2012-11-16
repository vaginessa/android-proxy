package com.lechucksoftware.proxy.proxysettings;

import com.lechucksoftware.proxy.proxysettings.services.ProxySettingsCheckerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ProxyChangeReceiver extends BroadcastReceiver
{
    public static String TAG = "ProxyChangeReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        Log.d(TAG,"Intent receiver called: " + intent.getAction().toString());

        if (intent.getAction().toString().equals("com.lechucksoftware.proxy.proxysettings.UPDATE_NOTIFICATION"))
        {
        	ProxySettingsCheckerService.CompletedStatusBarNotification(context);
        }
        else if (intent.getAction().toString().equals("com.lechucksoftware.proxy.proxysettings.UPDATE_PROXY") ||
        		 intent.getAction().toString().equals("android.intent.action.PROXY_CHANGE") ||
        		 intent.getAction().toString().equals("com.lechucksoftware.proxy.proxysettings.PROXY_CHANGE") ||
        		 intent.getAction().toString().equals("android.net.conn.CONNECTIVITY_CHANGE"))
        {
        	Intent msgIntent = new Intent(context, ProxySettingsCheckerService.class);
            context.startService(msgIntent);	
        }
        else
        {
        	Log.e(TAG,"Intent not found");
        }
    }
}

