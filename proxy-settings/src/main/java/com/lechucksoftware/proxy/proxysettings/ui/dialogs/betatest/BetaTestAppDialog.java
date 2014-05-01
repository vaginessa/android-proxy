package com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

public class BetaTestAppDialog extends DialogFragment
{
	public static String TAG = "LikeAppDialog";
    private static StartupAction startupAction;

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
		builder.setTitle(R.string.beta_testing);
		builder.setMessage(R.string.beta_testing_request);
		builder.setCancelable(false);
		builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
                startupAction.updateStatus(StartupActionStatus.DONE);
                EventReportingUtils.sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_beta_test_dialog, 1L);

                BetaTestCommunityDialog betaTestCommunityDialog = BetaTestCommunityDialog.newInstance();
                betaTestCommunityDialog.show(getFragmentManager(), "BetaTestCommunityDialog");
			}
		});

		builder.setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
                startupAction.updateStatus(StartupActionStatus.REJECTED);

                EventReportingUtils.sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_beta_test_dialog, 0L);

                BetaTestDismissedDialog betaTestDismissedDialog = BetaTestDismissedDialog.newInstance();
                betaTestDismissedDialog.show(getFragmentManager(), "BetaTestDismissedDialog");
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
                R.string.analytics_lab_beta_test_dialog, 2L);
    }

    public static BetaTestAppDialog newInstance(StartupAction action)
	{
		BetaTestAppDialog frag = new BetaTestAppDialog();
        startupAction = action;
		return frag;
	}
}
