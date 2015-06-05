package com.lechucksoftware.proxy.proxysettings.ui.dialogs.likeapp;

import android.app.Dialog;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabHelper;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabResult;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

public class DoLikeAppDialog extends BaseDialogFragment
{
    public static String TAG = DoLikeAppDialog.class.getSimpleName();
    private StartupAction startupAction;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startupAction = getArguments().getParcelable("ACTION");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.thank_you);
        builder.cancelable(false);

        // On the RIGHT

        if (App.getInstance().activeMarket == AndroidMarket.PLAY)
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

        builder.callback(new MaterialDialog.ButtonCallback() {

            @Override
            public void onPositive(MaterialDialog dialog)
            {
                final BaseActivity baseActivity = (BaseActivity) getActivity();
                MaterialDialog.Builder waitDialogBuilder = new MaterialDialog.Builder(baseActivity);
                waitDialogBuilder.title(R.string.app_name);
                waitDialogBuilder.content(R.string.please_wait);
                waitDialogBuilder.progress(true, 0);
                final MaterialDialog waitDialog = waitDialogBuilder.build();

                if(baseActivity.getIabInventory() == null)
                {
                    IabHelper.QueryInventoryFinishedListener queryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener()
                    {
                        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
                        {
                            baseActivity.handleQueryInventory(result, inventory);
                            waitDialog.dismiss();

                            if (result.isFailure())
                            {
                                UIUtils.showError(baseActivity, R.string.billing_error_during_init);
                            }
                            else
                            {
                                DonateDialog donateDialog = DonateDialog.newInstance();
                                donateDialog.show(baseActivity.getSupportFragmentManager(),"DonateDialog");
                            }
                        }
                    };

                    baseActivity.startInventoryRefresh(queryInventoryFinishedListener);
                    waitDialog.show();
                }
                else
                {
                    DonateDialog donateDialog = DonateDialog.newInstance();
                    donateDialog.show(baseActivity.getSupportFragmentManager(),"DonateDialog");
                }
            }

            @Override
            public void onNegative(MaterialDialog dialog)
            {
                startupAction.updateStatus(StartupActionStatus.DONE);
                Utils.startMarketActivity(getActivity());
            }
        });

        MaterialDialog alert = builder.build();
        return alert;
    }

    public static DoLikeAppDialog newInstance(StartupAction action)
    {
        DoLikeAppDialog frag = new DoLikeAppDialog();

        Bundle b = new Bundle();
        b.putParcelable("ACTION", action);
        frag.setArguments(b);

        return frag;
    }
}
