package com.lechucksoftware.proxy.proxysettings.ui.dialogs.likeapp;

import android.app.Dialog;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupActions;

public class DoLikeAppDialog extends BaseDialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.thank_you);
        builder.cancelable(false);

        // On the RIGHT
        if (App.getInstance().activeMarket == AndroidMarket.PLAY
            && ((BaseActivity) getActivity()).isIabEnabled())
        {
            builder.positiveText(R.string.donate);
            builder.content(R.string.rate_or_donate_message);
        }
        else
        {
            // All other stores
            builder.content(R.string.rate_app_message);
        }

        builder.negativeText(R.string.rate);

        // On the LEFT
        builder.neutralText(R.string.cancel);

        builder.callback(new MaterialDialog.ButtonCallback()
        {
            @Override
            public void onPositive(MaterialDialog dialog)
            {
                StartupActions.updateStatus(StartupActionType.RATE_DIALOG, StartupActionStatus.DONE);
                DonateDialog.showDonateDialog((BaseActivity) getActivity());
            }

            @Override
            public void onNegative(MaterialDialog dialog)
            {
                StartupActions.updateStatus(StartupActionType.RATE_DIALOG, StartupActionStatus.DONE);
                Utils.startMarketActivity(getActivity());
            }
        });

        MaterialDialog alert = builder.build();
        return alert;
    }
}
