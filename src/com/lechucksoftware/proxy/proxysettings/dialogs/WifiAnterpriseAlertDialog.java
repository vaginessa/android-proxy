package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import com.lechucksoftware.proxy.proxysettings.R.string;

public class WifiAnterpriseAlertDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(getResources().getText(string.proxysettingscalleractivity_dialog_title));
		builder.setMessage(getResources().getText(string.proxysettingscalleractivity_dialog_description));
		builder.setCancelable(false);

		builder.setPositiveButton(getResources().getText(string.proxysettingscalleractivity_dialog_OK), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				getActivity().finish();
			}
		});

		return builder.create();
    }
    
    
    public static WifiAnterpriseAlertDialog newInstance()
    {
    	WifiAnterpriseAlertDialog frag = new WifiAnterpriseAlertDialog();
        return frag;
    }
}
