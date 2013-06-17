package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ProxySettingsCallerActivity;
import com.shouldit.android.utils.lib.log.LogWrapper;

public class RateApplicationAlertDialog extends DialogFragment
{
	public static String TAG = "RateApplicationAlertDialog";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.app_rater_dialog_title));
		builder.setMessage(getResources().getString(R.string.app_rater_dialog_text));
		builder.setCancelable(false);
		builder.setPositiveButton(getResources().getText(R.string.app_rater_dialog_button_rate), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				((ProxySettingsCallerActivity) getActivity()).DontDisplayAgain();
				LogWrapper.d(TAG, "Starting Market activity");
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.lechucksoftware.proxy.proxysettings")));
				getActivity().finish();
			}
		});

		builder.setNeutralButton(getResources().getText(R.string.app_rater_dialog_button_remind), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				((ProxySettingsCallerActivity) getActivity()).GoToProxy();
			}
		});

		builder.setNegativeButton(getResources().getText(R.string.app_rater_dialog_button_nothanks), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				((ProxySettingsCallerActivity) getActivity()).DontDisplayAgain();
				((ProxySettingsCallerActivity) getActivity()).GoToProxy();
			}
		});

		AlertDialog alert = builder.create();
		return alert;
	}

	public static RateApplicationAlertDialog newInstance()
	{
		RateApplicationAlertDialog frag = new RateApplicationAlertDialog();
		return frag;
	}
}
