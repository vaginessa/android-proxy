package com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

public class BetaTestAppDialog extends BaseDialogFragment
{
	public static String TAG = BetaTestAppDialog.class.getSimpleName();
    private static StartupAction startupAction;

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
		builder.title(R.string.beta_testing);
		builder.content(R.string.beta_testing_request);
		builder.cancelable(false);
        builder.positiveText(R.string.yes);
        builder.negativeText(R.string.no);
        builder.callback(new MaterialDialog.ButtonCallback() {

            @Override
            public void onPositive(MaterialDialog dialog)
            {
                startupAction.updateStatus(StartupActionStatus.DONE);

                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_beta_test_dialog, 1L);

                BetaTestCommunityDialog betaTestCommunityDialog = BetaTestCommunityDialog.newInstance();
                betaTestCommunityDialog.show(getFragmentManager(), "BetaTestCommunityDialog");
            }

            @Override
            public void onNegative(MaterialDialog dialog)
            {
                startupAction.updateStatus(StartupActionStatus.REJECTED);

                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_beta_test_dialog, 0L);

                BetaTestDismissedDialog betaTestDismissedDialog = BetaTestDismissedDialog.newInstance();
                betaTestDismissedDialog.show(getFragmentManager(), "BetaTestDismissedDialog");
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
                R.string.analytics_lab_beta_test_dialog, 2L);
    }

    public static BetaTestAppDialog newInstance(StartupAction action)
	{
		BetaTestAppDialog frag = new BetaTestAppDialog();
        startupAction = action;
		return frag;
	}
}
