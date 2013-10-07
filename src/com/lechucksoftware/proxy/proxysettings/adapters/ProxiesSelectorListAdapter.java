package com.lechucksoftware.proxy.proxysettings.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyData;
import com.shouldit.proxy.lib.AccessPoint;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyUtils;
import com.shouldit.proxy.lib.SecurityType;

import java.util.ArrayList;
import java.util.List;

public class ProxiesSelectorListAdapter extends ArrayAdapter<ProxyData>
{
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
        TextView description;
    }

    public void setData(List<ProxyData> confList)
    {
        clear();
        if (confList != null)
        {
            for (ProxyData conf : confList)
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
            view = vi.inflate(R.layout.proxy_list_item, null);

            viewHolder = new ApViewHolder();
            viewHolder.host = (TextView) view.findViewById(R.id.list_item_proxy_host);
            viewHolder.port = (TextView) view.findViewById(R.id.list_item_proxy_port);
            viewHolder.description = (TextView) view.findViewById(R.id.list_item_proxy_description);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ApViewHolder) view.getTag();
        }

        ProxyData listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.host.setText(listItem.host);
            viewHolder.port.setText(listItem.port.toString());
            viewHolder.description.setText(listItem.description);
        }
        return view;
    }
}
