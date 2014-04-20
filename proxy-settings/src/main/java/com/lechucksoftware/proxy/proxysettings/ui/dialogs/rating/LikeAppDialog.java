package com.lechucksoftware.proxy.proxysettings.ui.dialogs.rating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.BaseActions;
import com.lechucksoftware.proxy.proxysettings.constants.EventCategories;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

public class LikeAppDialog extends DialogFragment
{
    public static String TAG = "LikeAppDialog";
    private StartupAction startupAction;

    public LikeAppDialog(StartupAction action)
    {
        startupAction = action;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());

        builder.setMessage(getResources().getString(R.string.app_rater_dialog_text));

        builder.setPositiveButton(getResources().getText(R.string.yes), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                RateAppDialog rateDialog = RateAppDialog.newInstance(startupAction);
                rateDialog.show(getFragmentManager(), "RateAppDialog");
                EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "like_proxy_settings", 1L);
            }
        });

        builder.setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                MailFeedbackDialog feedbackDialog = MailFeedbackDialog.newInstance(startupAction);
                feedbackDialog.show(getFragmentManager(), "MailFeedbackDialog");
                EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "like_proxy_settings", 0L);
            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "like_proxy_settings", 2L);
    }

    public static LikeAppDialog newInstance(StartupAction action)
    {
        LikeAppDialog frag = new LikeAppDialog(action);
        return frag;
    }
}
