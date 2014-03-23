package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import com.lechucksoftware.proxy.proxysettings.R;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class AdvancedPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.advanced_preferences);
		
		ActionBar actionBar = getActivity().getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		// TODO Auto-generated method stub
	}

}
