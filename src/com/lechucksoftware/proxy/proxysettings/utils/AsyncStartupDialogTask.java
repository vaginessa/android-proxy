package com.lechucksoftware.proxy.proxysettings.utils;

import android.os.AsyncTask;
import com.lechucksoftware.proxy.proxysettings.activities.MainActivity;
import com.shouldit.proxy.lib.log.LogWrapper;

/**
 * Created by Marco on 29/11/13.
 */
public class AsyncStartupDialogTask extends AsyncTask<Void, Void, Boolean>
{
    WhatsNewDialog wnd = null;
    MainActivity mainActivity;

    public AsyncStartupDialogTask(MainActivity activity)
    {
        mainActivity = activity;
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
        wnd = new WhatsNewDialog(mainActivity);
        return wnd.isToShow();
    }
}
