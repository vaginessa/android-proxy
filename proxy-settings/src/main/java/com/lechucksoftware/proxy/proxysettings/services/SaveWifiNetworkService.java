package com.lechucksoftware.proxy.proxysettings.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.utils.SaveResult;
import timber.log.Timber;

/**
 * Created by Marco on 09/03/14.
 */
public class SaveWifiNetworkService extends IntentService
{
    public static final String CALLER_INTENT = "CallerIntent";
    public static String TAG = SaveWifiNetworkService.class.getSimpleName();
    private boolean isHandling = false;
    private static SaveWifiNetworkService instance;

    public SaveWifiNetworkService()
    {
        super("SaveWifiNetworkService");
    }

    public static SaveWifiNetworkService getInstance()
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

        SaveResult result = null;

        Bundle extras = intent.getExtras();
        if (extras.containsKey(Constants.WIFI_AP_NETWORK_ARG))
        {
            WiFiApConfig wiFiApConfig = extras.getParcelable(Constants.WIFI_AP_NETWORK_ARG);

            try
            {
                result = APL.writeWifiAPConfig(wiFiApConfig, 50, 5000); // 50 attempts, 5 seconds
                if (result != null)
                    Timber.i("Configuration %s in %d attempts after %d ms", result.status.toString(), result.attempts, result.elapsedTime);
            }
            catch (Exception e)
            {
                Timber.e(e,"Exception saving Wi-Fi network configuration to device");

            }
        }

        isHandling = false;
    }
}
