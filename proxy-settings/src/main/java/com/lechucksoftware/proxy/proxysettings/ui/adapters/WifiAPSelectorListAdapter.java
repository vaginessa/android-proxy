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

public class WifiAPSelectorListAdapter extends ArrayAdapter<WiFiAPConfig>
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

    public void setData(List<WiFiAPConfig> confList)
    {
        App.getLogger().startTrace(TAG, "setData", Log.INFO);

        Boolean needsRefresh = false;

        if (this.getCount() == confList.size())
        {
            for (int i = 0; i < this.getCount(); i++)
            {
                WiFiAPConfig conf = this.getItem(i);

                // Compare if it's changed the SSIDs order
                if (conf.ssid.compareTo(confList.get(i).ssid) != 0)
                {
                    App.getLogger().d(TAG,String.format("setData order: Expecting %s, Found %s",confList.get(i).ssid, conf.ssid));
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

        WiFiAPConfig listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.security.setConfiguration(listItem);
            viewHolder.ssid.setText(listItem.getAPDescription());
            viewHolder.status.setText(listItem.toStatusString());
        }

        return view;
    }
}
