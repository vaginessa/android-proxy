package com.lechucksoftware.proxy.proxysettings.adapters;

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
import com.shouldit.proxy.lib.SecurityType;

import java.util.ArrayList;
import java.util.List;

public class WifiAPSelectorListAdapter extends ArrayAdapter<ProxyConfiguration>
{
    private static String TAG = WifiAPSelectorListAdapter.class.getSimpleName();

    private final LayoutInflater inflater;
    private ArrayList<ProxyConfiguration> mList; // --CloneChangeRequired
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
        //        RelativeLayout layout;
        View statusColor;
        ImageView signal;
        TextView ssid;
        TextView status;
        TextView security;
    }

    public void setData(List<ProxyConfiguration> confList)
    {
//        LogWrapper.startTrace(TAG,"setData",Log.ASSERT);
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

//        LogWrapper.stopTrace(TAG,"setData",Log.ASSERT);
    }

    public View getView(int position, View view, ViewGroup parent)
    {
//        LogWrapper.startTrace(TAG, "getView", Log.ERROR);

        if (view == null)
        {
//            LogWrapper.startTrace(TAG, "getView - viewHolder init", Log.ERROR);
            view = inflater.inflate(R.layout.ap_list_item, parent, false);

            viewHolder = new ApViewHolder();
//            viewHolder.layout = (RelativeLayout) view.findViewById(R.id.list_item_ap_layout);
            viewHolder.statusColor = view.findViewById(R.id.list_item_status_color);
            viewHolder.signal = (ImageView) view.findViewById(R.id.list_item_ap_icon);
            viewHolder.ssid = (TextView) view.findViewById(R.id.list_item_ap_name);
            viewHolder.status = (TextView) view.findViewById(R.id.list_item_ap_status);
            viewHolder.security = (TextView) view.findViewById(R.id.list_item_ap_security);

            view.setTag(viewHolder);

//            LogWrapper.stopTrace(TAG, "getView - viewHolder init", Log.ERROR);
        }
        else
        {
//            LogWrapper.startTrace(TAG, "getView - viewHolder getTag", Log.ERROR);
            viewHolder = (ApViewHolder) view.getTag();
//            LogWrapper.stopTrace(TAG, "getView - viewHolder getTag", Log.ERROR);
        }

//        LogWrapper.startTrace(TAG, "getView - getItem", Log.ERROR);
        ProxyConfiguration listItem = getItem(position);
//        LogWrapper.stopTrace(TAG, "getView - getItem", Log.ERROR);

//        LogWrapper.startTrace(TAG, "getView - setValues", Log.ERROR);
        if (listItem != null)
        {
            if (listItem.ap.getLevel() == -1)
            {
                viewHolder.signal.setImageResource(R.drawable.ic_action_notvalid);
                viewHolder.statusColor.setBackgroundResource(R.color.DarkGrey);

//                // Set disabled layout for not available networks
//                viewHolder.signal.setEnabled(false);
//                viewHolder.statusColor.setEnabled(false);
//                viewHolder.ssid.setEnabled(false);
//                viewHolder.status.setEnabled(false);
//                view.setEnabled(false);
            }
            else
            {
                viewHolder.signal.setImageLevel(listItem.ap.getLevel());
                viewHolder.signal.setImageResource(R.drawable.wifi_signal);
                viewHolder.signal.setImageState((listItem.ap.security != SecurityType.SECURITY_NONE) ? AccessPoint.STATE_SECURED : AccessPoint.STATE_NONE, true);

                if (listItem.isCurrentNetwork())
                {
//                    viewHolder.layout.setBackgroundResource(R.color.Holo_Blue_Light);
                    viewHolder.statusColor.setBackgroundResource(R.color.Holo_Blue_Dark);
                }
                else
                {
//                    viewHolder.layout.setBackgroundResource(R.color.Holo_Green_Light);
                    viewHolder.statusColor.setBackgroundResource(R.color.Holo_Green_Dark);
                }
            }

            viewHolder.ssid.setText(String.format("%s", ProxyUtils.cleanUpSSID(listItem.getSSID())));

            String sec = ProxyUtils.getSecurityString(listItem, ctx, true);
            if (sec != null && sec.length() > 0)
                viewHolder.security.setText(ProxyUtils.getSecurityString(listItem, ctx, true));
            else
                viewHolder.security.setText("");


            viewHolder.status.setText(listItem.toStatusString());
        }

//        LogWrapper.stopTrace(TAG, "getView - setValues", Log.ERROR);
//        LogWrapper.stopTrace(TAG, "getView", Log.ERROR);

        return view;
    }
}
