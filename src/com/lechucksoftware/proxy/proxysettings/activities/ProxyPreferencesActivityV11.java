package com.lechucksoftware.proxy.proxysettings.activities;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

public class ProxyPreferencesActivityV11 extends PreferenceActivity
{
	public static ProxyPreferencesActivityV11 instance;
	
	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;

	// static Preference appsFeedbackPref;

	public void showProgressDialog()
	{
		if (mProgressDialog != null)
			mProgressDialog.show();
	}

	public void dismissProgressDialog()
	{
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	public void setProgressDialogMessage(String message)
	{
		if (mProgressDialog != null)
			mProgressDialog.setMessage(message);
	}

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

        // Add a button to the header list.
        if (hasHeaders()) 
        {
            Button button = new Button(this);
            button.setText("Some action");
            setListFooter(button);
        }
	}
	
	@Override
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
        public void onCreate(Bundle savedInstanceState) 
        {
            super.onCreate(savedInstanceState);

            // Can retrieve arguments from preference XML.
            LogWrapper.i("args", "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.main_preferences);
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
    
    public static class ToolsFragment extends PreferenceFragment 
    {
        @Override
        public void onCreate(Bundle savedInstanceState) 
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.tools_preferences);
        }
    }
    
    public static class AboutFragment extends PreferenceFragment 
    {
        @Override
        public void onCreate(Bundle savedInstanceState) 
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.about_preferences);
        }
    }
}