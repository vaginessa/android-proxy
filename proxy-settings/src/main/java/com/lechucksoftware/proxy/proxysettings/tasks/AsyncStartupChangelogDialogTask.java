package com.lechucksoftware.proxy.proxysettings.tasks;

import android.os.AsyncTask;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WiFiApListActivity;
import com.lechucksoftware.proxy.proxysettings.utils.WhatsNewDialog;

/**
 * Created by Marco on 29/11/13.
 */
public class AsyncStartupChangelogDialogTask extends AsyncTask<Void, Void, Boolean>
{
    WhatsNewDialog wnd = null;
    WiFiApListActivity wiFiApListActivity;

    public AsyncStartupChangelogDialogTask(WiFiApListActivity activity)
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
