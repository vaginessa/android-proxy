package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.lechucksoftware.proxy.proxysettings.ui.dialogs.BetaTestApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.FirstRateDialog;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupActions;

/**
 * Created by mpagliar on 04/04/2014.
 */
public class AsyncStartupActions  extends AsyncTask<Void, Void, StartupAction>
{
    private final Activity activity;

    public AsyncStartupActions(Activity a)
    {
        activity = a;
    }

    @Override
    protected void onPostExecute(StartupAction action)
    {
        super.onPostExecute(action);

        if (action != null)
        {
            switch (action.actionType)
            {
                case RATE_DIALOG:
                    FirstRateDialog rateDialog = FirstRateDialog.newInstance(action);
                    rateDialog.show(activity.getFragmentManager(), "FirstRateDialog");
                    break;

                case BETA_TEST_DIALOG:
                    BetaTestApplicationAlertDialog betaDialog = BetaTestApplicationAlertDialog.newInstance(action);
                    betaDialog.show(activity.getFragmentManager(), "BetaTestApplicationAlertDialog");

                default:
                case NONE:
                    break;
            }
        }
    }

    @Override
    protected StartupAction doInBackground(Void... voids)
    {
        StartupAction action = null;

        ApplicationStatistics statistics = ApplicationStatistics.getInstallationDetails(activity.getApplicationContext());

        if (statistics.CrashesCount == 0)
        {
            // Avoid rating and betatest if application has crashed
            action = getStartupAction(statistics);
        }
        else
        {
            // TODO: If the application crashed ask the user to send information to support team
        }

        return action;
    }

    private StartupAction getStartupAction(ApplicationStatistics statistics)
    {
        StartupAction result = null;

        StartupActions actions = new StartupActions(activity);

        for (StartupAction action : actions.getAvailableActions())
        {
            if (action.canExecute(statistics))
            {
                result = action;
                break;
            }
        }

        return result;
    }
}
