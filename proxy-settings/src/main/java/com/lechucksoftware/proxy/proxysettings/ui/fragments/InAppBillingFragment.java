package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BillingManager;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabHelper;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabResult;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Purchase;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InAppBillingFragment extends BaseFragment
{
    // Implementing In App Billing GuideL http://www.techotopia.com/index.php/An_Android_Studio_Google_Play_In-app_Billing_Tutorial
    @InjectView(R.id.in_app_billing_test) Button inAppBillingTestBtn;

    private BillingManager billingManager;

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        billingManager = new BillingManager(getActivity());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (billingManager != null)
        {
            billingManager.close();
        }

        billingManager = null;
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
        if (billingManager != null)
        {
            billingManager.launchPurchase(getActivity(), Constants.IAB_ITEM_SKU_BASE, Requests.IAB_PURCHASE_PRO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (billingManager != null && billingManager.handleActivityResult(requestCode, resultCode, data))
        {

        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
