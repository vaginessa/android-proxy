package com.lechucksoftware.proxy.proxysettings.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.utils.WifiScannerHandler;
import com.shouldit.proxy.lib.APLIntents;
import com.shouldit.proxy.lib.log.LogWrapper;

/**
 * Created by marco on 07/11/13.
 */
public class BaseWifiActivity extends BaseActivity
{
    private static final String TAG = BaseWifiActivity.class.getSimpleName();
    private WifiScannerHandler mScanner;

    protected WifiScannerHandler getWifiScanner()
    {
        if (mScanner == null)
            mScanner = new WifiScannerHandler();

        return mScanner;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getWifiScanner().resume();

        // Start register the status receivers
        IntentFilter ifilt = new IntentFilter();

        ifilt.addAction(Intents.WIFI_AP_UPDATED);
        ifilt.addAction(APLIntents.APL_UPDATED_PROXY_STATUS_CHECK);
        ifilt.addAction(Intents.PROXY_REFRESH_UI);
        registerReceiver(changeStatusReceiver, ifilt);

        refreshUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getWifiScanner().pause();
        mScanner = null;

        // Stop the registered status receivers
        unregisterReceiver(changeStatusReceiver);
    }

    private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            LogWrapper.logIntent(TAG, intent, Log.DEBUG, true);

            if (action.equals(Intents.WIFI_AP_UPDATED))
            {
                if (ApplicationGlobals.getInstance().wifiActionEnabled)
                {
                    LogWrapper.d(TAG, "Received broadcast for proxy configuration written on device -> RefreshUI");
                    refreshUI();
                }
            }
            else if (action.equals(APLIntents.APL_UPDATED_PROXY_STATUS_CHECK))
            {
                LogWrapper.d(TAG, "Received broadcast for partial update on status of proxy configuration - RefreshUI");
                refreshUI();
            }
            else if (action.equals(Intents.PROXY_REFRESH_UI))
            {
                LogWrapper.d(TAG, "Received broadcast for update the Proxy Settings UI - RefreshUI");
                refreshUI();
            }
            else
            {
                LogWrapper.e(TAG, "Received intent not handled: " + intent.getAction());
            }
        }
    };
}
