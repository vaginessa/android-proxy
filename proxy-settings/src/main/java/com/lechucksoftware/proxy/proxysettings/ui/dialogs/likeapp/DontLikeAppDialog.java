package com.lechucksoftware.proxy.proxysettings.ui.dialogs.likeapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupActions;

public class DontLikeAppDialog extends BaseDialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        builder.title(R.string.sorry_for_that);
        builder.content(R.string.mail_feedback_dialog);
        builder.positiveText(R.string.ok);
        builder.negativeText(R.string.no);

        builder.callback(new MaterialDialog.ButtonCallback() {

            @Override
            public void onPositive(MaterialDialog dialog)
            {
                StartupActions.updateStatus(StartupActionType.RATE_DIALOG, StartupActionStatus.DONE);

                App.getEventsReporter().sendEvent(R.string.analytics_cat_dialogs_action,
                        R.string.analytics_act_dont_like_dialog,
                        R.string.analytics_lab_dont_like_app_dialog_email, 0L);

                Utils.sendFeedbackMail(getActivity());
            }

            @Override
            public void onNegative(MaterialDialog dialog)
            {
                StartupActions.updateStatus(StartupActionType.RATE_DIALOG, StartupActionStatus.REJECTED);

                App.getEventsReporter().sendEvent(R.string.analytics_cat_dialogs_action,
                        R.string.analytics_act_dont_like_dialog,
                        R.string.analytics_lab_dont_like_app_dialog_close, 0L);
            }
        });

        MaterialDialog alert = builder.build();
        return alert;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);

        App.getEventsReporter().sendEvent(R.string.analytics_cat_dialogs_action,
                R.string.analytics_act_dont_like_dialog,
                R.string.analytics_lab_dont_like_app_dialog_cancel, 0L);
    }
}
