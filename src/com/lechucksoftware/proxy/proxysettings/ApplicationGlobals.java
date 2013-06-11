package com.lechucksoftware.proxy.proxysettings;

import java.util.*;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import android.text.TextUtils;
import android.util.Log;
import com.bugsense.trace.BugSenseHandler;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.*;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;



public class ApplicationGlobals extends Application
{
	private static ApplicationGlobals mInstance;

    private Map<WifiNetworkId, ProxyConfiguration> configurations;
    private Map<WifiNetworkId, ProxyConfiguration> getConfigurations()
    {
        return configurations;
    }

    private String getConfigurationsString()
    {
        return TextUtils.join(", " ,getConfigurations().keySet());
    }

    // Wi-Fi networks available but still not configured into Android's Wi-Fi settings
    private Map<WifiNetworkId, ScanResult> notConfiguredWifi;
    public Map<WifiNetworkId, ScanResult> getNotConfiguredWifi()
    {
        return notConfiguredWifi;
    }

	public int timeout;
	private WifiManager mWifiManager;
	private ConnectivityManager mConnManager;
	private ProxyConfiguration currentConfiguration;

	private static final String TAG = "ApplicationGlobals";
    private static ProxyConfiguration selectedConfiguration;

    public static WifiManager getWifiManager()
	{
        if (getInstance().mWifiManager == null)
        {
            getInstance().mWifiManager = (WifiManager) getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

		return getInstance().mWifiManager;
	}

	public static ConnectivityManager getConnectivityManager()
	{
        if (getInstance().mConnManager == null)
        {
            getInstance().mConnManager = (ConnectivityManager) getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

		return getInstance().mConnManager;
	}

    public static void setSelectedConfiguration(ProxyConfiguration selectedConfiguration)
    {
        ApplicationGlobals.selectedConfiguration = selectedConfiguration;
    }

    public static ProxyConfiguration getSelectedConfiguration()
    {
        return ApplicationGlobals.selectedConfiguration;
    }

    @Override
	public void onCreate()
	{
		super.onCreate();

        mInstance = this;

		timeout = 10000; // Set default timeout value (10 seconds)

		configurations = Collections.synchronizedMap(new HashMap<WifiNetworkId, ProxyConfiguration>());
        notConfiguredWifi = Collections.synchronizedMap(new HashMap<WifiNetworkId, ScanResult>());
				
		Utils.SetupBugSense(getApplicationContext());
				
		LogWrapper.d(TAG, "Calling broadcast intent " + Constants.PROXY_SETTINGS_STARTED);
		sendBroadcast(new Intent(Constants.PROXY_SETTINGS_STARTED));
	}

	public static ApplicationGlobals getInstance()
	{
		if (mInstance == null)
        {
			BugSenseHandler.sendException(new Exception("Cannot find valid instance of ApplicationGlobals, trying to instanciate a new one"));
            mInstance = new ApplicationGlobals();
        }
		
		return mInstance;
	}

	public synchronized void updateProxyConfigurationList()
	{
        LogWrapper.startTrace(TAG,"updateProxyConfigurationList", Log.ASSERT);

        // Get information regarding current saved configuration
        Collection <WifiNetworkId> savedNetworks = getConfigurations().keySet();
        List <WifiNetworkId> savedSSIDNotMoreConfiguredList = new ArrayList<WifiNetworkId>();

        for (WifiNetworkId wifiNet: savedNetworks)
        {
            savedSSIDNotMoreConfiguredList.add(wifiNet);
        }

//        LogWrapper.d(TAG,"Saved configurations: " + TextUtils.join(", " , savedSSIDNotMoreConfiguredList));

		// Get latests information regarding configured AP
		List<ProxyConfiguration> updatedConfigurations = ProxySettings.getProxiesConfigurations(getInstance());

		for (ProxyConfiguration conf : updatedConfigurations)
		{
            if (getConfigurations().containsKey(conf.internalWifiNetworkId))
            {
                ProxyConfiguration originalConf = getConfigurations().get(conf.internalWifiNetworkId);
                originalConf.updateConfiguration(conf);
            }
            else
            {
                LogWrapper.d(TAG,"Adding to list new proxy configurations: " + conf.toShortString());
                getConfigurations().put(conf.internalWifiNetworkId, conf);
            }

            if (savedSSIDNotMoreConfiguredList.contains(conf.internalWifiNetworkId))
                savedSSIDNotMoreConfiguredList.remove(conf.internalWifiNetworkId);
		}

//        LogWrapper.d(TAG,"Updated configurations list: " + getConfigurationsString());
//        LogWrapper.d(TAG,"Configurations that need to be removed: " + TextUtils.join(", " , savedSSIDNotMoreConfiguredList));

        // Remove from current configuration the SSID that are not more configured into Android's Wi-Fi settings
        for (WifiNetworkId netId : savedSSIDNotMoreConfiguredList)
        {
            if (getConfigurations().containsKey(netId))
            {
                ProxyConfiguration removed = getConfigurations().remove(netId);
                LogWrapper.w(TAG,"Removing from Proxy Settings configuration a no more configured SSID: " + removed.toShortString());
            }
        }

//        LogWrapper.d(TAG,"Cleaned up configurations list: " + getConfigurationsString());

        // Update configurations with latest Wi-Fi scan results
        List<ScanResult> scanResults = getWifiManager().getScanResults();
		if (scanResults != null)
		{
            // clear all the configurations AP status
            for (ProxyConfiguration conf : getConfigurations().values())
            {
                conf.ap.clearScanStatus();
            }

			for (ScanResult res : scanResults)
			{
                LogWrapper.d(TAG, "Updating from scanresult: " + res.SSID + " level: " + res.level);
				String currSSID = ProxyUtils.cleanUpSSID(res.SSID);
                APLConstants.SecurityType security = ProxyUtils.getSecurity(res);
                WifiNetworkId currWifiNet = new WifiNetworkId(currSSID, security);

				if (getConfigurations().containsKey(currWifiNet))
				{
                    ProxyConfiguration conf = getConfigurations().get(currWifiNet);
                    if (conf != null && conf.ap != null)
                    {
                        conf.ap.update(res);
                    }
				}
                else
                {
                    if (getNotConfiguredWifi().containsKey(currWifiNet))
                    {
                        getNotConfiguredWifi().remove(currWifiNet);
                    }

                    getNotConfiguredWifi().put(currWifiNet, res);
                }
			}
		}

//        LogWrapper.d(TAG,"Not configured Wi-Fi: " + TextUtils.join(", " , getNotConfiguredWifi().values()));
        LogWrapper.d(TAG,"Final configurations list: " + getConfigurationsString());
        LogWrapper.stopTrace(TAG,"updateProxyConfigurationList", Log.ASSERT);
	}

	public ProxyConfiguration getCurrentConfiguration()
	{
		ProxyConfiguration conf = null;

		if (getInstance().mWifiManager != null && getInstance().mWifiManager.isWifiEnabled())
		{
			WifiInfo info = getInstance().mWifiManager.getConnectionInfo();
            if (info != null)
            {
                if (getConfigurations().isEmpty())
                    updateProxyConfigurationList();

                for (WifiConfiguration wifiConfig : getWifiManager().getConfiguredNetworks())
                {
                    if (wifiConfig.networkId == info.getNetworkId())
                    {
                        String SSID = ProxyUtils.cleanUpSSID(info.getSSID());
                        WifiNetworkId netId = new WifiNetworkId(SSID, ProxyUtils.getSecurity(wifiConfig));
                        if (getConfigurations().containsKey(netId))
                        {
                            conf = getConfigurations().get(netId);
                            break;
                        }
                    }
                }

                if (currentConfiguration == null || conf != null && currentConfiguration != null && currentConfiguration.compareTo(conf) != 0)
                {
                    getInstance().currentConfiguration = conf;
                }
            }
		}
		
		// Always return a not null configuration
		if (getInstance().currentConfiguration == null)
		{
            LogWrapper.w(TAG,"Cannot find a valid current configuration: creating an empty one");
            getInstance().currentConfiguration = new ProxyConfiguration(getInstance().getApplicationContext(), ProxySetting.NONE, null, null, null, null);
		}
		
		return getInstance().currentConfiguration;
	}

	public ProxyConfiguration getCachedConfiguration()
	{
		if (getInstance().currentConfiguration == null)
		{
			return getCurrentConfiguration();
		}
		
		return getInstance().currentConfiguration;
	}

	public List<ProxyConfiguration> getConfigurationsList()
	{
        if (getConfigurations().isEmpty())
            updateProxyConfigurationList();

        if (!getConfigurations().isEmpty())
        {
            Collection<ProxyConfiguration> values = getConfigurations().values();
            if (values != null && values.size() > 0)
            {
                ArrayList<ProxyConfiguration> results = new ArrayList<ProxyConfiguration>(values);
                Collections.sort(results);

                StringBuilder sb = new StringBuilder();
                for(ProxyConfiguration conf : results)
                {
                    sb.append(conf.ap.ssid + ",");
                }
                LogWrapper.d(TAG,"Sorted proxy configuration list: " + sb.toString());

                return results;
            }
        }

        return null;
	}
	
//	public ProxyConfiguration getConfiguration(String SSID)
//	{
//		String cleanSSID = ProxyUtils.cleanUpSSID(SSID);
//
//		if (getConfigurations().containsKey(cleanSSID))
//		{
//			return getConfigurations().get(cleanSSID);
//		}
//		else return null;
//	}



    public static void connectToAP(ProxyConfiguration conf)
    {
        if(getInstance().mWifiManager != null && getInstance().mWifiManager.isWifiEnabled())
        {
            if (conf != null && conf.ap != null && conf.ap.getLevel() > -1)
            {
                // Connect to AP only if it's available
                getInstance().mWifiManager.enableNetwork(conf.ap.networkId, true);
            }
        }
    }

    public static Boolean isConnected()
    {
        NetworkInfo ni = getCurrentNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Boolean isConnectedToWiFi()
    {
        NetworkInfo ni = getCurrentWiFiInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static NetworkInfo getCurrentNetworkInfo()
    {
        NetworkInfo ni = getConnectivityManager().getActiveNetworkInfo();
        return ni;
    }

    public static NetworkInfo getCurrentWiFiInfo()
    {
        NetworkInfo ni = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ni;
    }

	public static void startWifiScan()
	{
		if (getInstance().mWifiManager != null && getInstance().mWifiManager.isWifiEnabled())
		{
            getInstance().mWifiManager.startScan();
		}
	}


}
