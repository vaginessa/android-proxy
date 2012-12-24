package com.lechucksoftware.proxy.proxysettings.fragments;

import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.preferences.ApSelectorDialogPreference;

public class MainAPPrefsFragment extends PreferenceFragment
{
	private WifiManager mWifiManager;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
		
		mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		ApSelectorDialogPreference appref = (ApSelectorDialogPreference) findPreference("pref_ap_selector_dialog");
		
		if (mWifiManager.isWifiEnabled())
		{
			WifiInfo wi = mWifiManager.getConnectionInfo();
			SupplicantState ss = wi.getSupplicantState();
			if (ss == SupplicantState.ASSOCIATED ||
				ss == SupplicantState.ASSOCIATING ||
				ss == SupplicantState.COMPLETED)
			{
				appref.setTitle(wi.getSSID());
				appref.setSummary(wi.getSupplicantState().toString());
			}
			else
			{
				appref.setEnabled(false);
			}
		}
		else
		{
			appref.setEnabled(false);
		}
	}
}