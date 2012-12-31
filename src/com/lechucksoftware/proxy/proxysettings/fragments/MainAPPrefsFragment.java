package com.lechucksoftware.proxy.proxysettings.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.preferences.ApSelectorDialogPreference;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class MainAPPrefsFragment extends PreferenceFragment
{
    private TextView mEmptyView;

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
		ApSelectorDialogPreference appref = (ApSelectorDialogPreference) findPreference("pref_ap_selector_dialog");

		ProxyConfiguration conf = ApplicationGlobals.getCurrentConfiguration();
		if (conf != null)
		{
			appref.setTitle(Utils.cleanUpSSID(conf.getSSID()));
			appref.setSummary(conf.toShortString());
		}
//        mEmptyView = (TextView) getView().findViewById(android.R.id.empty);
//        ((ListActivity) getActivity()).getListView().setEmptyView(mEmptyView);
		
//		if (ApplicationGlobals.getWifiManager().isWifiEnabled())
//		{
//			WifiInfo wi = ApplicationGlobals.getWifiManager().getConnectionInfo();
//			
//			SupplicantState ss = wi.getSupplicantState();
//			if (ss == SupplicantState.ASSOCIATED ||
//				ss == SupplicantState.ASSOCIATING ||
//				ss == SupplicantState.COMPLETED)
//			{
//				appref.setTitle(wi.getSSID());
//				appref.setSummary(wi.getSupplicantState().toString());
//			}
//			else
//			{
//				appref.setEnabled(false);
//			}
//		}
//		else
//		{
//			appref.setEnabled(false);
//		}
	}
}