package com.lechucksoftware.proxy.proxysettings.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.UUID;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncUpdateApplicationStatistics extends AsyncTask<Void, UUID, Integer>
{
    private final Context callerContext;
    private static final String TAG = AsyncUpdateApplicationStatistics.class.getSimpleName();

    public AsyncUpdateApplicationStatistics(Context context)
    {
        callerContext = context;
    }

    @Override
    protected void onPostExecute(Integer updatedWiFiAP)
    {
        super.onPostExecute(updatedWiFiAP);
    }

    @Override
    protected Integer doInBackground(Void... voids)
    {
        return 0;
    }
}
