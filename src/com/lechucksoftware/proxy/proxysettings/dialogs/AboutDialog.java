package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class AboutDialog extends DialogPreference
{
    public AboutDialog(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPersistent(false);
    }

    @Override
    public View onCreateDialogView()
    {
        View v = super.onCreateDialogView();

//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//		builder.setTitle(getResources().getText(string.proxysettingscalleractivity_dialog_title));
//		builder.setMessage(getResources().getText(string.proxysettingscalleractivity_dialog_description));
//		builder.setCancelable(true);
//
//		builder.setPositiveButton(getResources().getText(string.proxysettingscalleractivity_dialog_OK), new DialogInterface.OnClickListener() {
//
//			public void onClick(DialogInterface paramDialogInterface, int paramInt)
//			{
//				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//				getActivity().finish();
//			}
//		});

		return v;
    }
}
