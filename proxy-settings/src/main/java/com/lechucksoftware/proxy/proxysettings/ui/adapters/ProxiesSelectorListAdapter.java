package com.lechucksoftware.proxy.proxysettings.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.lang.reflect.Proxy;
import java.util.List;

import be.shouldit.proxy.lib.WiFiAPConfig;

public class ProxiesSelectorListAdapter extends ArrayAdapter<ProxyEntity>
{
    private static final String TAG = ProxiesSelectorListAdapter.class.getSimpleName();
    private final LayoutInflater vi;
    private Context ctx;

    public ProxiesSelectorListAdapter(Context context)
    {
        super(context, R.layout.proxy_list_item);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx = context;
    }

    static class ApViewHolder
    {
        TextView host;
        TextView port;
        TextView bypass;
//        TagsView tags;
        TextView used;
    }

    public void setData(List<ProxyEntity> confList)
    {
        App.getLogger().startTrace(TAG, "setData", Log.INFO);

        Boolean needsListReplace = false;

        if (this.getCount() == confList.size())
        {
            // Check if the order of SSID is changed
            for (int i = 0; i < this.getCount(); i++)
            {
                ProxyEntity adapterProxyItem = this.getItem(i);
                ProxyEntity newProxyItem = confList.get(i);

                if (!adapterProxyItem.equals(newProxyItem))
                {
                    // Changed the Proxies order
                    App.getLogger().d(TAG,String.format("setData order: Expecting %s, Found %s", newProxyItem, adapterProxyItem));
                    needsListReplace = true;
                    break;
                }
            }
        }
        else
        {
            needsListReplace = true;
        }

        if (needsListReplace)
        {
            setNotifyOnChange(false);
            clear();
            addAll(confList);
            App.getLogger().partialTrace(TAG,"setData","Replaced adapter list items",Log.DEBUG);

            // note that a call to notifyDataSetChanged() implicitly sets the setNotifyOnChange back to 'true'!
            // That's why the call 'setNotifyOnChange(false) should be called first every time (see call before 'clear()').
            notifyDataSetChanged();
            App.getLogger().partialTrace(TAG,"setData","notifyDataSetChanged",Log.DEBUG);
        }
        else
        {
            // Just notifyDataSetChanged
            notifyDataSetChanged();
            App.getLogger().partialTrace(TAG,"setData","notifyDataSetChanged",Log.DEBUG);
        }

        App.getLogger().stopTrace(TAG, "setData", Log.INFO);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ApViewHolder viewHolder;
        View view = convertView;

        if (view == null)
        {
            view = vi.inflate(R.layout.proxy_list_item, parent, false);

            viewHolder = new ApViewHolder();
            viewHolder.host = (TextView) view.findViewById(R.id.list_item_proxy_host);
            viewHolder.port = (TextView) view.findViewById(R.id.list_item_proxy_port);
            viewHolder.bypass = (TextView) view.findViewById(R.id.list_item_proxy_bypass);
//            viewHolder.tags = (TagsView) view.findViewById(R.id.list_item_proxy_tags);
            viewHolder.used = (TextView) view.findViewById(R.id.li_proxy_used_txt);

            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ApViewHolder) view.getTag();
        }

        ProxyEntity listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.host.setText(listItem.getHost());
            viewHolder.port.setText(listItem.getPort().toString());

            viewHolder.bypass.setText(getContext().getString(R.string.bypass_for) + " " +  listItem.getExclusion());
            viewHolder.bypass.setVisibility(UIUtils.booleanToVisibility(!TextUtils.isEmpty(listItem.getExclusion())));
//            viewHolder.tags.setTags(listItem.getTags());
            viewHolder.used.setText(String.valueOf(listItem.getUsedByCount()));
            viewHolder.used.setVisibility(UIUtils.booleanToVisibility(listItem.getInUse()));
        }

        return view;
    }
}
