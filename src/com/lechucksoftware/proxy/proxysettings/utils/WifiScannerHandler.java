package com.lechucksoftware.proxy.proxysettings.utils;

import android.os.Handler;
import android.os.Message;
import com.lechucksoftware.proxy.proxysettings.activities.MainActivity;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.log.LogWrapper;

/**
 * Created by Marco on 29/11/13.
 */
public class WifiScannerHandler extends Handler
{
    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

    private static final String TAG = WifiScannerHandler.class.getSimpleName();
    private final MainActivity mainActivity;
    private int mRetry = 0;

    public WifiScannerHandler(MainActivity activity)
    {
        mainActivity = activity;
    }

    public void resume()
    {
        if (!hasMessages(0))
        {
            LogWrapper.w(TAG, "Resume Wi-Fi scanner");
            sendEmptyMessage(0);
        }
    }

    public void forceScan()
    {
        LogWrapper.w(TAG, "Force Wi-Fi scanner");
        removeMessages(0);
        sendEmptyMessage(0);
    }

    public void pause()
    {
        LogWrapper.w(TAG, "Pause Wi-Fi scanner");
        mRetry = 0;
        removeMessages(0);
    }

    @Override
    public void handleMessage(Message message)
    {
        LogWrapper.w(TAG, "Calling Wi-Fi scanner");

        if (APL.getWifiManager().startScan())
        {
            mRetry = 0;
        }
        else if (++mRetry >= 3)
        {
            mRetry = 0;
            return;
        }

        sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
    }
}
