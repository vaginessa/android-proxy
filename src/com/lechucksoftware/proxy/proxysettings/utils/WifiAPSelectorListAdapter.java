package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.proxy.lib.*;

import java.util.ArrayList;
import java.util.List;

public class WifiAPSelectorListAdapter extends ArrayAdapter<ProxyConfiguration>
{
    private final LayoutInflater vi;
    private ArrayList<ProxyConfiguration> mList; // --CloneChangeRequired
    private Context ctx;

    public WifiAPSelectorListAdapter(Context context)
    {
        super(context, R.layout.ap_list_item);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx = context;
    }

    static class ApViewHolder
    {
//        RelativeLayout layout;
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
//            viewHolder.layout = (RelativeLayout) view.findViewById(R.id.list_item_ap_layout);
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
            if (listItem.ap.getLevel() == -1)
            {
                viewHolder.signal.setImageDrawable(null);

                viewHolder.statusColor.setBackgroundResource(R.color.Gray);
            }
            else
            {
                viewHolder.signal.setImageLevel(listItem.ap.getLevel());
                viewHolder.signal.setImageResource(R.drawable.wifi_signal);
                viewHolder.signal.setImageState((listItem.ap.security != SecurityType.SECURITY_NONE) ? AccessPoint.STATE_SECURED : AccessPoint.STATE_NONE, true);

                if (listItem.isCurrentNetwork())
                {
//                    viewHolder.layout.setBackgroundResource(R.color.Holo_Blue_Light);
                    viewHolder.statusColor.setBackgroundResource(R.color.Holo_Blue_Light);
                }
                else
                {
//                    viewHolder.layout.setBackgroundResource(R.color.Holo_Green_Light);
                    viewHolder.statusColor.setBackgroundResource(R.color.Holo_Green_Light);
                }
            }

            viewHolder.ssid.setText(String.format("%s",ProxyUtils.cleanUpSSID(listItem.getSSID())));

            StringBuilder sb = new StringBuilder();
            sb.append(listItem.toStatusString());
            String sec = ProxyUtils.getSecurityString(listItem,ctx,true);
            if (sec != null && sec.length() > 0)
                sb.append(String.format(" - %s",sec));

            viewHolder.status.setText(sb.toString());
        }
        return view;
    }
}
