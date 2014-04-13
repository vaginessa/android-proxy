package com.lechucksoftware.proxy.proxysettings.ui.dialogs.rating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
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
//        builder.setTitle(getResources().getString(R.string.app_rater_dialog_title));
        builder.setMessage(getResources().getString(R.string.mail_feedback_dialog));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {

//                App.getLogger().d(TAG, "Starting Market activity");
                startupAction.updateStatus(StartupActionStatus.DONE);
                Utils.startMarketActivity(getActivity());
            }
        });

        builder.setNeutralButton(getResources().getText(R.string.not_now), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {

                startupAction.updateStatus(StartupActionStatus.POSTPONED);
            }
        });

        builder.setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {

                startupAction.updateStatus(StartupActionStatus.REJECTED);
            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    public static MailFeedbackDialog newInstance(StartupAction action)
    {
        MailFeedbackDialog frag = new MailFeedbackDialog(action);
        return frag;
    }
}
