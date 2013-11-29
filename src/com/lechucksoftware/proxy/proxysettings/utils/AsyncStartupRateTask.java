package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.lechucksoftware.proxy.proxysettings.activities.MainActivity;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.dialogs.RateApplicationAlertDialog;

import java.util.Calendar;

/**
 * Created by Marco on 29/11/13.
 */
public class AsyncStartupRateTask extends AsyncTask<Void, Void, Boolean>
{
    private final MainActivity mainActivity;

    public AsyncStartupRateTask(MainActivity activity)
    {
        mainActivity = activity;
    }

    @Override
    protected void onPostExecute(Boolean showDialog)
    {
        super.onPostExecute(showDialog);

        if (showDialog)
        {
            RateApplicationAlertDialog dialog = RateApplicationAlertDialog.newInstance();
            dialog.show(mainActivity.getFragmentManager(), "AsyncStartupRateTask");
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        return showAppRate();
    }

    public boolean showAppRate()
    {
        SharedPreferences prefs = mainActivity.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        if (prefs.getBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, false))
        {
            return false;
        }

        InstallationStatistics statistics = InstallationStatistics.GetInstallationDetails(mainActivity.getApplicationContext());

        // Wait at least N days before opening
        if (statistics.launchCount >= Constants.APPRATE_LAUNCHES_UNTIL_PROMPT)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(statistics.launhcFirstDate);
            c.add(Calendar.DATE, Constants.APPRATE_DAYS_UNTIL_PROMPT);

            if (System.currentTimeMillis() >= c.getTime().getTime())
            {
                return true;
            }
        }

        return false;
    }
}
