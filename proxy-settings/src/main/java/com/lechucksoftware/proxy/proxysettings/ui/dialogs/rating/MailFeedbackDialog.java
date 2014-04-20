package com.lechucksoftware.proxy.proxysettings.ui.dialogs.rating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.BaseActions;
import com.lechucksoftware.proxy.proxysettings.constants.EventCategories;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

public class MailFeedbackDialog extends DialogFragment
{
    public static String TAG = "LikeAppDialog";
    private StartupAction startupAction;

    public MailFeedbackDialog(StartupAction action)
    {
        startupAction = action;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());

        builder.setMessage(getResources().getString(R.string.mail_feedback_dialog));
        builder.setPositiveButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {

//                App.getLogger().d(TAG, "Starting Market activity");
                startupAction.updateStatus(StartupActionStatus.DONE);
                EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "mail_feedback_proxy_settings", 1L);
                Utils.startMarketActivity(getActivity());
            }
        });

        builder.setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {

                startupAction.updateStatus(StartupActionStatus.REJECTED);
                EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "mail_feedback_proxy_settings", 0L);
            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "mail_feedback_proxy_settings", 2L);
    }

    public static MailFeedbackDialog newInstance(StartupAction action)
    {
        MailFeedbackDialog frag = new MailFeedbackDialog(action);
        return frag;
    }
}
