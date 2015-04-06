package com.lechucksoftware.proxy.proxysettings.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.billing.SkuDetails;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Marco on 06/04/15.
 */
public class IabSkuRecyclerViewAdapter extends RecyclerView.Adapter<IabSkuRecyclerViewAdapter.IabSkuViewHolder>
{
    private List<SkuDetails> items;
    private int itemLayout;

    public static class IabSkuViewHolder extends RecyclerView.ViewHolder
    {
        @InjectView(R.id.iab_sku_title) TextView skuTitle;
        @InjectView(R.id.iab_sku_description) TextView skuDescription;

        public IabSkuViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public IabSkuRecyclerViewAdapter(List<SkuDetails> skuItems, int itemLayout)
    {
        this.items = skuItems;
        this.itemLayout = itemLayout;
    }

    @Override
    public IabSkuViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(this.itemLayout, parent, false);

        return new IabSkuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(IabSkuViewHolder holder, int position)
    {
        SkuDetails iabSku = this.items.get(position);
        holder.skuTitle.setText(iabSku.getTitle());
        holder.skuDescription.setText(iabSku.getDescription());
    }

    @Override
    public int getItemCount()
    {
        if (items != null)
        {
            return this.items.size();
        }
        else
        {
            return 0;
        }
    }
}
