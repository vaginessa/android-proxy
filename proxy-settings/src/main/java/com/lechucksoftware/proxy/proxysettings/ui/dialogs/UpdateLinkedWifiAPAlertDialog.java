package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
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
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.warning);
        builder.content(R.string.wifi_ap_will_be_updated);

        builder.positiveText(R.string.ok);

        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog)
            {
                onResult(Activity.RESULT_OK);
            }
        });

        MaterialDialog alert = builder.build();
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
