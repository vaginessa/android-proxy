package com.lechucksoftware.proxy.proxysettings.utils;

import android.os.Handler;
import android.os.Message;

import be.shouldit.proxy.lib.APL;

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
//            Timber.d("Resume Wi-Fi scanner");
            sendEmptyMessage(0);
        }
    }

    public void forceScan()
    {
//        Timber.d("Force Wi-Fi scanner");
        removeMessages(0);
        sendEmptyMessage(0);
    }

    public void pause()
    {
//        Timber.d("Pause Wi-Fi scanner");
        mRetry = 0;
        removeMessages(0);
    }

    @Override
    public void handleMessage(Message message)
    {
        if (APL.getWifiManager().isWifiEnabled())
        {
//            Timber.d("Calling Wi-Fi scanner");

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
//            Timber.d("Wi-Fi scanner disabled");
        }

        sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
    }
}
