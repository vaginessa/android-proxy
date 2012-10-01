package com.lechucksoftware.proxy.proxysettings.activities;

import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.ValidationPreference;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;
import com.shouldit.proxy.lib.ProxyUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Proxy;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class ProxyPreferencesActivity extends PreferenceActivity
{
	public static String TAG = "ProxyPreferencesActivity";
    static final int SELECT_PROXY_REQUEST = 0;
    
    SharedPreferences sharedPref;
    
    CheckBoxPreference notificationEnabled;
    CheckBoxPreference authenticationEnabled;
    EditTextPreference userPref;
    EditTextPreference passwordPref;
    Preference proxyHostPortPref; 
    
    ValidationPreference proxyEnabledPref;
    ValidationPreference proxyAddressPref;
    ValidationPreference proxyReachablePref;
    ValidationPreference proxyWebReachablePref;
    
//    static Preference appsFeedbackPref;
    
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		notificationEnabled = (CheckBoxPreference) findPreference("preference_notification_enabled");
		authenticationEnabled = (CheckBoxPreference) findPreference("preference_authentication_enabled");
		userPref = (EditTextPreference) findPreference("preference_authentication_user");
		passwordPref = (EditTextPreference) findPreference("preference_authentication_password");
		proxyHostPortPref = findPreference("preference_proxy_host_port");
		
		proxyEnabledPref = (ValidationPreference) findPreference("validation_proxy_enabled");
		proxyAddressPref = (ValidationPreference) findPreference("validation_proxy_valid_address");
		proxyReachablePref = (ValidationPreference) findPreference("validation_proxy_reachable");
		proxyWebReachablePref = (ValidationPreference) findPreference("validation_web_reachable");
		
//		appsFeedbackPref = findPreference("preference_applications_feedback");
		
		refreshPreferenceSettings();
		setListenersToUI();
		
		refreshProxySettings();
	}

    private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver() 
    { 
        @Override 
        public void onReceive(Context context, Intent intent) 
        { 
            String action = intent.getAction(); 
            if (action.equals("com.lechucksoftware.proxy.proxysettings.UPDATE_PROXY")) 
            { 
            	Log.d(TAG, "Received broadcast for Updated proxy configuration");
            	refreshProxySettings();
            }         
        } 
    };
	
	public void setListenersToUI()
	{
		notificationEnabled.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
            	sendBroadcast(new Intent(Proxy.PROXY_CHANGE_ACTION));
                return checkNotificationPref(newValue);
            }
        });
		
		authenticationEnabled.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                return checkAuthenticationPref(newValue);
            }
        });
				
		
		proxyHostPortPref.setOnPreferenceClickListener(new OnPreferenceClickListener() 
		{
		    public boolean onPreferenceClick(Preference preference) 
            {
                Intent proxyIntent = ProxyUtils.getProxyIntent();
                startActivityForResult(proxyIntent,SELECT_PROXY_REQUEST);
                return true;
            }
        });
		
//		appsFeedbackPref.setOnPreferenceClickListener(new OnPreferenceClickListener() 
//		{
//		    public boolean onPreferenceClick(Preference preference) 
//            {
//                Intent feedbackIntent = new Intent(getApplicationContext(), ApplicationsFeedbacksActivity.class);
//                startActivity(feedbackIntent);
//                return true;
//            }
//        });
		
		userPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() 
		{
			public boolean onPreferenceChange(Preference preference, Object newValue) 
			{
				return checkUsernamePref(newValue);
			}
		});
		
		passwordPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) 
			{
				return checkPasswordPref(newValue);
			}
		});
	}	

	public boolean checkUsernamePref(Object newValue) 
	{
		Log.d(TAG, "checkUsernamePref : " + (String) newValue);
		
		userPref.setSummary((String) newValue);
		return true;
	}
	
	public boolean checkNotificationPref (Object newValue) 
	{
		Log.d(TAG, "checkNotificationPref : " + (Boolean) newValue);
		
		if ((Boolean) newValue)
		{
			notificationEnabled.setSummary(getApplicationContext().getText(R.string.preferences_statusbar_notification_description_enabled));
		}
		else
		{
			notificationEnabled.setSummary(getApplicationContext().getText(R.string.preferences_statusbar_notification_description_disabled));
		}
        return true;
	}
	
	public boolean checkAuthenticationPref (Object newValue) 
	{
		Log.d(TAG, "checkAuthenticationPref : " + (Boolean) newValue);
		
		if ((Boolean) newValue)
        {
            userPref.setEnabled(true);
            passwordPref.setEnabled(true);
            authenticationEnabled.setSummary(getApplicationContext().getText(R.string.preference_proxy_authentication_summary_enabled));
        }
        else
        {
            userPref.setEnabled(false);
            passwordPref.setEnabled(false);
            authenticationEnabled.setSummary(getApplicationContext().getText(R.string.preference_proxy_authentication_summary_disabled));
        }
		
        return true;
	}
	
	public boolean checkPasswordPref(Object newValue) 
	{
		Log.d(TAG, "checkPasswordPref : " + (String) newValue);
		
		String pwd = newValue.toString();
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<pwd.length(); i++)
		{
			sb.append('*');
		}
		
		passwordPref.setSummary(sb.toString());
		return true;
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        if (requestCode == SELECT_PROXY_REQUEST) 
        {
            refreshProxySettings();
        }
    }
    
    private void refreshPreferenceSettings()
    {   	
    	checkNotificationPref(sharedPref.getBoolean("preference_notification_enabled", false));
    	checkAuthenticationPref(sharedPref.getBoolean("preference_authentication_enabled", false));
    	checkUsernamePref(sharedPref.getString("preference_authentication_user", ""));
    	checkPasswordPref(sharedPref.getString("preference_authentication_password", ""));
    }
    
    private void refreshProxySettings()
    {   
    	proxyHostPortPref.setSummary(UIUtils.GetStatusSummary(getApplicationContext()));
    }
	
	private void openFeedbacks()
	{
        Intent test = new Intent(getApplicationContext(), ApplicationsFeedbacksActivity.class);
        startActivity(test);
	}
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Start");
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        
        // Start register the status receiver
        IntentFilter ifilt = new IntentFilter("com.lechucksoftware.proxy.proxysettings.UPDATE_PROXY"); 
        registerReceiver(changeStatusReceiver, ifilt);
    
    }
   
    @Override
    protected void onPause() 
    {
        super.onPause();        
        
        // Stop the registerd status receiver
        unregisterReceiver(changeStatusReceiver);
    }
    
    @Override
    protected void onStop() {
    	Log.d(TAG, "Stop");
        super.onStop();
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}
}