package com.lechucksoftware.proxy.proxysettings.ui.dialogs.rating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.R;
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

        builder.setTitle(R.string.sorry_for_that);
        builder.setMessage(R.string.mail_feedback_dialog);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {

                startupAction.updateStatus(StartupActionStatus.DONE);

                EventReportingUtils.sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_like_app_mail_feedback, 1L);

                Utils.sendFeedbackMail(getActivity());
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {

                startupAction.updateStatus(StartupActionStatus.REJECTED);

                EventReportingUtils.sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_like_app_mail_feedback, 0L);
            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);

        EventReportingUtils.sendEvent(R.string.analytics_cat_user_action,
                R.string.analytics_act_dialog_button_click,
                R.string.analytics_lab_like_app_mail_feedback, 2L);
    }

    public static MailFeedbackDialog newInstance(StartupAction action)
    {
        MailFeedbackDialog frag = new MailFeedbackDialog(action);
        return frag;
    }
}
