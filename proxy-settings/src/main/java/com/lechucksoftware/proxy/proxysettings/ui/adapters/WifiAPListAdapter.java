package com.lechucksoftware.proxy.proxysettings.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.components.WifiSignal;

import java.util.List;

import be.shouldit.proxy.lib.WiFiAPConfig;

public class WifiAPListAdapter extends ArrayAdapter<WiFiAPConfig>
{
    private static String TAG = WifiAPListAdapter.class.getSimpleName();

    private final LayoutInflater inflater;
    private Context ctx;

    ApViewHolder viewHolder;

    public WifiAPListAdapter(Context context)
    {
        super(context, R.layout.wifi_ap_list_item);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx = context;
    }

    static class ApViewHolder
    {
        TextView ssid;
        TextView status;
        WifiSignal security;
    }

    public void setData(List<WiFiAPConfig> confList)
    {
        App.getLogger().startTrace(TAG, "setData", Log.INFO);

        Boolean needsRefresh = false;

        //TODO: Needed refactoring here!!
        // * The check if the order of SSID works
        // * The check if a proxy configuration is changed without affecting the order doesn't work,
        //   since the configuration inside the adapter is the same of the configuration passed to it
        //   since they take this configuration from the in memory WifiNetworksManager

//        if (this.getCount() == confList.size())
//        {
//            for (int i = 0; i < this.getCount(); i++)
//            {
//                WiFiAPConfig conf = this.getItem(i);
//
//                if (conf.getSSID().compareTo(confList.get(i).getSSID()) != 0)
//                {
//                    // Changed the SSIDs order
//                    App.getLogger().d(TAG,String.format("setData order: Expecting %s, Found %s", confList.get(i).getSSID(), conf.getSSID()));
//                    needsRefresh = true;
//                    break;
//                }
//                else if (!conf.isSameConfiguration(confList.get(i)))
//                {
//                    // Same SSID order, but different configuration
//                    App.getLogger().d(TAG,String.format("setData configuration changed: Expecting %s, Found %s", confList.get(i), conf));
//                    needsRefresh = true;
//                    break;
//                }
//            }
//        }
//        else
//        {
            needsRefresh = true;
//        }

        if (needsRefresh)
        {
            App.getLogger().d(TAG,"Adapter need to refresh its data");

            setNotifyOnChange(false);
            clear();
            addAll(confList);
            // note that a call to notifyDataSetChanged() implicitly sets the setNotifyOnChange back to 'true'!
            // That's why the call 'setNotifyOnChange(false) should be called first every time (see call before 'clear()').
            notifyDataSetChanged();

            App.getLogger().d(TAG,"setData - notifyDataSetChanged");
        }

        App.getLogger().stopTrace(TAG, "setData", Log.INFO);
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.wifi_ap_list_item, parent, false);

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

        WiFiAPConfig listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.security.setConfiguration(listItem);

            if (listItem.isReachable())
            {
                viewHolder.security.setAlpha(1f);
                viewHolder.ssid.setAlpha(1f);
                viewHolder.status.setAlpha(1f);
            }
            else
            {
                float alpha = 0.7f;
                viewHolder.security.setAlpha(alpha);
                viewHolder.ssid.setAlpha(alpha);
                viewHolder.status.setAlpha(alpha);
            }

            viewHolder.ssid.setText(listItem.getSSID());

            viewHolder.status.setText(listItem.toStatusString());

        }

        return view;
    }
}
