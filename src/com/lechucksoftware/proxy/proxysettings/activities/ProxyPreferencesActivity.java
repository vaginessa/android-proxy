package com.lechucksoftware.proxy.proxysettings.activities;

import com.lechucksoftware.proxy.proxysettings.ProxySettingsCheckerService;
import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;
import com.shouldit.proxy.lib.ProxyUtils;

import android.content.Intent;
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
    
    static CheckBoxPreference notificationEnabled;
    static CheckBoxPreference authenticationEnabled;
    static EditTextPreference userPref;
    static EditTextPreference passwordPref;
    static Preference proxyHostPortPref; 
//    static Preference appsFeedbackPref;
    
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		addPreferencesFromResource(R.xml.preferences);
		
		notificationEnabled = (CheckBoxPreference) findPreference("preference_notification_enabled");
		authenticationEnabled = (CheckBoxPreference) findPreference("preference_authentication_enabled");
		userPref = (EditTextPreference) findPreference("preference_authentication_user");
		passwordPref = (EditTextPreference) findPreference("preference_authentication_password");
		proxyHostPortPref = findPreference("preference_proxy_host_port");
//		appsFeedbackPref = findPreference("preference_applications_feedback");
		
		RefreshPreferenceSettings();
		RefreshProxySettings();
	
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
            RefreshProxySettings();
        }
    }
    
    private void RefreshPreferenceSettings()
    {   	
    	checkNotificationPref(sharedPref.getBoolean("preference_notification_enabled", false));
    	checkAuthenticationPref(sharedPref.getBoolean("preference_authentication_enabled", false));
    	checkUsernamePref(sharedPref.getString("preference_authentication_user", ""));
    	checkPasswordPref(sharedPref.getString("preference_authentication_password", ""));
    }
    
    
    private void RefreshProxySettings()
    {
        Preference proxyHostPortPref = findPreference("preference_proxy_host_port");
        
        try
        {
            ProxyConfiguration proxyConf = ProxySettings.getCurrentHttpProxyConfiguration(getApplicationContext());
            if (proxyConf.isProxyEnabled())
            {
                proxyHostPortPref.setSummary(proxyConf.toShortString());
            }
            else
            {
                proxyHostPortPref.setSummary(getApplicationContext().getText(R.string.preference_proxy_host_port_summary_default));
            }
        }
        catch(Exception e) 
        {
            proxyHostPortPref.setSummary(getApplicationContext().getText(R.string.preference_proxy_host_port_summary_exception));
            e.printStackTrace();
        }
    }
	
	private void OpenFeedbacks()
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
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume");
    }
    @Override
    protected void onPause() 
    {
        super.onPause();        
        Log.d(TAG, "Pause");
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