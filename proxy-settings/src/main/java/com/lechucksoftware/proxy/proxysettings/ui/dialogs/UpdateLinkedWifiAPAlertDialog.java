package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

public class UpdateLinkedWifiAPAlertDialog extends BaseDialogFragment
{
    public static String TAG = UpdateLinkedWifiAPAlertDialog.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
        builder.setTitle(getActivity().getString(R.string.warning));
        builder.setMessage(getActivity().getString(R.string.wifi_ap_will_be_updated));
        builder.setPositiveButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                onResult(Activity.RESULT_OK);
            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        onResult(Activity.RESULT_CANCELED);
    }

    protected void onResult(final int resultCode)
    {
        getTargetFragment().onActivityResult(Requests.UPDATE_LINKED_WIFI_AP, resultCode, null);
    }

    public static UpdateLinkedWifiAPAlertDialog newInstance()
    {
        UpdateLinkedWifiAPAlertDialog frag = new UpdateLinkedWifiAPAlertDialog();
        return frag;
    }
}
