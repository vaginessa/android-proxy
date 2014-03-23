package com.lechucksoftware.proxy.proxysettings.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.List;

public class ProxiesSelectorListAdapter extends ArrayAdapter<ProxyEntity>
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
        TextView bypass;
//        TagsView tags;
        ImageView used;
    }

    public void setData(List<ProxyEntity> confList)
    {
        clear();
        if (confList != null)
        {
            for (ProxyEntity conf : confList)
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
            view = vi.inflate(R.layout.proxy_list_item, parent, false);

            viewHolder = new ApViewHolder();
            viewHolder.host = (TextView) view.findViewById(R.id.list_item_proxy_host);
            viewHolder.port = (TextView) view.findViewById(R.id.list_item_proxy_port);
            viewHolder.bypass = (TextView) view.findViewById(R.id.list_item_proxy_bypass);
//            viewHolder.tags = (TagsView) view.findViewById(R.id.list_item_proxy_tags);
            viewHolder.used = (ImageView) view.findViewById(R.id.li_proxy_used);

            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ApViewHolder) view.getTag();
        }

        ProxyEntity listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.host.setText(listItem.host);
            viewHolder.port.setText(listItem.port.toString());

            viewHolder.bypass.setText(getContext().getString(R.string.bypass_for) + " " +  listItem.exclusion);
            viewHolder.bypass.setVisibility(UIUtils.booleanToVisibility(!TextUtils.isEmpty(listItem.exclusion)));
//            viewHolder.tags.setTags(listItem.getTags());
            viewHolder.used.setVisibility(UIUtils.booleanToVisibility(listItem.getInUse()));
        }

        return view;
    }
}
