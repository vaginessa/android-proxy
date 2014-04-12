package com.lechucksoftware.proxy.proxysettings.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WiFiApListActivity;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

/**
 * Created by Marco on 29/11/13.
 */
public class AsyncStartupRateTask extends AsyncTask<Void, Void, Boolean>
{
    private final WiFiApListActivity wiFiApListActivity;

    public AsyncStartupRateTask(WiFiApListActivity activity)
    {
        wiFiApListActivity = activity;
    }

    @Override
    protected void onPostExecute(Boolean showDialog)
    {
        super.onPostExecute(showDialog);

        if (showDialog)
        {
//            RateApplicationAlertDialog dialog = RateApplicationAlertDialog.newInstance(action);
//            dialog.show(wiFiApListActivity.getFragmentManager(), "AsyncStartupRateTask");
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        return showAppRate();
    }

    public boolean showAppRate()
    {
        SharedPreferences prefs = wiFiApListActivity.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
        if (prefs.getBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, false))
        {
            return false;
        }

        ApplicationStatistics statistics = ApplicationStatistics.getInstallationDetails(wiFiApListActivity.getApplicationContext());

        // Wait at least N days before opening
        if (statistics.LaunchCount >= Constants.APPRATE_LAUNCHES_UNTIL_PROMPT)
        {
            boolean result = Utils.ElapsedNDays(statistics.LaunhcFirstDate, Constants.APPRATE_DAYS_UNTIL_PROMPT);
        }

        return false;
    }

}
