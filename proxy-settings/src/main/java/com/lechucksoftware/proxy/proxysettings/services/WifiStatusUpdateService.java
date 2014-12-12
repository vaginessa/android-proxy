package com.lechucksoftware.proxy.proxysettings.services;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;

import java.util.List;

import be.shouldit.proxy.lib.APL;
import timber.log.Timber;

/**
 * Created by Marco on 09/03/14.
 */
public class WifiStatusUpdateService extends EnhancedIntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = WifiStatusUpdateService.class.getSimpleName();
    private boolean isHandling = false;
    private static WifiStatusUpdateService instance;

    public WifiStatusUpdateService()
    {
        super("WifiStatusUpdateService");
    }

    public static WifiStatusUpdateService getInstance()
    {
        return instance;
    }

    public boolean isHandlingIntent()
    {
        return isHandling;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        instance = this;
        isHandling = true;

        updatedWifiStatus();

        isHandling = false;
    }

    private void updatedWifiStatus()
    {
        App.getTraceUtils().startTrace(TAG, "updateAfterScanResults", Log.DEBUG);
        WifiInfo currentWifiInfo = APL.getWifiManager().getConnectionInfo();

        // update current WifiInfo information for each WifiApConfig
        App.getWifiNetworksManager().updateCurrentWifiInfo(currentWifiInfo);

        // TODO: getScanResults() seems to Trigger a query to LocationManager
        // Add a possibility to disable the behaviour in order to avoid problems with KitKat AppOps
        List<ScanResult> scanResults = APL.getWifiManager().getScanResults();
        if (scanResults != null)
        {
            App.getWifiNetworksManager().updateWifiConfigWithScanResults(scanResults);
        }

        Timber.d("Sending broadcast intent " + Intents.PROXY_REFRESH_UI);
        Intent intent = new Intent(Intents.PROXY_REFRESH_UI);
        getApplicationContext().sendBroadcast(intent);

//        Map<APLNetworkId,WifiConfiguration> wiFiAPConfigMap = APL.getConfiguredNetworks();
//        if (wiFiAPConfigMap.size() != App.getWifiNetworksManager().getSortedWifiApConfigsList().size())
//        {
//            ProxyChangeReceiver.callWifiSyncService(this, intent);
//        }

        App.getTraceUtils().stopTrace(TAG, "updateAfterScanResults", Log.DEBUG);
    }
}
