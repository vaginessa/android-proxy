package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActions;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.BetaTestApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.RateApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

/**
 * Created by mpagliar on 04/04/2014.
 */
public class AsyncStartupActions  extends AsyncTask<Void, Void, StartupActions>
{
    private final Activity activity;

    public AsyncStartupActions(Activity a)
    {
        activity = a;
    }

    @Override
    protected void onPostExecute(StartupActions action)
    {
        super.onPostExecute(action);

        switch (action)
        {
            case RATE_DIALOG:
                RateApplicationAlertDialog rateDialog = RateApplicationAlertDialog.newInstance();
                rateDialog.show(activity.getFragmentManager(), "RateApplicationAlertDialog");
                break;

            case BETA_TEST_DIALOG:
                BetaTestApplicationAlertDialog betaDialog = BetaTestApplicationAlertDialog.newInstance();
                betaDialog.show(activity.getFragmentManager(), "BetaTestApplicationAlertDialog");

            default:
            case NONE:
                break;
        }
    }

    @Override
    protected StartupActions doInBackground(Void... voids)
    {
        StartupActions action = StartupActions.NONE;

        ApplicationStatistics statistics = ApplicationStatistics.GetInstallationDetails(activity.getApplicationContext());
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);

        if (statistics.CrashesCount == 0)
        {
            // Avoid rating and betatest if application has crashed

            if (!prefs.getBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, false))
            {
                if (checkInstallationConditions(statistics, Constants.APPRATE_LAUNCHES_UNTIL_PROMPT, Constants.APPRATE_DAYS_UNTIL_PROMPT))
                {
                    action = StartupActions.RATE_DIALOG;
                }
            }

            if (!prefs.getBoolean(Constants.PREFERENCES_BETATEST_DONT_SHOW_AGAIN, false))
            {
                if (checkInstallationConditions(statistics, Constants.BETATEST_LAUNCHES_UNTIL_PROMPT, Constants.BETATEST_DAYS_UNTIL_PROMPT))
                {
                    action = StartupActions.BETA_TEST_DIALOG;
                }
            }
        }
        else
        {
            // If the application crashed ask the user to send information to support team
        }

        return action;
    }

    private Boolean checkInstallationConditions(ApplicationStatistics statistics, int launchCount, int daysCount)
    {
        Boolean result = false;

        if (statistics.LaunchCount >= launchCount &&
            Utils.ElapsedNDays(statistics.LaunhcFirstDate, daysCount))
        {
            result = true;
        }

        return result;
    }


}
