package com.lechucksoftware.proxy.proxysettings.fragments;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.preferences.AccessPoint;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;

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
		
		ProxyConfiguration conf;
		try
		{
			conf = ProxySettings.getCurrentHttpProxyConfiguration(getActivity());
			AccessPoint ap = new AccessPoint(getActivity(),conf);
			ap.setOrder(0);
			getPreferenceScreen().addPreference(ap);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}