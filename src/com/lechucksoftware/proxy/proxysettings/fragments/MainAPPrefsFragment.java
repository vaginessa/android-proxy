package com.lechucksoftware.proxy.proxysettings.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.preferences.ApSelectorDialogPreference;
import com.lechucksoftware.proxy.proxysettings.preferences.ValidationPreference;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyStatus;

public class MainAPPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
	public static final String TAG = "MainAPPrefsFragment";
//    private TextView mEmptyView;
    private ApSelectorDialogPreference apSelectorPref;
	private PreferenceScreen authPrefScreen;
	private CheckBoxPreference notificationPref;
	private CheckBoxPreference notificationAlwaysPref;
	private ValidationPreference proxyEnabledValidPref;
	private Preference proxyTester;
	private Preference helpPref;
	private Preference aboutPref;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		getUIComponents();
		refreshUIComponents();
	}
	
	private void getUIComponents()
	{
		apSelectorPref = (ApSelectorDialogPreference) findPreference("pref_ap_selector_dialog");
		authPrefScreen = (PreferenceScreen) findPreference("pref_key_proxy_settings_authentication_screen");
		notificationPref = (CheckBoxPreference) findPreference("preference_notification_enabled");
		notificationAlwaysPref = (CheckBoxPreference) findPreference("preference_notification_always_visible");
		
		proxyTester = (Preference) findPreference("preference_test_proxy_configuration");
		proxyEnabledValidPref = (ValidationPreference) findPreference("validation_proxy_enabled");
		proxyEnabledValidPref = (ValidationPreference) findPreference("validation_proxy_valid_address");
		proxyEnabledValidPref = (ValidationPreference) findPreference("validation_proxy_reachable");
		proxyEnabledValidPref = (ValidationPreference) findPreference("validation_web_reachable");
		
		helpPref = (Preference) findPreference("preference_help");
		aboutPref = (Preference) findPreference("preference_about");
	}
	
	private void refreshUIComponents()
	{
		ProxyConfiguration conf = ApplicationGlobals.getCurrentConfiguration();
		
		//getPreferenceScreen().removePreference(authPrefScreen);
		
		if (ApplicationGlobals.getInstance().proxyCheckStatus == ProxyCheckStatus.CHECKED)
		{
    		if (ApplicationGlobals.getWifiManager().isWifiEnabled())
    		{
    			apSelectorPref.setTitle(Utils.cleanUpSSID(conf.getSSID()));
    			apSelectorPref.setSummary(conf.toShortString());
    		}
    		else
    		{
    			// TODO : show dialog to ena	
    		}
		}
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		LogWrapper.d(TAG, "Changed preference: " + key);	
	}
	
	private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals("com.lechucksoftware.proxy.proxysettings.UPDATE_PROXY"))
			{
				LogWrapper.d(TAG, "Received broadcast for updated proxy configuration");
				refreshUIComponents();
			}
			else if (action.equals("com.shouldit.proxy.lib.PROXY_CHECK_STATUS_UPDATE"))
			{
				LogWrapper.d(TAG, "Received broadcast for partial update to proxy configuration");
				refreshUIComponents();
			}
		}
	};
	
	@Override
	public void onResume()
	{
		super.onResume();

		// Start register the status receivers
		IntentFilter ifilt = new IntentFilter();
		ifilt.addAction(Constants.PROXY_UPDATE_NOTIFICATION);
		ifilt.addAction(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
		getActivity().registerReceiver(changeStatusReceiver, ifilt); 
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		
		// Stop the registered status receivers
		getActivity().unregisterReceiver(changeStatusReceiver); 
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
}