package com.lechucksoftware.proxy.proxysettings.ui.dialogs.likeapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabHelper;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabResult;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DonateDialog extends BaseDialogFragment
{
    public static void showDonateDialog(final BaseActivity baseActivity)
    {
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
                        DonateDialog donateDialog = new DonateDialog();
                        donateDialog.show(baseActivity.getSupportFragmentManager(),"DonateDialog");
                    }
                }
            };

            try
            {
                baseActivity.startInventoryRefresh(queryInventoryFinishedListener);
                waitDialog.show();
            }
            catch (Exception e)
            {
                Timber.e(e, "Exception during queryInventoryAsync");
                UIUtils.showError(baseActivity, R.string.billing_error_during_init);
            }
        }
        else
        {
            DonateDialog donateDialog = new DonateDialog();
            donateDialog.show(baseActivity.getSupportFragmentManager(),"DonateDialog");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final BaseActivity activity = (BaseActivity) getActivity();
        Inventory inventory = activity.getIabInventory();

        return buildDonateDialog(inventory);
    }

    private Dialog buildDonateDialog(Inventory inventory)
    {
        final BaseActivity activity = (BaseActivity) getActivity();
        MaterialDialog.Builder donateBuilder = new MaterialDialog.Builder(activity);

        final String[] donationSkus = new String[]
                {
                        Constants.IAB_ITEM_SKU_DONATION_0_99,
                        Constants.IAB_ITEM_SKU_DONATION_1_99,
                        Constants.IAB_ITEM_SKU_DONATION_2_99,
                        Constants.IAB_ITEM_SKU_DONATION_5_99,
                        Constants.IAB_ITEM_SKU_DONATION_9_99
                };

        Map<String, SkuDetails> donationSkuDetails = new HashMap<>();
        for (String sku : donationSkus)
        {
            SkuDetails skuDetails = inventory.getSkuDetails(sku);
            if (skuDetails != null)
            {
                donationSkuDetails.put(sku, skuDetails);
            }
        }

        List<String> skusDesc = new ArrayList<String>();
        for (int i = 0; i < donationSkus.length; i++)
        {
            if (donationSkuDetails.containsKey(donationSkus[i]))
            {
                SkuDetails skuDetails = donationSkuDetails.get(donationSkus[i]);
//                    skusDesc.add(String.format("%s (%s)",skuDetails.getPrice(), skuDetails.getDescription()));
                skusDesc.add(skuDetails.getPrice());
            }
        }

        donateBuilder.title(R.string.donate);
        donateBuilder.negativeText(R.string.cancel);
        donateBuilder.positiveText(R.string.ok);
        donateBuilder.cancelable(false);
        donateBuilder.items(skusDesc.toArray(new String[skusDesc.size()]));

        donateBuilder.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
        {

            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
            {
                /**
                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                 * returning false here won't allow the newly selected radio button to actually be selected.
                 **/

                Timber.d("Selected donation SKU: %d", which);

                if (which != -1 && which <= donationSkus.length)
                {
                    StartupActions.updateStatus(StartupActionType.DONATE_DIALOG, StartupActionStatus.DONE);

                    App.getEventsReporter().sendEvent(R.string.analytics_cat_dialogs,
                            R.string.analytics_act_donate_dialog,
                            R.string.analytics_lab_donate_dialog_selected_sku, (long) which);

                    activity.iabLaunchPurchase(donationSkus[which], Requests.IAB_DONATE);
                    return true;
                }
                else
                {
                    App.getEventsReporter().sendEvent(R.string.analytics_cat_dialogs,
                            R.string.analytics_act_donate_dialog,
                            R.string.analytics_lab_donate_dialog_no_sku_selected, -1L);

                    return false;
                }
            }
        });

        donateBuilder.callback(new MaterialDialog.ButtonCallback()
        {
            @Override
            public void onNegative(MaterialDialog dialog)
            {
                App.getEventsReporter().sendEvent(R.string.analytics_cat_dialogs,
                        R.string.analytics_act_donate_dialog,
                        R.string.analytics_lab_donate_dialog_cancel, 0L);
            }
        });

        MaterialDialog alert = donateBuilder.build();
        return alert;
    }
}
