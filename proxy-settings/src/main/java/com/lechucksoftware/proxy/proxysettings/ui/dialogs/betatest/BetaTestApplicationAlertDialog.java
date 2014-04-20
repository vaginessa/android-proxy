package com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest;

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
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

public class BetaTestApplicationAlertDialog extends DialogFragment
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
                EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "beta_test_proxy_settings", 1L);
                UIUtils.openBetaTestProject(getActivity());
//				getActivity().finish();
			}
		});

		builder.setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
                startupAction.updateStatus(StartupActionStatus.REJECTED);
                EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "beta_test_proxy_settings", 0L);
			}
		});

		AlertDialog alert = builder.create();
		return alert;
	}

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "beta_test_proxy_settings", 2L);
    }

    public static BetaTestApplicationAlertDialog newInstance(StartupAction action)
	{
		BetaTestApplicationAlertDialog frag = new BetaTestApplicationAlertDialog();
        startupAction = action;
		return frag;
	}
}
