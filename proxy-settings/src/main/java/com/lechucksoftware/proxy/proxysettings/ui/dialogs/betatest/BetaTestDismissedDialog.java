package com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

public class BetaTestDismissedDialog extends BaseDialogFragment
{
	public static String TAG = BetaTestDismissedDialog.class.getSimpleName();

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        builder.title(R.string.no_problem);
		builder.content(R.string.beta_testing_dismissed);

		builder.positiveText(R.string.ok);

        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog)
            {
                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_beta_test_dismiss_dialog, 1L);
            }
        });

		AlertDialog alert = builder.build();
		return alert;
	}

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);

        App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                R.string.analytics_act_dialog_button_click,
                R.string.analytics_lab_beta_test_dismiss_dialog, 2L);
    }

    public static BetaTestDismissedDialog newInstance()
	{
		BetaTestDismissedDialog frag = new BetaTestDismissedDialog();
		return frag;
	}
}
