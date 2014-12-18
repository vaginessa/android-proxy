package com.lechucksoftware.proxy.proxysettings.ui.dialogs.rating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

public class MailFeedbackDialog extends BaseDialogFragment
{
    public static String TAG = MailFeedbackDialog.class.getSimpleName();
    private StartupAction startupAction;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startupAction = (StartupAction) getArguments().getSerializable("ACTION");
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

                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
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

                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
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

        App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                R.string.analytics_act_dialog_button_click,
                R.string.analytics_lab_like_app_mail_feedback, 2L);
    }

    public static MailFeedbackDialog newInstance(StartupAction action)
    {
        MailFeedbackDialog frag = new MailFeedbackDialog();

        Bundle b = new Bundle();
        b.putSerializable("ACTION", action);
        frag.setArguments(b);

        return frag;
    }
}
