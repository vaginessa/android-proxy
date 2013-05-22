package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.proxy.lib.AccessPoint;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyUtils;

import java.util.ArrayList;
import java.util.List;

public class ProxySelectorListAdapter extends ArrayAdapter<ProxyConfiguration>
{
    private final LayoutInflater vi;
    private ArrayList<ProxyConfiguration> mList; // --CloneChangeRequired
    private Context ctx;

    public ProxySelectorListAdapter(Context context)
    {
        super(context, R.layout.ap_list_item);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx = context;
    }

    static class ApViewHolder
    {
        View statusColor;
        ImageView signal;
        TextView ssid;
        TextView status;
    }

    public void setData(List<ProxyConfiguration> confList)
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

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ApViewHolder viewHolder;
        View view = convertView;

        if (view == null)
        {
            view = vi.inflate(R.layout.ap_list_item, null);

            viewHolder = new ApViewHolder();
            viewHolder.statusColor = view.findViewById(R.id.list_item_status_color);
            viewHolder.signal = (ImageView) view.findViewById(R.id.list_item_ap_icon);
            viewHolder.ssid = (TextView) view.findViewById(R.id.list_item_ap_name);
            viewHolder.status = (TextView) view.findViewById(R.id.list_item_ap_status);

            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ApViewHolder) view.getTag();
        }

        ProxyConfiguration listItem = getItem(position);

        if (listItem != null)
        {
            if (listItem.ap.mRssi == Integer.MAX_VALUE)
            {
                viewHolder.signal.setImageDrawable(null);

                viewHolder.statusColor.setBackgroundResource(R.color.Gray);
            }
            else
            {
                viewHolder.signal.setImageLevel(listItem.ap.getLevel());
                viewHolder.signal.setImageResource(R.drawable.wifi_signal);
                viewHolder.signal.setImageState((listItem.ap.security != AccessPoint.SECURITY_NONE) ? AccessPoint.STATE_SECURED : AccessPoint.STATE_NONE, true);

                if (listItem.isCurrentNetwork())
                    viewHolder.statusColor.setBackgroundResource(R.color.Holo_Blue_Light);
                else
                    viewHolder.statusColor.setBackgroundResource(R.color.Holo_Green_Light);
            }

            viewHolder.ssid.setText(ProxyUtils.cleanUpSSID(listItem.getSSID()));

            viewHolder.status.setText(String.format("%s - %s", listItem.toShortString(), listItem.getAPConnectionStatus()));
//			viewHolder.status.setText(listItem.getAPConnectionStatus());
        }
        return view;
    }
}
