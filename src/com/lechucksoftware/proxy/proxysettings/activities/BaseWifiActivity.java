package com.lechucksoftware.proxy.proxysettings.activities;

import com.lechucksoftware.proxy.proxysettings.utils.WifiScannerHandler;

/**
 * Created by marco on 07/11/13.
 */
public class BaseWifiActivity extends BaseActivity
{
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
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getWifiScanner().pause();
        mScanner = null;
    }
}
