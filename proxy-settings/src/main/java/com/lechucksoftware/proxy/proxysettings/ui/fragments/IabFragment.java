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
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.activities.IabActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.IabSkuRecyclerViewAdapter;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Purchase;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class IabFragment extends BaseFragment
{
    @InjectView(R.id.iab_recycler_view)
    RecyclerView iabRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IabSkuRecyclerViewAdapter mAdapter;

    private Inventory mInventory;
    private List<SkuDetails> mSkus;
    private List<Purchase> mPurchase;

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

        setInventory(null);

        return v;
    }

    public void setInventory(Inventory inventory)
    {
        mInventory = inventory;

        if (mInventory != null)
        {
            mSkus = inventory.getAllSkus();
            mPurchase = inventory.getAllPurchases();
        }

        mAdapter = new IabSkuRecyclerViewAdapter(inventory, R.layout.iab_sku_item);
        mAdapter.setOnItemClickListener(new IabSkuRecyclerViewAdapter.OnItemClickListener()
        {

            @Override
            public void onItemClick(View view, int position)
            {
                Timber.d("Selected SKU Item %d", position);

                if (mSkus != null)
                {
                    SkuDetails skuDetails = mSkus.get(position);
                    if (skuDetails != null)
                    {
                        Timber.d("Selected SKU: %s", skuDetails.toString());

                        if (mInventory.hasPurchase(skuDetails.getSku()))
                        {
                            Purchase p = mInventory.getPurchase(skuDetails.getSku());
                            Timber.d("SKU purchased: %s", p.toString());
                        }
                        else
                        {
                            Timber.d("Launching purchase for SKU: %s", skuDetails.toString());
                            launchSkuPurchase(skuDetails);
                        }
                    }
                    else
                    {
                        Timber.e("Cannot find SKU (size: %d) for position %d", mSkus.size(), position);
                    }
                }
            }

        });

        iabRecyclerView.setAdapter(mAdapter);
    }

    void launchSkuPurchase(SkuDetails sku)
    {
        Activity activity = getActivity();
        if (activity != null && activity instanceof IabActivity)
        {
            ((IabActivity) activity).launchPurchase(sku.getSku(), Requests.IAB_PURCHASE);
        }
    }
}
