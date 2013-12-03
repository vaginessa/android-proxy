package com.lechucksoftware.proxy.proxysettings.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.WifiSignal;
import com.shouldit.proxy.lib.AccessPoint;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.utils.ProxyUtils;
import com.shouldit.proxy.lib.enums.SecurityType;

import java.util.ArrayList;
import java.util.List;

public class WifiAPSelectorListAdapter extends ArrayAdapter<ProxyConfiguration>
{
    private static String TAG = WifiAPSelectorListAdapter.class.getSimpleName();

    private final LayoutInflater inflater;
    private Context ctx;

    ApViewHolder viewHolder;

    public WifiAPSelectorListAdapter(Context context)
    {
        super(context, R.layout.ap_list_item);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx = context;
    }

    static class ApViewHolder
    {
        TextView ssid;
        TextView status;
        WifiSignal security;
    }

    public void setData(List<ProxyConfiguration> confList)
    {
        Boolean needsRefresh = false;

        if (this.getCount() == confList.size())
        {
            for (int i = 0; i < this.getCount(); i++)
            {
                ProxyConfiguration conf = this.getItem(i);
                if (!conf.isSameConfiguration(confList.get(i)))
                {
                    needsRefresh = true;
                    break;
                }
            }
        }
        else
        {
            needsRefresh = true;
        }

        if (needsRefresh)
        {
            clear();
            if (confList != null)
            {
                for (ProxyConfiguration conf : confList)
                {
                    add(conf);
                }
            }
        }
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.ap_list_item, parent, false);

            viewHolder = new ApViewHolder();
            viewHolder.ssid = (TextView) view.findViewById(R.id.list_item_ap_name);
            viewHolder.status = (TextView) view.findViewById(R.id.list_item_ap_status);
            viewHolder.security = (WifiSignal) view.findViewById(R.id.list_item_wifi_signal);

            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ApViewHolder) view.getTag();
        }

        ProxyConfiguration listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.security.setConfiguration(listItem);
            viewHolder.ssid.setText(String.format("%s", ProxyUtils.cleanUpSSID(listItem.getSSID())));
            viewHolder.status.setText(listItem.toStatusString());
        }

        return view;
    }
}
