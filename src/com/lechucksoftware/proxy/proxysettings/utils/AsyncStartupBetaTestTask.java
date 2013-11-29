package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.lechucksoftware.proxy.proxysettings.activities.MainActivity;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.dialogs.BetaTestApplicationAlertDialog;

import java.util.Calendar;

/**
 * Created by Marco on 29/11/13.
 */
public class AsyncStartupBetaTestTask extends AsyncTask<Void, Void, Boolean>
{
    MainActivity mainActivity;

    public AsyncStartupBetaTestTask(MainActivity activity)
    {
        mainActivity = activity;
    }

    @Override
    protected void onPostExecute(Boolean showDialog)
    {
        super.onPostExecute(showDialog);

        if (showDialog)
        {
            BetaTestApplicationAlertDialog dialog = BetaTestApplicationAlertDialog.newInstance();
            dialog.show(mainActivity.getFragmentManager(), "AsyncStartupBetaTestTask");
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        return showAppBetaTest();
    }

    public boolean showAppBetaTest()
    {
        SharedPreferences prefs = mainActivity.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        if (prefs.getBoolean(Constants.PREFERENCES_BETATEST_DONT_SHOW_AGAIN, false))
        {
            return false;
        }

        InstallationStatistics statistics = InstallationStatistics.GetInstallationDetails(mainActivity.getApplicationContext());

        // Wait at least N days before opening
        if (statistics.launchCount >= Constants.BETATEST_LAUNCHES_UNTIL_PROMPT)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(statistics.launhcFirstDate);
            c.add(Calendar.DATE, Constants.BETATEST_DAYS_UNTIL_PROMPT);

            if (System.currentTimeMillis() >= c.getTime().getTime())
            {
                return true;
            }
        }

        return false;
    }

}
