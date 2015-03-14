package com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

public class BetaTestCommunityDialog extends BaseDialogFragment
{
	public static String TAG = BetaTestCommunityDialog.class.getSimpleName();

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        builder.title(R.string.welcome_aboard);
		builder.content(R.string.beta_testing_instructions);
		builder.positiveText(R.string.ok);

        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog)
            {
                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_dialog_button_click,
                        R.string.analytics_lab_beta_test_community_dialog, 1L);

                UIUtils.openBetaTestProject(getActivity());
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
                R.string.analytics_lab_beta_test_community_dialog, 2L);
    }

    public static BetaTestCommunityDialog newInstance()
	{
		BetaTestCommunityDialog frag = new BetaTestCommunityDialog();
		return frag;
	}
}
