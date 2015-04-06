package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.activities.IabActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.IabSkuRecyclerViewAdapter;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IabFragment extends BaseFragment
{
    @InjectView(R.id.iab_test_button) Button inAppBillingTestBtn;
    @InjectView(R.id.iab_recycler_view) RecyclerView iabRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IabSkuRecyclerViewAdapter mAdapter;

    public static IabFragment newInstance()
    {
        IabFragment fragment = new IabFragment();
        return fragment;
    }

    public IabFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.in_app_billing_fragment, container, false);
        ButterKnife.inject(this, v);

        mLayoutManager = new LinearLayoutManager(getActivity());
        iabRecyclerView.setHasFixedSize(true);
        iabRecyclerView.setLayoutManager(mLayoutManager);

        setSkus(null);

        return v;
    }

    public void setSkus(List<SkuDetails> skus)
    {
        mAdapter = new IabSkuRecyclerViewAdapter(skus, R.layout.iab_sku_item);
        iabRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.iab_test_button)
    void inAppBillingTest()
    {
        Activity activity = getActivity();
        if (activity != null && activity instanceof IabActivity)
        {
            ((IabActivity) activity).launchPurchase(Constants.IAB_ITEM_SKU_TEST_PURCHASED, Requests.IAB_PURCHASE_PRO);
        }
    }


}
