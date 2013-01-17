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
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.View;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.preferences.ApSelectorDialogPreference;
import com.lechucksoftware.proxy.proxysettings.preferences.ValidationPreference;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.reflection.android.RProxySettings;

public class MainAPPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
	public static MainAPPrefsFragment instance;

	public static final String TAG = "MainAPPrefsFragment";
	// private TextView mEmptyView;
	private ApSelectorDialogPreference apSelectorPref;
	private PreferenceScreen authPrefScreen;
	private CheckBoxPreference notificationPref;
	private CheckBoxPreference notificationAlwaysPref;
	private ValidationPreference proxyEnabledValidPref;
	private Preference proxyTester;
	private Preference helpPref;
	private Preference aboutPref;
	private SwitchPreference proxyEnablePref;
	private PreferenceCategory apCategoryPref;
	private SwitchPreference wifiEnabledPref;
	private ProxyConfiguration selectedConfiguration;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
		instance = this;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		getUIComponents();
		refreshUIComponents();
		selectAP(ApplicationGlobals.getCachedConfiguration());
	}

	public void selectAP(ProxyConfiguration conf)
	{
		//		if (selectedConfiguration == null)
		//		{
		//			if (ApplicationGlobals.getWifiManager().isWifiEnabled())
		//			{
		if (conf.isValidConfiguration())
		{
			selectedConfiguration = conf;
			refreshAP();
		}
		//			}
		//		}
	}

	private void refreshAP()
	{
		if (selectedConfiguration != null)
		{
			apSelectorPref.setEnabled(true);
			proxyEnablePref.setEnabled(true);
			
			apSelectorPref.setSummary(Utils.cleanUpSSID(selectedConfiguration.getSSID()) + " - " + selectedConfiguration.getAPDescription(getActivity()));

			if (selectedConfiguration.proxyToggle == RProxySettings.NONE || selectedConfiguration.proxyToggle == RProxySettings.UNASSIGNED)
			{
				proxyEnablePref.setChecked(false);
			}
			else
			{
				proxyEnablePref.setChecked(true);
			}
		}
	}

	private void getUIComponents()
	{
		wifiEnabledPref = (SwitchPreference) findPreference("pref_wifi_enabled");
		wifiEnabledPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				Boolean isChecked = (Boolean) newValue;
				ApplicationGlobals.getWifiManager().setWifiEnabled(isChecked);

				if (isChecked == false)
				{
					// Immediately disable when Wi-Fi is set to OFF
					apSelectorPref.setEnabled(isChecked);
					proxyEnablePref.setEnabled(isChecked);
				}

				return true;
			}
		});

		apCategoryPref = (PreferenceCategory) findPreference("pref_ap_category");
		apSelectorPref = (ApSelectorDialogPreference) findPreference("pref_ap_selector_dialog");

		proxyEnablePref = (SwitchPreference) findPreference("pref_proxy_enabled");
		proxyEnablePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				Boolean isChecked = (Boolean) newValue;

				if (isChecked)
				{
					selectedConfiguration.proxyToggle = RProxySettings.STATIC;
				}
				else
				{
					selectedConfiguration.proxyToggle = RProxySettings.NONE;
				}

				selectedConfiguration.writeConfigurationToDevice();
				return true;
			}
		});

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
		boolean wifiEnabled = ApplicationGlobals.getWifiManager().isWifiEnabled();
		wifiEnabledPref.setChecked(wifiEnabled);
		
		refreshAP();

		//		ProxyConfiguration conf = ApplicationGlobals.getCurrentConfiguration();
		//
		//		getPreferenceScreen().removeAll();
		//		
		//		View v = getView();
		//		
		//		TextView valueTV = new TextView(getActivity());
		//	    valueTV.setText("Please Enable Wi-Fi");
		//	    valueTV.setId(5);
		//	    valueTV.setLayoutParams(new LayoutParams(
		//	            LayoutParams.FILL_PARENT,
		//	            LayoutParams.FILL_PARENT));
		//
		//		((LinearLayout) v).addView(valueTV);

		//		if (ApplicationGlobals.getWifiManager().isWifiEnabled())
		//		{
		//			// getPreferenceScreen().removePreference(authPrefScreen);
		//
		//			if (ApplicationGlobals.getCachedConfiguration().getCheckingStatus() == CheckStatusValues.CHECKED)
		//			{
		//				apSelectorPref.setTitle(Utils.cleanUpSSID(conf.getSSID()));
		//				apSelectorPref.setSummary(conf.toShortString());
		//				
		//				
		//				if (proxyEnablePref.isChecked())
		//				{
		//					
		//				}
		//				else
		//				{
		//					apCategoryPref.removePreference(proxyEnablePref);
		//				}
		//			}
		//		}
		//		else
		//		{
		//
		//		}

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		LogWrapper.d(TAG, "Changed preference: " + key);

		if (key == "pref_wifi_enabled")
		{
			ApplicationGlobals.getWifiManager().setWifiEnabled(wifiEnabledPref.isChecked());
		}
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
