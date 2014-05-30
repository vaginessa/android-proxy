package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Resources;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.HtmlDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest.BetaTestAppDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.rating.LikeAppDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.tour.AppTourDialog;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
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

        try
        {
            if (action != null)
            {
                switch (action.actionType)
                {
                    case WHATSNEW_215:
                    case WHATSNEW_216:
                        HtmlDialog htmlDialog = HtmlDialog.newInstance(activity.getString(R.string.whatsnew), Resources.getWhatsNewHTML());
                        htmlDialog.show(activity.getFragmentManager(), "WhatsNewHTMLDialog");
                        action.updateStatus(StartupActionStatus.DONE);
                        break;

                    case FIRST_QUICK_TOUR:
                        AppTourDialog appTourDialog = AppTourDialog.newInstance(action);
                        appTourDialog.show(activity.getFragmentManager(), "AppTourDialog");
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

        if (statistics != null && statistics.CrashesCount == 0)
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
