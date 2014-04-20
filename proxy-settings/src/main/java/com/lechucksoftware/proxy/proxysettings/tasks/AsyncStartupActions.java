package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest.BetaTestAppDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.rating.LikeAppDialog;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupActions;
import com.lechucksoftware.proxy.proxysettings.utils.startup.WhatsNewDialog;

/**
 * Created by mpagliar on 04/04/2014.
 */
public class AsyncStartupActions  extends AsyncTask<Void, Void, StartupAction>
{
    private final Activity activity;
    private WhatsNewDialog whatsNewDialog;

    public AsyncStartupActions(Activity a)
    {
        activity = a;
    }

    @Override
    protected void onPostExecute(StartupAction action)
    {
        super.onPostExecute(action);

        try
        {
            if (action != null)
            {
                switch (action.actionType)
                {
                    case WHATSNEW:
                        whatsNewDialog.show();
                        break;

                    case RATE_DIALOG:
                        LikeAppDialog likeAppDialog = LikeAppDialog.newInstance(action);
                        likeAppDialog.show(activity.getFragmentManager(), "LikeAppDialog");
                        break;

                    case BETA_TEST_DIALOG:
                        BetaTestAppDialog betaDialog = BetaTestAppDialog.newInstance(action);
                        betaDialog.show(activity.getFragmentManager(), "BetaTestApplicationAlertDialog");

                    default:
                    case NONE:
                        break;
                }
            }
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
        }
    }

    @Override
    protected StartupAction doInBackground(Void... voids)
    {
        StartupAction action = null;

        ApplicationStatistics statistics = ApplicationStatistics.getInstallationDetails(activity.getApplicationContext());

        whatsNewDialog = new WhatsNewDialog(activity);
        if (whatsNewDialog.isToShow())
        {
            action = new StartupAction(activity,
                    StartupActionType.WHATSNEW,
                    StartupActionStatus.NOT_AVAILABLE,
                    null,
                    null);
        }

        if (action == null && statistics != null && statistics.CrashesCount == 0)
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
