package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncDeleteProxy extends AsyncTask<Void, String, Boolean>
{
    private static final String TAG = AsyncDeleteProxy.class.getSimpleName();

    private ProxyEntity proxyEntity;
    private Context context;

    public AsyncDeleteProxy(Fragment caller, ProxyEntity proxy)
    {
        if (caller != null)
        {
            context = caller.getActivity().getBaseContext();
            proxyEntity = proxy;
        }
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);

        if (result)
        {
            Toast.makeText(context, context.getString(R.string.proxy_deleted), Toast.LENGTH_SHORT).show();
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
                App.getDBManager().deleteProxy(proxyEntity.getId());
            }

            App.getTraceUtils().stopTrace(TAG, "saveProxy", Log.DEBUG);
            return true;
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
            return false;
        }
    }
}
