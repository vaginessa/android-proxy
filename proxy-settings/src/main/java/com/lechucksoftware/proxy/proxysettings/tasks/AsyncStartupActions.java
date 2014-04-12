package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.BetaTestApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.RateApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.StartupAction;
import com.lechucksoftware.proxy.proxysettings.utils.StartupActions;

/**
 * Created by mpagliar on 04/04/2014.
 */
public class AsyncStartupActions  extends AsyncTask<Void, Void, StartupActionType>
{
    private final Activity activity;

    public AsyncStartupActions(Activity a)
    {
        activity = a;
    }

    @Override
    protected void onPostExecute(StartupActionType action)
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
    protected StartupActionType doInBackground(Void... voids)
    {
        StartupActionType action = StartupActionType.NONE;

        ApplicationStatistics statistics = ApplicationStatistics.GetInstallationDetails(activity.getApplicationContext());

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

    private StartupActionType getStartupAction(ApplicationStatistics statistics)
    {
        StartupActionType result = StartupActionType.NONE;

        StartupActions actions = new StartupActions(activity);

        for (StartupAction action : actions.getAvailableActions())
        {
            if (action.canExecute(statistics))
            {
                result = action.actionType;
                break;
            }
        }

        return result;
    }
}
