package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

public class NoProxiesDefinedAlertDialog extends BaseDialogFragment
{
    public static String TAG = NoProxiesDefinedAlertDialog.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
        builder.setTitle(getActivity().getString(R.string.warning));
        builder.setMessage(getActivity().getString(R.string.no_proxy_defined));

        builder.setNegativeButton(getResources().getText(R.string.cancel),new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                dismiss();
            }
        });

        builder.setPositiveButton(getResources().getText(R.string.create_new), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                Intent i = new Intent(getActivity(), ProxyDetailActivity.class);
//                ProxyEntity emptyProxy = new ProxyEntity();
//                App.getCacheManager().put(emptyProxy.getUUID(), emptyProxy);
//                i.putExtra(Constants.SELECTED_PROXY_CONF_ARG, emptyProxy.getUUID());
                startActivity(i);
//                onResult(Activity.RESULT_OK);
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
        getTargetFragment().onActivityResult(Requests.CREATE_NEW_PROXY, resultCode, null);
    }

    public static NoProxiesDefinedAlertDialog newInstance()
    {
        NoProxiesDefinedAlertDialog frag = new NoProxiesDefinedAlertDialog();
        return frag;
    }
}
