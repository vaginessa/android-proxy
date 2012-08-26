package com.lechucksoftware.proxy.proxysettings.activities;

import com.lechucksoftware.proxy.proxysettings.Constants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Proxy;
import android.os.Bundle;
import android.util.Log;

public class ProxySettingsMainActivity extends Activity 
{
	public static String TAG = "ProxySettingsActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	sendBroadcast(new Intent(Proxy.PROXY_CHANGE_ACTION));
    	
    	// Restore preferences
        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        boolean acceptedDisclaimer = settings.getBoolean(Constants.PREFERENCES_ACCEPTED_DISCLAIMER, false);
          
        if (acceptedDisclaimer)
        {
            Intent i = new Intent(getApplicationContext(), ProxySettingsCallerActivity.class);
            startActivity(i);
        }
        else
        {
        	Intent disclaimer = new Intent(getApplicationContext(),DisclaimerActivity.class);
            startActivity(disclaimer); 
        }
    	
        finish();
    }
        
    public void onBackPressed() 
    {
    	Log.d(TAG, "Back Pressed");
    	return;
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Start");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }
    @Override
    protected void onStop() {
    	Log.d(TAG, "Stop");
        super.onStop();
    }
    @Override
    protected void onDestroy() {
    	Log.d(TAG, "Destroy");
        super.onDestroy();
    }
    
    
}