package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.activities.help.DisclaimerFragmentActivity;

public class ProxySettingsMainActivity extends Activity 
{
	public static String TAG = "ProxySettingsActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {    
    	super.onCreate(savedInstanceState);
    	
    	Globals.getInstance().addApplicationContext(getApplicationContext());
    	
    	Log.d(TAG, "Calling broadcast intent com.lechucksoftware.proxy.proxysettings.PROXY_CHANGE");
    	sendBroadcast(new Intent("com.lechucksoftware.proxy.proxysettings.PROXY_CHANGE"));
    	
    	// Restore preferences
        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        boolean acceptedDisclaimer = settings.getBoolean(Constants.PREFERENCES_ACCEPTED_DISCLAIMER, false);
          
        if (acceptedDisclaimer)
        {
        	Log.d(TAG, "Starting ProxySettingsCallerActivity activity");
            Intent i = new Intent(getApplicationContext(), ProxySettingsCallerActivity.class);
            startActivity(i);
        }
        else
        {
        	Log.d(TAG, "Starting DisclaimerActivity activity");
        	Intent disclaimer = new Intent(getApplicationContext(),DisclaimerFragmentActivity.class);
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