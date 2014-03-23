package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import com.lechucksoftware.proxy.proxysettings.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;

public class VersionWarningAlertDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(getResources().getText(R.string.proxysettingscalleractivity_dialog_title));
		builder.setMessage(getResources().getText(R.string.proxysettingscalleractivity_dialog_description));
		builder.setCancelable(false);

		builder.setPositiveButton(getResources().getText(R.string.proxysettingscalleractivity_dialog_OK), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				getActivity().finish();
			}
		});

		return builder.create();
    }
    
    
    public static VersionWarningAlertDialog newInstance() 
    {
    	VersionWarningAlertDialog frag = new VersionWarningAlertDialog();
        return frag;
    }
}
