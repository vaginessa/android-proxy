package com.lechucksoftware.proxy.proxysettings.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.components.WifiSignal;

import java.util.List;

import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.utils.ProxyUtils;

public class WifiAPListAdapter extends ArrayAdapter<WiFiApConfig>
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
        TextView securityText;
        WifiSignal wifiSignal;
        ImageView proxySetting;
        LinearLayout wifiApLabel;
        LinearLayout wifiApLProxyLabel;
        TextView proxySettingText;
        RelativeLayout wifiApLayout;
    }

    public void setData(List<WiFiApConfig> confList)
    {
//        App.getTraceUtils().startTrace(TAG, "setData", Log.DEBUG);

        Boolean needsListReplace = false;

        if (this.getCount() == confList.size())
        {
            // Check if the order of SSID is changed
            for (int i = 0; i < this.getCount(); i++)
            {
                WiFiApConfig conf = this.getItem(i);

                if (conf.getSSID().compareTo(confList.get(i).getSSID()) != 0)
                {
                    // Changed the SSIDs order
//                    Timber.d("setData order: Expecting %s, Found %s", confList.get(i).getSSID(), conf.getSSID());
                    needsListReplace = true;
                    break;
                }
            }
        }
        else
        {
            needsListReplace = true;
        }

//        App.getTraceUtils().partialTrace(TAG,"setData","Checked if adapter list needs replace",Log.DEBUG);

        if (needsListReplace)
        {
            setNotifyOnChange(false);
            clear();
            addAll(confList);
//            App.getTraceUtils().partialTrace(TAG,"setData","Replaced adapter list items",Log.DEBUG);

            // note that a call to notifyDataSetChanged() implicitly sets the setNotifyOnChange back to 'true'!
            // That's why the call 'setNotifyOnChange(false) should be called first every time (see call before 'clear()').
            notifyDataSetChanged();
//            App.getTraceUtils().partialTrace(TAG,"setData","notifyDataSetChanged",Log.DEBUG);
        }
        else
        {
            // Just notifyDataSetChanged
            notifyDataSetChanged();
//            App.getTraceUtils().partialTrace(TAG,"setData","notifyDataSetChanged",Log.DEBUG);
        }

//        App.getTraceUtils().stopTrace(TAG, "setData", Log.DEBUG);
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.wifi_ap_list_item, parent, false);

            viewHolder = new ApViewHolder();
            viewHolder.ssid = (TextView) view.findViewById(R.id.wifi_ap_ssid);
            viewHolder.status = (TextView) view.findViewById(R.id.wifi_ap_proxy_status_text);
            viewHolder.wifiSignal = (WifiSignal) view.findViewById(R.id.wifi_ap_signal_icon);
            viewHolder.securityText = (TextView) view.findViewById(R.id.wifi_ap_security_text);
            viewHolder.proxySetting = (ImageView) view.findViewById(R.id.wifi_ap_proxy_settings_icon);
            viewHolder.wifiApLabel = (LinearLayout) view.findViewById(R.id.wifi_ap_label);
            viewHolder.wifiApLProxyLabel = (LinearLayout) view.findViewById(R.id.wifi_ap_proxy_label);
            viewHolder.proxySettingText = (TextView) view.findViewById(R.id.wifi_ap_proxy_settings_text);
            viewHolder.wifiApLayout = (RelativeLayout) view.findViewById(R.id.wifi_ap_layout);

            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ApViewHolder) view.getTag();
        }

        WiFiApConfig listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.wifiSignal.setConfiguration(listItem);

            int selectedColor = R.color.grey_600;
            if (listItem.getLevel() != -1)
            {
                if (listItem.isActive())
                {
                    selectedColor = R.color.blue_500;
                }
                else
                {
                    selectedColor = R.color.green_500;
                }
            }

            viewHolder.wifiApLabel.setBackgroundResource(selectedColor);
//            viewHolder.wifiApLayout.setBackgroundResource(selectedColor);

            float alpha = 1f;
            if (!listItem.isReachable())
            {
                alpha = 0.5f;
            }

            viewHolder.wifiSignal.setAlpha(alpha);
            viewHolder.wifiSignal.setAlpha(alpha);
            viewHolder.ssid.setAlpha(alpha);
            viewHolder.status.setAlpha(alpha);

            viewHolder.ssid.setText(listItem.getSSID());

            switch (listItem.getProxySetting())
            {
                case NONE:
                case UNASSIGNED:
                default:
                    viewHolder.wifiApLProxyLabel.setVisibility(View.GONE);
                    break;

                case STATIC:
                    viewHolder.wifiApLProxyLabel.setVisibility(View.VISIBLE);
                    viewHolder.wifiApLProxyLabel.setBackgroundResource(R.color.red_400);
                    viewHolder.proxySettingText.setText(R.string.static_p);
                    viewHolder.proxySetting.setImageResource(R.drawable.ic_action_proxy_dark);
                    break;

                case PAC:
                    viewHolder.wifiApLProxyLabel.setVisibility(View.VISIBLE);
                    viewHolder.wifiApLProxyLabel.setBackgroundResource(R.color.orange_400);
                    viewHolder.proxySettingText.setText(R.string.pac);
                    viewHolder.proxySetting.setImageResource(R.drawable.ic_action_file);
                    break;
            }

            viewHolder.status.setText(listItem.getProxyStatusString());
            viewHolder.securityText.setText(ProxyUtils.getSecurityString(listItem,ctx,true));
        }

        return view;
    }
}
