package com.lechucksoftware.proxy.proxysettings;

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
        Log.d(TAG,"Intent receiver called: " + intent.toString());
        
        Intent msgIntent = new Intent(context, ProxySettingsCheckerService.class);
        context.startService(msgIntent);
    }
}

