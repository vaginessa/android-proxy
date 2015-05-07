package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.IabFragment;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabException;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabHelper;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabResult;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Purchase;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class IabActivity extends ActionBarActivity
{
    private IabHelper iabHelper;
    private Inventory iabInventory;
    private IabFragment iabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_in_app_billing);

        if (savedInstanceState == null)
        {
            iabFragment = IabFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, iabFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        initIAB();
    }

    private void initIAB()
    {
        try
        {
            iabHelper = new IabHelper(this, BuildConfig.PLAY_IN_APP_BILLING_PUBLIC_KEY);

            iabHelper.enableDebugLogging(true);
            iabHelper.startSetup(new CustomOnIabSetupFinishedListener());
        }
        catch (Exception e)
        {
            Timber.e(e,"Cannot initIAB");
        }
    }

    private boolean checkIabHelper()
    {
        if (iabHelper == null)
        {
            Timber.e("iabHelper not initialized. Try again to initIAB.");
            initIAB();
            return true;
        }

        return false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (iabHelper != null)
        {
            iabHelper.dispose();
        }

        iabHelper = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Timber.d("Received activity result. Request: %d, Result: %d", requestCode, resultCode);

        if (iabHelper != null && iabHelper.handleActivityResult(requestCode, resultCode, data))
        {

        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_app_billing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
//        skus.add(Constants.IAB_ITEM_SKU_PRO);
//        skus.add(Constants.IAB_ITEM_SKU_NINJA);

//        if (BuildConfig.DEBUG)
//        {
//            skus.add(Constants.IAB_ITEM_SKU_TEST_PURCHASED);
//            skus.add(Constants.IAB_ITEM_SKU_TEST_CANCELED);
//            skus.add(Constants.IAB_ITEM_SKU_TEST_REFUNDED);
//            skus.add(Constants.IAB_ITEM_SKU_TEST_UNAVAILABLE);
//        }

        iabHelper.queryInventoryAsync(true, skus, queryAvailableSkuReceivedInventoryListener);
    }

    public void launchPurchase(String sku, int requestCode)
    {
        if (checkIabHelper()) return;

        Timber.d("Launching purchase for SKU: '%s'", sku);

        try
        {
//            if (iabInventory.hasPurchase(sku))
//            {
//                Purchase purchase = iabInventory.getPurchase(sku);
//                iabHelper.consumeAsync(purchase, mOnConsumeFinishedListener);
//            }
//            else
//            {
                iabHelper.launchPurchaseFlow(this, sku, requestCode, mPurchaseFinishedListener, "mypurchasetoken");
//            }
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception during launchPurchaseFlow");
            UIUtils.showError(this, R.string.billing_error);
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (checkIabHelper()) return false;

        return iabHelper.handleActivityResult(requestCode, resultCode, data);
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
                        Timber.e("User canceled IAB: '%s'", result.toString());
                        break;

                    default:
                        Timber.e(new IabException(result), "Failure on Iab Purchase Finished");
                        break;
                }

                return;
            }
            else
            {
                Timber.d("Purchase successful: %s", result.toString());
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

                iabFragment.setInventory(inventory);
            }
        }
    };
}
