package com.lechucksoftware.proxy.proxysettings.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import timber.log.Timber;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncSaveProxy extends AsyncTask<Void, String, Boolean>
{
    private Context context;
    private ProxyEntity proxyEntity;
    private PacEntity pacEntity;
    private static final String TAG = AsyncSaveProxy.class.getSimpleName();

    public AsyncSaveProxy(Fragment caller, ProxyEntity proxy)
    {
        if (caller != null)
        {
            context = caller.getActivity().getBaseContext();
            proxyEntity = proxy;
        }
    }

    public AsyncSaveProxy(Fragment caller, PacEntity pac)
    {
        if (caller != null)
        {
            context = caller.getActivity().getBaseContext();
            pacEntity = pac;
        }
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);

        if (result)
        {
            if (proxyEntity != null)
            {
                Toast.makeText(context, context.getString(R.string.proxy_saved), Toast.LENGTH_SHORT).show();
            }

            if (pacEntity != null)
            {
                Toast.makeText(context, context.getString(R.string.pac_saved), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            UIUtils.showError(context, R.string.exception_apl_writeconfig_error_message);
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        App.getTraceUtils().startTrace(TAG, "saveProxy", Log.DEBUG);

        try
        {
            if (proxyEntity != null)
            {
                App.getDBManager().upsertProxy(proxyEntity);
            }

            if (pacEntity != null)
            {
                App.getDBManager().upsertPac(pacEntity);
            }

            App.getTraceUtils().stopTrace(TAG, "saveProxy", Log.DEBUG);
            return true;
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception saving proxies in doInBackground");
            return false;
        }
    }
}
