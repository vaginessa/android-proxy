package com.lechucksoftware.proxy.proxysettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabException;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabHelper;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabResult;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Purchase;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.ArrayList;
import java.util.List;

import be.shouldit.proxy.lib.APL;
import timber.log.Timber;

/**
 * Created by Marco on 15/09/13.
 */
public class IABManager
{
    private final Context context;
    private IabHelper iabHelper;

    private Inventory iabInventory;

    public IABManager(Context ctx)
    {
        context = ctx;

        init();
    }

    private void init()
    {
        // Setup IN APP BILLING
        iabHelper = new IabHelper(context, BuildConfig.PLAY_IN_APP_BILLING_PUBLIC_KEY);

        iabHelper.enableDebugLogging(true);
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
        skus.add(Constants.IAB_ITEM_SKU_PRO);
        skus.add(Constants.IAB_ITEM_SKU_TEST_PURCHASED);
        skus.add(Constants.IAB_ITEM_SKU_TEST_CANCELED);
        skus.add(Constants.IAB_ITEM_SKU_TEST_REFUNDED);
        skus.add(Constants.IAB_ITEM_SKU_TEST_UNAVAILABLE);

        iabHelper.queryInventoryAsync(true, skus, queryAvailableSkuReceivedInventoryListener);
    }

    public void launchPurchase(Activity activity, String sku, int resultCode)
    {
        if (checkIabHelper()) return;

        Timber.d("Launching purchase for SKU: '%s'", sku);

        try
        {
            if (iabInventory.hasPurchase(sku))
            {
                Purchase purchase = iabInventory.getPurchase(sku);
                iabHelper.consumeAsync(purchase, mOnConsumeFinishedListener);
            }
            else
            {
                iabHelper.launchPurchaseFlow(activity, sku, resultCode, mPurchaseFinishedListener, "mypurchasetoken");
            }
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception during launchPurchaseFlow");
            UIUtils.showError(context, R.string.billing_error);
        }
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

    IabHelper.OnConsumeFinishedListener mOnConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
    {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result)
        {
            if (result.isFailure())
            {
                // Handle error
                Timber.e(new IabException(result), "Failure on Iab Purchase Finished");
                return;
            }
            else
            {
                Timber.d("Consumed purchase: '%s'", purchase.toString());
                startQueryAvailableSKU();
            }
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
    {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure())
            {
                switch (result.getResponse())
                {
                    case IabHelper.IABHELPER_USER_CANCELLED:
                        Timber.d("User canceled IAB: '%s'", result.toString());
                        break;

                    default:
                        Timber.e(new IabException(result), "Failure on Iab Purchase Finished");
                        break;
                }


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
                    case Constants.IAB_ITEM_SKU_TEST_PURCHASED:
                        break;
                    case Constants.IAB_ITEM_SKU_TEST_CANCELED:
                        break;
                    case Constants.IAB_ITEM_SKU_TEST_REFUNDED:
                        break;
                    case Constants.IAB_ITEM_SKU_TEST_UNAVAILABLE:
                        break;

                    default:
                        Timber.e("Purchase not recognized");
                }

                startQueryAvailableSKU();
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
                iabInventory = inventory;

                List<SkuDetails> skus = inventory.getAllSkus();
                List<Purchase> purchases = inventory.getAllPurchases();

                for (SkuDetails sku : skus)
                {
                    Timber.d(sku.toString());
                }

                for (Purchase purchase : purchases)
                {
                    Timber.d(purchase.toString());
                }
            }
        }
    };
}
