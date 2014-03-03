package com.lechucksoftware.proxy.proxysettings.utils;

import android.os.AsyncTask;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WiFiApListActivity;

/**
 * Created by Marco on 29/11/13.
 */
public class AsyncStartupDialogTask extends AsyncTask<Void, Void, Boolean>
{
    WhatsNewDialog wnd = null;
    WiFiApListActivity wiFiApListActivity;

    public AsyncStartupDialogTask(WiFiApListActivity activity)
    {
        wiFiApListActivity = activity;
    }

    @Override
    protected void onPostExecute(Boolean showDialog)
    {
        super.onPostExecute(showDialog);

        if (wnd != null && showDialog)
        {
            wnd.show();
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        wnd = new WhatsNewDialog(wiFiApListActivity);
        return wnd.isToShow();
    }
}
