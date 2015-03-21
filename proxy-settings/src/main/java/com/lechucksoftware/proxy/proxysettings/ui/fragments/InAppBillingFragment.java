package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabException;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabHelper;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabResult;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Purchase;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class InAppBillingFragment extends BaseFragment
{

    // Implementing In App Billing GuideL http://www.techotopia.com/index.php/An_Android_Studio_Google_Play_In-app_Billing_Tutorial
    @InjectView(R.id.in_app_billing_test)
    Button inAppBillingTestBtn;

    private IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased";

    public static InAppBillingFragment newInstance()
    {
        InAppBillingFragment fragment = new InAppBillingFragment();
        return fragment;
    }

    public InAppBillingFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Setup IN APP BILLING
        mHelper = new IabHelper(getActivity(), BuildConfig.PLAY_IN_APP_BILLING_PUBLIC_KEY);
        mHelper.startSetup(new CustomOnIabSetupFinishedListener());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.in_app_billing_fragment, container, false);
        ButterKnife.inject(this, v);

        return v;
    }

    @OnClick(R.id.in_app_billing_test)
    void inAppBillingTest()
    {
        if (mHelper != null)
        {
            mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU, 10001,
                    mPurchaseFinishedListener, "mypurchasetoken");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data))
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
    {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure())
            {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU))
            {
                consumeItem();
                inAppBillingTestBtn.setEnabled(false);
            }

        }
    };

    public void consumeItem()
    {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            if (result.isFailure())
            {
                // Handle failure
            }
            else
            {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
    {
        public void onConsumeFinished(Purchase purchase,
                                      IabResult result)
        {
            if (result.isSuccess())
            {
//                        clickButton.setEnabled(true);
            }
            else
            {
                // handle error
            }
        }
    };

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
                consumeItem();
            }
        }
    }


}
