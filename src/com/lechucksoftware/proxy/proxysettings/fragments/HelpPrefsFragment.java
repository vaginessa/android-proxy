package com.lechucksoftware.proxy.proxysettings.fragments;

import com.lechucksoftware.proxy.proxysettings.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class HelpPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.help_preferences);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		// TODO Auto-generated method stub
	}

}
