package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.activities.PacDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

public class NoProxiesDefinedAlertDialog extends BaseDialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final BaseActivity baseActivity = (BaseActivity) getActivity();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(getActivity().getString(R.string.warning));
        builder.content(getActivity().getString(R.string.no_proxy_defined));

        builder.negativeText(R.string.cancel);
        builder.positiveText(R.string.create_new);

        builder.callback(new MaterialDialog.ButtonCallback() {

            @Override
            public void onPositive(MaterialDialog dialog)
            {
                new MaterialDialog.Builder(baseActivity)
                        .title(R.string.create_new_proxy)
                        .positiveText(R.string.ok)
                        .items(R.array.proxy_types)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                            {
                                switch (which)
                                {
                                    case 0:
                                    default:
                                        Intent staticProxyIntent = new Intent(baseActivity, ProxyDetailActivity.class);
                                        baseActivity.startActivity(staticProxyIntent);

                                        break;
                                    case 1:
                                        Intent pacProxyIntent = new Intent(baseActivity, PacDetailActivity.class);
                                        baseActivity.startActivity(pacProxyIntent);
                                        break;
                                }
                                return true;
                            }
                        })
                        .show();
            }

            @Override
            public void onNeutral(MaterialDialog dialog)
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
