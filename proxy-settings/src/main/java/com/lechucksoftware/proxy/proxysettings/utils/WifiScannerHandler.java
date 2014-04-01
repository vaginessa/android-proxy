package com.lechucksoftware.proxy.proxysettings.utils;

import android.os.Handler;
import android.os.Message;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.shouldit.proxy.lib.APL;

/**
 * Created by Marco on 29/11/13.
 */
public class WifiScannerHandler extends Handler
{
    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

    private static final String TAG = WifiScannerHandler.class.getSimpleName();
    private int mRetry = 0;

    public void resume()
    {
        if (!hasMessages(0))
        {
            ApplicationGlobals.getLogger().d(TAG, "Resume Wi-Fi scanner");
            sendEmptyMessage(0);
        }
    }

    public void forceScan()
    {
        ApplicationGlobals.getLogger().d(TAG, "Force Wi-Fi scanner");
        removeMessages(0);
        sendEmptyMessage(0);
    }

    public void pause()
    {
        ApplicationGlobals.getLogger().d(TAG, "Pause Wi-Fi scanner");
        mRetry = 0;
        removeMessages(0);
    }

    @Override
    public void handleMessage(Message message)
    {
        if (ApplicationGlobals.getInstance().wifiActionEnabled)
        {
            ApplicationGlobals.getLogger().d(TAG, "Calling Wi-Fi scanner");

            if (APL.getWifiManager().startScan())
            {
                mRetry = 0;
            }
            else if (++mRetry >= 3)
            {
                mRetry = 0;
                return;
            }
        }
        else
        {
            ApplicationGlobals.getLogger().d(TAG, "Wi-Fi scanner disabled");
        }

        sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
    }
}
