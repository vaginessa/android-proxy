package com.lechucksoftware.proxy.proxysettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabException;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabHelper;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabResult;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Purchase;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Marco on 15/09/13.
 */
public class BillingManager
{
    private final Context context;
    private IabHelper iabHelper;

    public BillingManager(Context ctx)
    {
        context = ctx;

        init();
    }

    private void init()
    {
        // Setup IN APP BILLING
        iabHelper = new IabHelper(context, BuildConfig.PLAY_IN_APP_BILLING_PUBLIC_KEY);
        iabHelper.startSetup(new CustomOnIabSetupFinishedListener());
    }

    public void close()
    {
        if (iabHelper != null)
        {
            iabHelper.dispose();
        }

        iabHelper = null;
    }

    private class CustomOnIabSetupFinishedListener implements IabHelper.OnIabSetupFinishedListener
    {
        public void onIabSetupFinished(IabResult result)
        {
            if (!result.isSuccess())
            {
                Timber.e(new IabException(result), "In-app Billing setup failed: " + result);
            }
            else
            {
                Timber.d("In-app Billing is set up OK");
                startQueryAvailableSKU();
            }
        }
    }

    public void startQueryAvailableSKU()
    {
        List<String> skus = new ArrayList<>();
        skus.add(Constants.IAB_ITEM_SKU_BASE);
        skus.add(Constants.IAB_ITEM_SKU_TEST);
        skus.add(Constants.IAB_ITEM_SKU_PRO);

        iabHelper.queryInventoryAsync(true, skus, queryAvailableSkuReceivedInventoryListener);
    }
    
    public void launchPurchase(Activity activity, String sku, int resultCode)
    {
        if (checkIabHelper()) return;

        iabHelper.launchPurchaseFlow(activity, sku, resultCode, mPurchaseFinishedListener, "mypurchasetoken");
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (checkIabHelper()) return false;

        return iabHelper.handleActivityResult(requestCode, resultCode, data);
    }

    private boolean checkIabHelper()
    {
        if (iabHelper == null)
        {
            Timber.e("iabHelper not initialized. Try again to init.");
            init();
            return true;
        }
        return false;
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
    {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure())
            {
                // Handle error
                Timber.e(new IabException(result),"Failure on Iab Purchase Finished");
                return;
            }
            else
            {
                switch (purchase.getSku())
                {
                    case Constants.IAB_ITEM_SKU_PRO:
                        break;
                    case Constants.IAB_ITEM_SKU_BASE:
                        break;
                    case Constants.IAB_ITEM_SKU_TEST:
                        break;

                    default:
                        Timber.e("Purchase not recognized");
                }
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener queryAvailableSkuReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            if (result.isFailure())
            {
                // Handle failure
            }
            else
            {
                List<SkuDetails> skus = inventory.getAllSkus();
                List<Purchase> purchases = inventory.getAllPurchases();

                for(SkuDetails sku:skus)
                {
                    Timber.d(sku.toString());
                }

                for(Purchase purchase:purchases)
                {
                    Timber.d(purchase.toString());
                }
            }
        }
    };
}
