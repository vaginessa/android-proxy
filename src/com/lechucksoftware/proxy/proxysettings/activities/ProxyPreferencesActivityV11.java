package com.lechucksoftware.proxy.proxysettings.activities;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.R;

public class ProxyPreferencesActivityV11 extends PreferenceActivity {

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		//addPreferencesFromResource(R.xml.preferences);
	}
	
	public void onBuildHeaders(List<Header> target) 
	{
        loadHeadersFromResource(R.xml.preferences_header, target);
    }

	
    /**
     * This fragment contains a second-level set of preference that you
     * can get to by tapping an item in the first preferences fragment.
     */
    public static class MainPrefsFragment extends PreferenceFragment 
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Can retrieve arguments from preference XML.
            Log.i("args", "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
        
    	@Override
    	public void onSaveInstanceState(Bundle outState) {
    		super.onSaveInstanceState(outState);
    		

    	}
    	
    	@Override
    	public void onDestroy() {
    		super.onDestroy();


    	}
    }
}