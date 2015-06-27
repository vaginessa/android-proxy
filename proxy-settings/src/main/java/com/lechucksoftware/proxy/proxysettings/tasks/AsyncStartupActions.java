package com.lechucksoftware.proxy.proxysettings.tasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Resources;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.ui.activities.TransparentAppGuideActivity;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest.BetaTestAppDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.likeapp.DoLikeAppDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.likeapp.LikeAppDialog;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupActions;

import timber.log.Timber;

/**
 * Created by mpagliar on 04/04/2014.
 */
public class AsyncStartupActions  extends AsyncTask<Void, Void, StartupAction>
{
    private static final String TAG = AsyncStartupActions.class.getSimpleName();
    private final AppCompatActivity activity;
    private ApplicationStatistics statistics;

    public AsyncStartupActions(AppCompatActivity a)
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
                    case WHATSNEW_216:
                    case WHATSNEW_300:

                        WebView webView = new WebView(activity);
                        webView.loadUrl(Resources.getWhatsNewHTML());
                        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                                .title(R.string.whatsnew)
                                .customView(webView,true)
                                .positiveText(R.string.ok).build();
                        dialog.show();

                        StartupActions.updateStatus(action.actionType, StartupActionStatus.DONE);

//                        HtmlDialog htmlDialog = HtmlDialog.newInstance(activity.getString(R.string.whatsnew), Resources.getWhatsNewHTML());
//                        htmlDialog.show(activity.getSupportFragmentManager(), "WhatsNewHTMLDialog");

                        break;

                    case FIRST_QUICK_TOUR:
                        Intent appDemo = new Intent(activity, TransparentAppGuideActivity.class);
                        activity.startActivity(appDemo);
                        break;

                    case DONATE_DIALOG:
                        DoLikeAppDialog donateDialog = new DoLikeAppDialog();
                        donateDialog.setCancelable(false);
                        donateDialog.show(activity.getSupportFragmentManager(), "DoLikeAppDialog");
                        break;

                    case RATE_DIALOG:
//                        if (statistics != null && statistics.CrashesCount != 0)
//                        {
//                            // Avoid rating if application has crashed
//                            // TODO: If the application crashed ask the user to sendEvent information to support team
//                            action.updateStatus(StartupActionStatus.NOT_APPLICABLE);
//                        }
//                        else
//                        {
                            LikeAppDialog likeAppDialog = new LikeAppDialog();
                            likeAppDialog.setCancelable(false);
                            likeAppDialog.show(activity.getSupportFragmentManager(), "LikeAppDialog");
//                        }
                        break;

                    case BETA_TEST_DIALOG:
                        BetaTestAppDialog betaDialog = new BetaTestAppDialog();
                        betaDialog.setCancelable(false);
                        betaDialog.show(activity.getSupportFragmentManager(), "BetaTestApplicationAlertDialog");

                    default:
                    case NONE:
                        break;
                }
            }
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during AsyncStartupActions postExecute");
        }
    }

    @Override
    protected StartupAction doInBackground(Void... voids)
    {
        StartupAction action = null;

        action = getStartupAction();

        return action;
    }

    private StartupAction getStartupAction()
    {
        StartupAction result = null;

        App.getTraceUtils().startTrace(TAG, "getStartupAction", Log.DEBUG);

        for (StartupAction action : StartupActions.getAvailableActions().values())
        {
            if (action.canExecute())
            {
                result = action;
                break;
            }
        }

        App.getTraceUtils().stopTrace(TAG, "getStartupAction", Log.DEBUG);

        return result;
    }
}
