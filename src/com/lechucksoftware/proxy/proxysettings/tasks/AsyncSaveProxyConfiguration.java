package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncSaveProxyConfiguration extends AsyncTask<Void, String, Boolean>
{
    private final Fragment callerFragment;
    private final ProxyConfiguration configuration;
    private static final String TAG = AsyncSaveProxyConfiguration.class.getSimpleName();

    public AsyncSaveProxyConfiguration(Fragment caller, ProxyConfiguration conf)
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
            LogWrapper.i(TAG, "Sending broadcast intent: " + Intents.WIFI_AP_UPDATED);
            Intent intent = new Intent(Intents.WIFI_AP_UPDATED);
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
        LogWrapper.startTrace(TAG,"saveConfiguration", Log.DEBUG);

        try
        {
            if (configuration != null && configuration.isValidConfiguration())
            {
                ApplicationGlobals.getInstance().wifiActionEnabled = false;
                configuration.writeConfigurationToDevice();
                ApplicationGlobals.getInstance().wifiActionEnabled = true;
            }

            LogWrapper.stopTrace(TAG,"saveConfiguration", Log.DEBUG);
            return true;
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
            return false;
        }
    }
}
