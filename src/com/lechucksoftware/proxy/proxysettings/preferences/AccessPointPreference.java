package com.lechucksoftware.proxy.proxysettings.preferences;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class AccessPointPreference extends Preference
{
	static final String TAG = "Settings.AccessPoint";

	public static final int INVALID_NETWORK_ID = -1;
	private static final int DISABLED_UNKNOWN_REASON = 0;
	private static final int DISABLED_DNS_FAILURE = 1;
	private static final int DISABLED_DHCP_FAILURE = 2;
	private static final int DISABLED_AUTH_FAILURE = 3;

	private static final String KEY_DETAILEDSTATE = "key_detailedstate";
	private static final String KEY_WIFIINFO = "key_wifiinfo";
	private static final String KEY_SCANRESULT = "key_scanresult";
	private static final String KEY_CONFIG = "key_config";

	private static final int[] STATE_SECURED = { R.attr.state_encrypted };
	private static final int[] STATE_NONE = {};

	/**
	 * These values are matched in string arrays -- changes must be kept in sync
	 */
	static final int SECURITY_NONE = 0;
	static final int SECURITY_WEP = 1;
	static final int SECURITY_PSK = 2;
	static final int SECURITY_EAP = 3;

	enum PskType
	{
		UNKNOWN, WPA, WPA2, WPA_WPA2
	}

	public String ssid;
	public String bssid;
	public int security;
	public int networkId;
	public boolean wpsAvailable = false;

	PskType pskType = PskType.UNKNOWN;

	private ProxyConfiguration mPConfig;
	/* package */ScanResult mScanResult;

	private int mRssi;
	private WifiInfo mInfo;
	private DetailedState mState;

	static int getSecurity(WifiConfiguration config)
	{
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK))
		{
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X))
		{
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	private static int getSecurity(ScanResult result)
	{
		if (result.capabilities.contains("WEP"))
		{
			return SECURITY_WEP;
		}
		else if (result.capabilities.contains("PSK"))
		{
			return SECURITY_PSK;
		}
		else if (result.capabilities.contains("EAP"))
		{
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	public String getSecurityString(boolean concise)
	{
		Context context = getContext();
		switch (security)
		{
			case SECURITY_EAP:
				return concise ? context.getString(R.string.wifi_security_short_eap) : context.getString(R.string.wifi_security_eap);
			case SECURITY_PSK:
				switch (pskType)
				{
					case WPA:
						return concise ? context.getString(R.string.wifi_security_short_wpa) : context.getString(R.string.wifi_security_wpa);
					case WPA2:
						return concise ? context.getString(R.string.wifi_security_short_wpa2) : context.getString(R.string.wifi_security_wpa2);
					case WPA_WPA2:
						return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) : context.getString(R.string.wifi_security_wpa_wpa2);
					case UNKNOWN:
					default:
						return concise ? context.getString(R.string.wifi_security_short_psk_generic) : context.getString(R.string.wifi_security_psk_generic);
				}
			case SECURITY_WEP:
				return concise ? context.getString(R.string.wifi_security_short_wep) : context.getString(R.string.wifi_security_wep);
			case SECURITY_NONE:
			default:
				return concise ? "" : context.getString(R.string.wifi_security_none);
		}
	}

	private static PskType getPskType(ScanResult result)
	{
		boolean wpa = result.capabilities.contains("WPA-PSK");
		boolean wpa2 = result.capabilities.contains("WPA2-PSK");
		if (wpa2 && wpa)
		{
			return PskType.WPA_WPA2;
		}
		else if (wpa2)
		{
			return PskType.WPA2;
		}
		else if (wpa)
		{
			return PskType.WPA;
		}
		else
		{
			Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
			return PskType.UNKNOWN;
		}
	}

	public AccessPointPreference(Context context, ProxyConfiguration config)
	{
		super(context);
		setWidgetLayoutResource(R.layout.pref_widget_wifi_signal);
		loadConfig(config);
		refresh();
	}

//	public AccessPoint(Context context, Bundle savedState)
//	{
//		super(context);
//		setWidgetLayoutResource(R.layout.pref_widget_wifi_signal);
//
//		mPConfig = savedState.getParcelable(KEY_CONFIG);
//		if (mPConfig != null)
//		{
//			loadConfig(mPConfig);
//		}
//		mScanResult = (ScanResult) savedState.getParcelable(KEY_SCANRESULT);
//		if (mScanResult != null)
//		{
//			loadResult(mScanResult);
//		}
//		mInfo = (WifiInfo) savedState.getParcelable(KEY_WIFIINFO);
//		if (savedState.containsKey(KEY_DETAILEDSTATE))
//		{
//			mState = DetailedState.valueOf(savedState.getString(KEY_DETAILEDSTATE));
//		}
//		update(mInfo, mState);
//	}

//	public void saveWifiState(Bundle savedState)
//	{
//		savedState.putParcelable(KEY_CONFIG, mPConfig);
//		savedState.putParcelable(KEY_SCANRESULT, mScanResult);
//		savedState.putParcelable(KEY_WIFIINFO, mInfo);
//		if (mState != null)
//		{
//			savedState.putString(KEY_DETAILEDSTATE, mState.toString());
//		}
//	}

	private void loadConfig(ProxyConfiguration pconfig)
	{
		ssid = (pconfig.wifiConfiguration.SSID == null ? "" : removeDoubleQuotes(pconfig.wifiConfiguration.SSID));
		bssid = pconfig.wifiConfiguration.BSSID;
		security = getSecurity(pconfig.wifiConfiguration);
		networkId = pconfig.wifiConfiguration.networkId;
		mRssi = Integer.MAX_VALUE;
		mPConfig = pconfig;
	}

	private void loadResult(ScanResult result)
	{
		ssid = result.SSID;
		bssid = result.BSSID;
		security = getSecurity(result);
		wpsAvailable = security != SECURITY_EAP && result.capabilities.contains("WPS");
		if (security == SECURITY_PSK)
			pskType = getPskType(result);
		networkId = -1;
		mRssi = result.level;
		mScanResult = result;
	}

	@Override
	protected void onBindView(View view)
	{
		super.onBindView(view);
		ImageView signal = (ImageView) view.findViewById(R.id.signal);
		if (mRssi == Integer.MAX_VALUE)
		{
			signal.setImageDrawable(null);
		}
		else
		{
			signal.setImageLevel(getLevel());
			signal.setImageResource(R.drawable.wifi_signal);
			signal.setImageState((security != SECURITY_NONE) ? STATE_SECURED : STATE_NONE, true);
		}
	}

	@Override
	public int compareTo(Preference preference)
	{
		if (!(preference instanceof AccessPointPreference))
		{
			return 1;
		}
		AccessPointPreference other = (AccessPointPreference) preference;
		// Active one goes first.
		if (mInfo != other.mInfo)
		{
			return (mInfo != null) ? -1 : 1;
		}
		// Reachable one goes before unreachable one.
		if ((mRssi ^ other.mRssi) < 0)
		{
			return (mRssi != Integer.MAX_VALUE) ? -1 : 1;
		}
		// Configured one goes before unconfigured one.
		if ((networkId ^ other.networkId) < 0)
		{
			return (networkId != -1) ? -1 : 1;
		}
		// Sort by signal strength.
		int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
		if (difference != 0)
		{
			return difference;
		}
		// Sort by ssid.
		return ssid.compareToIgnoreCase(other.ssid);
	}

	public boolean update(ScanResult result)
	{
		if (ssid.equals(result.SSID) && security == getSecurity(result))
		{
			if (WifiManager.compareSignalLevel(result.level, mRssi) > 0)
			{
				int oldLevel = getLevel();
				mRssi = result.level;
				if (getLevel() != oldLevel)
				{
					notifyChanged();
				}
			}
			// This flag only comes from scans, is not easily saved in config
			if (security == SECURITY_PSK)
			{
				pskType = getPskType(result);
			}
			refresh();
			return true;
		}
		return false;
	}

	public void update(WifiInfo info, DetailedState state)
	{
		boolean reorder = false;
		if (info != null && networkId != AccessPointPreference.INVALID_NETWORK_ID && networkId == info.getNetworkId())
		{
			reorder = (mInfo == null);
			mRssi = info.getRssi();
			mInfo = info;
			mState = state;
			refresh();
		}
		else if (mInfo != null)
		{
			reorder = true;
			mInfo = null;
			mState = null;
			refresh();
		}
		if (reorder)
		{
			notifyHierarchyChanged();
		}
	}

	int getLevel()
	{
		if (mRssi == Integer.MAX_VALUE)
		{
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, 4);
	}

	ProxyConfiguration getConfig()
	{
		return mPConfig;
	}

	WifiInfo getInfo()
	{
		return mInfo;
	}

	DetailedState getState()
	{
		return mState;
	}

	static String removeDoubleQuotes(String string)
	{
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"'))
		{
			return string.substring(1, length - 1);
		}
		return string;
	}

	static String convertToQuotedString(String string)
	{
		return "\"" + string + "\"";
	}

	/** Updates the title and summary; may indirectly call notifyChanged() */
	private void refresh()
	{
		setTitle(ssid);

		Context context = getContext();
		if (mState != null)
		{ // This is the active connection
			setSummary(com.shouldit.proxy.lib.WiFiSummary.get(context, mState));
		}
		else if (mRssi == Integer.MAX_VALUE)
		{ // Wifi out of range
			setSummary(context.getString(R.string.wifi_not_in_range));
		}
		else if (mPConfig != null && mPConfig.wifiConfiguration != null && mPConfig.wifiConfiguration.status == WifiConfiguration.Status.DISABLED)
		{
			Log.e(TAG,"Add disable reason!");
//			switch (mConfig.disableReason)
//			{
//				case AccessPoint.DISABLED_AUTH_FAILURE:
//					setSummary(context.getString(R.string.wifi_disabled_password_failure));
//					break;
//				case AccessPoint.DISABLED_DHCP_FAILURE:
//				case AccessPoint.DISABLED_DNS_FAILURE:
//					setSummary(context.getString(R.string.wifi_disabled_network_failure));
//					break;
//				case AccessPoint.DISABLED_UNKNOWN_REASON:
//					setSummary(context.getString(R.string.wifi_disabled_generic));
//			}
		}
		else
		{ 
			// In range, not disabled.
			StringBuilder summary = new StringBuilder();
			if (mPConfig != null && mPConfig.wifiConfiguration != null)
			{ 
				// Is saved network
				summary.append(context.getString(R.string.wifi_remembered));
			}

			if (security != SECURITY_NONE)
			{
				String securityStrFormat;
				if (summary.length() == 0)
				{
					securityStrFormat = context.getString(R.string.wifi_secured_first_item);
				}
				else
				{
					securityStrFormat = context.getString(R.string.wifi_secured_second_item);
				}
				summary.append(String.format(securityStrFormat, getSecurityString(true)));
			}

			if (mPConfig == null && mPConfig.wifiConfiguration == null && wpsAvailable)
			{ // Only list WPS available for unsaved networks
				if (summary.length() == 0)
				{
					summary.append(context.getString(R.string.wifi_wps_available_first_item));
				}
				else
				{
					summary.append(context.getString(R.string.wifi_wps_available_second_item));
				}
			}
			setSummary(summary.toString());
		}
	}

//	/**
//	 * Generate and save a default wifiConfiguration with common values. Can
//	 * only be called for unsecured networks.
//	 * 
//	 * @hide
//	 */
//	protected void generateOpenNetworkConfig()
//	{
//		if (security != SECURITY_NONE)
//			throw new IllegalStateException();
//		if (mPConfig != null)
//			return;
//		mConfig = new WifiConfiguration();
//		mConfig.SSID = AccessPoint.convertToQuotedString(ssid);
//		mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
//	}
}
