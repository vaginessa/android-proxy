package com.lechucksoftware.proxy.proxysettings.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WiFiApListActivity;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;

import java.util.Calendar;

/**
 * Created by Marco on 29/11/13.
 */
public class AsyncStartupBetaTestTask extends AsyncTask<Void, Void, Boolean>
{
    WiFiApListActivity wiFiApListActivity;

    public AsyncStartupBetaTestTask(WiFiApListActivity activity)
    {
        wiFiApListActivity = activity;
    }

    @Override
    protected void onPostExecute(Boolean showDialog)
    {
        super.onPostExecute(showDialog);

        if (showDialog)
        {
//            BetaTestApplicationAlertDialog dialog = BetaTestApplicationAlertDialog.newInstance(action);
//            dialog.show(wiFiApListActivity.getFragmentManager(), "AsyncStartupBetaTestTask");
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        return showAppBetaTest();
    }

    public boolean showAppBetaTest()
    {
        SharedPreferences prefs = wiFiApListActivity.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
        if (prefs.getBoolean(Constants.PREFERENCES_BETATEST_DONT_SHOW_AGAIN, false))
        {
            return false;
        }

        ApplicationStatistics statistics = ApplicationStatistics.getInstallationDetails(wiFiApListActivity.getApplicationContext());

        // Wait at least N days before opening
        if (statistics.LaunchCount >= Constants.BETATEST_LAUNCHES_UNTIL_PROMPT)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(statistics.LaunhcFirstDate);
            c.add(Calendar.DATE, Constants.BETATEST_DAYS_UNTIL_PROMPT);

            if (System.currentTimeMillis() >= c.getTime().getTime())
            {
                return true;
            }
        }

        return false;
    }

}
