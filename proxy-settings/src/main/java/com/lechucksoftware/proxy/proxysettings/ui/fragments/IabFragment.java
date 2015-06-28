package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.activities.IabActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Inventory;
import com.lechucksoftware.proxy.proxysettings.utils.billing.Purchase;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IabFragment extends BaseFragment
{
//    @InjectView(R.id.iab_recycler_view)
//    RecyclerView iabRecyclerView;
//    private LinearLayoutManager mLayoutManager;
//    private IabSkuRecyclerViewAdapter mAdapter;

    @InjectView(R.id.iab_reset) Button resetIabButton;
    @InjectView(R.id.iab_buy_base) Button baseIabButton;

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

//        mLayoutManager = new LinearLayoutManager(getActivity());
//        iabRecyclerView.setHasFixedSize(true);
//        iabRecyclerView.setLayoutManager(mLayoutManager);

        if (BuildConfig.DEBUG)
        {
            resetIabButton.setVisibility(View.VISIBLE);
        }

        setInventory(null);

        return v;
    }

    @OnClick(R.id.iab_reset)
    public void resetIab()
    {
        if (mInventory != null)
        {
            SkuDetails skuDetails = mInventory.getSkuDetails(Constants.IAB_ITEM_SKU_BASE);
            launchSkuPurchase(skuDetails);
        }
    }

    @OnClick(R.id.iab_buy_base)
    public void buyBase()
    {
        if (mInventory != null)
        {
            SkuDetails skuDetails = mInventory.getSkuDetails(Constants.IAB_ITEM_SKU_BASE);
            launchSkuPurchase(skuDetails);
        }
    }

    public void setInventory(Inventory inventory)
    {
        mInventory = inventory;

        if (mInventory != null)
        {
            mSkus = inventory.getAllSkus();
            mPurchase = inventory.getAllPurchases();
        }

//        mAdapter = new IabSkuRecyclerViewAdapter(inventory, R.layout.iab_sku_item);
//        mAdapter.setOnItemClickListener(new IabSkuRecyclerViewAdapter.OnItemClickListener()
//        {
//
//            @Override
//            public void onItemClick(View view, int position)
//            {
//                Timber.d("Selected SKU Item %d", position);
//
//                if (mSkus != null)
//                {
//                    SkuDetails skuDetails = mSkus.get(position);
//                    if (skuDetails != null)
//                    {
//                        Timber.d("Selected SKU: %s", skuDetails.toString());
//
//                        if (mInventory.hasPurchase(skuDetails.getSku()))
//                        {
//                            Purchase p = mInventory.getPurchase(skuDetails.getSku());
//                            Timber.d("SKU purchased: %s", p.toString());
//
//                            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
//
//                            DateFormat df = DateFormat.getDateInstance();
//
//                            long purchaseTimeL = p.getPurchaseTime();
//                            Date purchaseDate = new Date(purchaseTimeL);
//                            builder.content(String.format("You already purchased the %s on %s", skuDetails.getTitle(), df.format(purchaseDate)));
//                            builder.positiveText(R.string.ok);
//
//                            builder.show();
//                        }
//                        else
//                        {
//                            Timber.d("Launching purchase for SKU: %s", skuDetails.toString());
//                            launchSkuPurchase(skuDetails);
//                        }
//                    }
//                    else
//                    {
//                        Timber.e("Cannot find SKU (size: %d) for position %d", mSkus.size(), position);
//                    }
//                }
//            }
//
//        });
//
//        iabRecyclerView.setAdapter(mAdapter);
    }

    void launchSkuPurchase(SkuDetails skuDetail)
    {
        Activity activity = getActivity();
        if (activity != null && activity instanceof IabActivity)
        {
            ((IabActivity) activity).launchPurchase(skuDetail.getSku(), Requests.IAB_PURCHASE);
        }
    }
}
