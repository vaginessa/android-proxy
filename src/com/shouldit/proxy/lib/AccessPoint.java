package com.shouldit.proxy.lib;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AccessPoint implements Comparable<AccessPoint>
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

	public static final int[] STATE_SECURED = { R.attr.state_encrypted };
	public static final int[] STATE_NONE = {};

	public String ssid;
	public String bssid;
	public APLConstants.SecurityType security;
	public int networkId;
	public boolean wpsAvailable = false;

	public APLConstants.PskType pskType = APLConstants.PskType.UNKNOWN;

	public WifiConfiguration wifiConfig;
	/* package */ScanResult mScanResult;

	private int mRssi;
//	private WifiInfo mInfo;

	public AccessPoint(WifiConfiguration config)
	{
		loadConfig(config);
	}

	private void loadConfig(WifiConfiguration config)
	{
		ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
		bssid = config.BSSID;
		security = ProxyUtils.getSecurity(config);
		networkId = config.networkId;
		mRssi = Integer.MAX_VALUE;
//        mInfo = info;
		wifiConfig = config;
	}

	@Override
	public int compareTo(AccessPoint ap)
	{
		if (!(ap instanceof AccessPoint))
		{
			return 1;
		}
		
		AccessPoint other = (AccessPoint) ap;

//		// Active one goes first: Only check different SSID
//	    if (ssid.compareTo(other.ssid) != 0 && mInfo != other.mInfo)
//		{
//			return (mInfo != null) ? -1 : 1;
//		}
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
//		int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
//		if (difference != 0)
//		{
//			return difference;
//		}
		// Sort by ssid.
		return ssid.compareToIgnoreCase(other.ssid);
	}

    public void clearScanStatus()
    {
        mRssi = Integer.MAX_VALUE;
        pskType = APLConstants.PskType.UNKNOWN;
    }

	public boolean update(ScanResult result)
	{
		if (ssid.equals(result.SSID) && security == ProxyUtils.getSecurity(result))
		{
			if (WifiManager.compareSignalLevel(result.level, mRssi) > 0)
			{
				mRssi = result.level;
			}
			// This flag only comes from scans, is not easily saved in config
			if (security == APLConstants.SecurityType.SECURITY_PSK)
			{
				pskType = ProxyUtils.getPskType(result);
			}
			return true;
		}
		return false;
	}

	public int getLevel()
	{
		if (mRssi == Integer.MAX_VALUE)
		{
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, 4);
	}

//	WifiInfo getInfo()
//	{
//		return mInfo;
//	}

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
}
