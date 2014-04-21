package com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.BaseActions;
import com.lechucksoftware.proxy.proxysettings.constants.EventCategories;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

public class BetaTestCommunityDialog extends DialogFragment
{
	public static String TAG = "LikeAppDialog";

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());

		builder.setMessage(R.string.beta_testing_community);
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
                EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "beta_test_community_proxy_settings", 1L);
                UIUtils.openBetaTestProject(getActivity());
			}
		});

		AlertDialog alert = builder.create();
		return alert;
	}

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        EventReportingUtils.sendEvent(EventCategories.USER_ACTION, BaseActions.DIALOG_ANSWER, "beta_test_community_proxy_settings", 2L);
    }

    public static BetaTestCommunityDialog newInstance()
	{
		BetaTestCommunityDialog frag = new BetaTestCommunityDialog();
		return frag;
	}
}
