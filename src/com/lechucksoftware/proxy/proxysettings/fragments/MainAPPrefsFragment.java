package com.lechucksoftware.proxy.proxysettings.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

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
	
		
		final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();	
		List<ProxyConfiguration> confs = ProxySettings.getProxiesConfigurations(getActivity());
		
		CharSequence[] entries = { "One", "Two", "Three" };
		CharSequence[] entryValues = { "1", "2", "3" };
		
		for (ProxyConfiguration conf1 : confs)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			
			if (conf1.wifiConfiguration != null)
			{
				map.put("title", conf1.wifiConfiguration.SSID.replaceAll("\"", ""));
				map.put("pconf", conf1);
				data.add(map);
			}
		}	
		
		ListPreference aplist = (ListPreference) findPreference("pref_ap_selector");
		

		aplist.setEntries(entries);
		aplist.setEntryValues(entryValues);
		
//		
//		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_spinner_dropdown_item, new String[] { "title" }, new int[] { android.R.id.text1 });

//		actionBar.setListNavigationCallbacks(adapter, new OnNavigationListener()
//		{
//			public boolean onNavigationItemSelected(int itemPosition, long itemId)
//			{
//				Map<String, Object> map = data.get(itemPosition);
//				Object o = map.get("pconf");
//				if (o instanceof ProxyConfiguration)
//				{
//					// FragmentTransaction tx =
//					// getFragmentManager().beginTransaction();
//					// tx.replace( android.R.id.content,
//					// (Fragment )o );
//					// tx.commit();
//				}
//				return true;
//			}
//		});
	}
}