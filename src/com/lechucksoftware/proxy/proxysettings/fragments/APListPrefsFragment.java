package com.lechucksoftware.proxy.proxysettings.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.preferences.AccessPoint;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;

public class APListPrefsFragment extends PreferenceFragment
{
	private TextView mEmptyView;
	private IntentFilter mFilter;
	private BroadcastReceiver mReceiver;
	private WifiManager mWifiManager;

	private DetailedState mLastState;
	private WifiInfo mLastInfo;

	// Combo scans can take 5-6s to complete - set to 10s.
	private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

	public void StartMainPrefsFragment()
	{
		mFilter = new IntentFilter();
		mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		// mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		// mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent)
			{
				handleEvent(context, intent);
			}
		};
	}

	@Override
	public void onResume()
	{
		super.onResume();

		getActivity().registerReceiver(mReceiver, mFilter);
		mWifiManager.startScan();
		updateAccessPoints();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
	}

	private void handleEvent(Context context, Intent intent)
	{
		String action = intent.getAction();

		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action))
		{
			updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
		}
		else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action))
		{
			updateAccessPoints();
		}
		else if (WifiManager.RSSI_CHANGED_ACTION.equals(action))
		{
			updateConnectionState(null);
		}
	}

	private void updateConnectionState(DetailedState state)
	{
		/* sticky broadcasts can call this when wifi is disabled */
		if (!mWifiManager.isWifiEnabled())
		{
			return;
		}

		mLastInfo = mWifiManager.getConnectionInfo();
		if (state != null)
		{
			mLastState = state;
		}

		for (int i = getPreferenceScreen().getPreferenceCount() - 1; i >= 0; --i)
		{
			// Maybe there's a WifiConfigPreference
			Preference preference = getPreferenceScreen().getPreference(i);
			if (preference instanceof AccessPoint)
			{
				final AccessPoint accessPoint = (AccessPoint) preference;
				accessPoint.update(mLastInfo, mLastState);
			}
		}
	}

	private void updateWifiState(int state)
	{
		switch (state)
		{
			case WifiManager.WIFI_STATE_ENABLED:
				// mScanner.resume();
				return; // not break, to avoid the call to pause() below

			case WifiManager.WIFI_STATE_ENABLING:
				addMessagePreference(R.string.wifi_starting);
				break;

			case WifiManager.WIFI_STATE_DISABLED:
				addMessagePreference(R.string.wifi_empty_list_wifi_off);
				break;
		}

		mLastInfo = null;
		mLastState = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		// Can retrieve arguments from preference XML.
		LogWrapper.i("args", "Arguments: " + getArguments());

		addPreferencesFromResource(R.xml.ap_preferences);
		// getPreferenceScreen().removeAll();
		StartMainPrefsFragment();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * Shows the latest access points available with supplimental
	 * information like the strength of network and the security for it.
	 */
	private void updateAccessPoints()
	{
		// Safeguard from some delayed event handling
		if (getActivity() == null)
			return;

		final Collection<AccessPoint> accessPoints = constructAccessPoints();

		PreferenceScreen mPollPref = getPreferenceScreen();
		mPollPref.removeAll();

		if (accessPoints.size() == 0)
		{
			addMessagePreference(R.string.wifi_empty_list_wifi_on);
		}
		for (AccessPoint accessPoint : accessPoints)
		{
			getPreferenceScreen().addPreference(accessPoint);
		}
	}

	/** A restricted multimap for use in constructAccessPoints */
	private class Multimap<K, V>
	{
		private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

		/** retrieve a non-null list of values with key K */
		List<V> getAll(K key)
		{
			List<V> values = store.get(key);
			return values != null ? values : Collections.<V> emptyList();
		}

		void put(K key, V val)
		{
			List<V> curVals = store.get(key);
			if (curVals == null)
			{
				curVals = new ArrayList<V>(3);
				store.put(key, curVals);
			}
			curVals.add(val);
		}
	}

	/** Returns sorted list of access points */
	private List<AccessPoint> constructAccessPoints()
	{
		ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
		/**
		 * Lookup table to more quickly update AccessPoints by only
		 * considering objects with the correct SSID. Maps SSID -> List of
		 * AccessPoints with the given SSID.
		 */
		Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

		final List<ProxyConfiguration> confs = ProxySettings.getProxiesConfigurations(getActivity());

		if (confs != null)
		{
			for (ProxyConfiguration config : confs)
			{

				AccessPoint accessPoint = new AccessPoint(getActivity(), config);
				accessPoint.update(mLastInfo, mLastState);
				accessPoints.add(accessPoint);
				apMap.put(accessPoint.ssid, accessPoint);
			}
		}

		// Pre-sort accessPoints to speed preference insertion
		Collections.sort(accessPoints);
		return accessPoints;
	}

	private void addMessagePreference(int messageId)
	{
		if (mEmptyView != null)
			mEmptyView.setText(messageId);
		getPreferenceScreen().removeAll();
	}
}