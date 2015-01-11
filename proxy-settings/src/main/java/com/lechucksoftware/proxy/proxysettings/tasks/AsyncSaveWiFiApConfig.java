package com.lechucksoftware.proxy.proxysettings.tasks;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.WiFiApDetailFragment;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import be.shouldit.proxy.lib.WiFiAPConfig;
import timber.log.Timber;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncSaveWiFiApConfig extends AsyncTask<Void, String, Boolean>
{
    private final Fragment callerFragment;
    private final WiFiAPConfig configuration;
    private static final String TAG = AsyncSaveWiFiApConfig.class.getSimpleName();

    public AsyncSaveWiFiApConfig(Fragment caller, WiFiAPConfig conf)
    {
        callerFragment = caller;
        configuration = conf;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);

        if (result)
        {
            WiFiApDetailFragment wiFiApDetailFragment = (WiFiApDetailFragment) callerFragment;
            wiFiApDetailFragment.refreshUI();

//        Toast.makeText(callerFragment.getActivity(), String.format("Updated %s Wi-Fi access point configuration", result.toString()), Toast.LENGTH_SHORT).show();

            // Calling refresh intent only after save of all configuration
//            App.getTraceUtils().i(TAG, "Sending broadcast intent: " + Intents.WIFI_AP_UPDATED);
//            Intent intent = new Intent(Intents.WIFI_AP_UPDATED);
//            intent.putExtra(Intents.UPDATED_WIFI, configuration.getAPLNetworkId());
//            APL.getContext().sendBroadcast(intent);
        }
        else
        {
            UIUtils.showError(callerFragment.getActivity(), R.string.exception_apl_writeconfig_error_message);
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        App.getTraceUtils().startTrace(TAG, "saveConfiguration", Log.DEBUG);

        try
        {
            if (configuration != null)
            {
//                App.getInstance().wifiActionEnabled = false;
                configuration.writeConfigurationToDevice();
//                App.getInstance().wifiActionEnabled = true;
            }

            App.getTraceUtils().stopTrace(TAG, "saveConfiguration", Log.DEBUG);
            return true;
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception saving WifiAPConfig in doInBackground");
            return false;
        }
    }
}
