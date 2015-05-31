package com.lechucksoftware.proxy.proxysettings.ui.dialogs.appfeedback;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

public class LikeAppDialog extends BaseDialogFragment
{
    public static String TAG = LikeAppDialog.class.getSimpleName();
    private StartupAction startupAction;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startupAction = getArguments().getParcelable("ACTION");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        builder.title(R.string.app_name);
        builder.content(R.string.do_you_like);

        builder.positiveText(R.string.yes);
        builder.negativeText(R.string.no);

        builder.callback(new MaterialDialog.ButtonCallback() {

            @Override
            public void onPositive(MaterialDialog dialog)
            {
                DoLikeAppDialog rateDialog = DoLikeAppDialog.newInstance(startupAction);
                rateDialog.show(getFragmentManager(), "RateAppDialog");

                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_like_app_dialog, 1L);
            }

            @Override
            public void onNegative(MaterialDialog dialog)
            {
                DontLikeAppDialog feedbackDialog = DontLikeAppDialog.newInstance(startupAction);
                feedbackDialog.show(getFragmentManager(), "MailFeedbackDialog");

                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_like_app_dialog, 0L);
            }
        });

        MaterialDialog alert = builder.build();
        return alert;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);

        App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                R.string.analytics_act_dialog_button_click,
                R.string.analytics_lab_like_app_dialog, 0L);
    }

    public static LikeAppDialog newInstance(StartupAction action)
    {
        LikeAppDialog frag = new LikeAppDialog();

        Bundle b = new Bundle();
        b.putParcelable("ACTION", action);
        frag.setArguments(b);

        return frag;
    }
}
