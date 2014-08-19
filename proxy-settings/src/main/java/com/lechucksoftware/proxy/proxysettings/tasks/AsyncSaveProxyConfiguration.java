package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.APL;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncSaveProxyConfiguration extends AsyncTask<Void, String, Boolean>
{
    private final Fragment callerFragment;
    private final WiFiAPConfig configuration;
    private static final String TAG = AsyncSaveProxyConfiguration.class.getSimpleName();

    public AsyncSaveProxyConfiguration(Fragment caller, WiFiAPConfig conf)
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

//        callerFragment.progress.setVisibility(View.GONE);
//        Toast.makeText(callerFragment.getActivity(), String.format("Updated %s Wi-Fi access point configuration", result.toString()), Toast.LENGTH_SHORT).show();

            // Calling refresh intent only after save of all configuration
            App.getLogger().i(TAG, "Sending broadcast intent: " + Intents.WIFI_AP_UPDATED);
            Intent intent = new Intent(Intents.WIFI_AP_UPDATED);
            intent.putExtra(Intents.UPDATED_WIFI, configuration.getAPLNetworkId());
            APL.getContext().sendBroadcast(intent);
        }
        else
        {
            UIUtils.showError(callerFragment.getActivity(), R.string.exception_apl_writeconfig_error_message);
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        App.getLogger().startTrace(TAG, "saveConfiguration", Log.DEBUG);

        try
        {
            if (configuration != null)
            {
//                App.getInstance().wifiActionEnabled = false;
                configuration.writeConfigurationToDevice();
//                App.getInstance().wifiActionEnabled = true;
            }

            App.getLogger().stopTrace(TAG, "saveConfiguration", Log.DEBUG);
            return true;
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
            return false;
        }
    }
}
