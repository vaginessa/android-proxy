package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

public class NoProxiesDefinedAlertDialog extends BaseDialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(getActivity().getString(R.string.warning));
        builder.content(getActivity().getString(R.string.no_proxy_defined));

        builder.negativeText(getResources().getText(R.string.cancel));
        builder.positiveText(getResources().getText(R.string.create_new));

        builder.callback(new MaterialDialog.ButtonCallback() {

            @Override
            public void onPositive(MaterialDialog dialog)
            {
                Intent i = new Intent(getActivity(), ProxyDetailActivity.class);
                startActivity(i);
            }

            @Override
            public void onNegative(MaterialDialog dialog)
            {
                dialog.dismiss();
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
        getTargetFragment().onActivityResult(Requests.CREATE_NEW_PROXY, resultCode, null);
    }

    public static NoProxiesDefinedAlertDialog newInstance()
    {
        NoProxiesDefinedAlertDialog frag = new NoProxiesDefinedAlertDialog();
        return frag;
    }
}
