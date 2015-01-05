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
import timber.log.Timber;

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
        App.getTraceUtils().startTrace(TAG, "setData", Log.INFO);

        Boolean needsListReplace = false;

        if (this.getCount() == confList.size())
        {
            // Check if the order of SSID is changed
            for (int i = 0; i < this.getCount(); i++)
            {
                WiFiAPConfig conf = this.getItem(i);

                if (conf.getSSID().compareTo(confList.get(i).getSSID()) != 0)
                {
                    // Changed the SSIDs order
                    Timber.d("setData order: Expecting %s, Found %s", confList.get(i).getSSID(), conf.getSSID());
                    needsListReplace = true;
                    break;
                }
            }
        }
        else
        {
            needsListReplace = true;
        }

        App.getTraceUtils().partialTrace(TAG,"setData","Checked if adapter list needs replace",Log.DEBUG);

        if (needsListReplace)
        {
            setNotifyOnChange(false);
            clear();
            addAll(confList);
            App.getTraceUtils().partialTrace(TAG,"setData","Replaced adapter list items",Log.DEBUG);

            // note that a call to notifyDataSetChanged() implicitly sets the setNotifyOnChange back to 'true'!
            // That's why the call 'setNotifyOnChange(false) should be called first every time (see call before 'clear()').
            notifyDataSetChanged();
            App.getTraceUtils().partialTrace(TAG,"setData","notifyDataSetChanged",Log.DEBUG);
        }
        else
        {
            // Just notifyDataSetChanged
            notifyDataSetChanged();
            App.getTraceUtils().partialTrace(TAG,"setData","notifyDataSetChanged",Log.DEBUG);
        }

        App.getTraceUtils().stopTrace(TAG, "setData", Log.INFO);
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
