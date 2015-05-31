package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DonateDialog extends BaseDialogFragment
{
    public static String TAG = DonateDialog.class.getSimpleName();
    private StartupAction startupAction;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null && args.containsKey("ACTION"))
        {
            startupAction = getArguments().getParcelable("ACTION");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final BaseActivity activity = (BaseActivity) getActivity();
        MaterialDialog.Builder donateBuilder = new MaterialDialog.Builder(activity);
        Inventory inventory = activity.getIabInventory();

        if (inventory != null)
        {
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
                        if (startupAction != null)
                        {
                            startupAction.updateStatus(StartupActionStatus.DONE);
                        }

                        App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                                R.string.analytics_act_dialog_button_click,
                                R.string.analytics_lab_donate_dialog, 1L);

                        activity.iabLaunchPurchase(donationSkus[which], Requests.IAB_DONATE);
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            });
        }

        MaterialDialog alert = donateBuilder.build();
        return alert;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);

        App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                R.string.analytics_act_dialog_button_click,
                R.string.analytics_lab_donate_dialog, 0L);
    }

    public static DonateDialog newInstance(StartupAction action)
    {
        DonateDialog frag = new DonateDialog();

        Bundle b = new Bundle();
        b.putParcelable("ACTION", action);
        frag.setArguments(b);

        return frag;
    }

    public static DonateDialog newInstance()
    {
        DonateDialog frag = new DonateDialog();
        return frag;
    }
}
