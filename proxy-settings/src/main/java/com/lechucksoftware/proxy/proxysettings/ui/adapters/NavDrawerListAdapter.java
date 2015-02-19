package com.lechucksoftware.proxy.proxysettings.ui.adapters;

/**
 * Created by mpagliar on 21/10/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.NavigationAction;
import com.lechucksoftware.proxy.proxysettings.ui.components.NavDrawerItem;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.List;

public class NavDrawerListAdapter extends BaseAdapter
{
    private Context context;
    private List<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context)
    {
        this.context = context;
    }

    public void setData(List<NavDrawerItem> items)
    {
        this.navDrawerItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        NavigationAction action = NavigationAction.parseInt(position);

        if (navDrawerItems.size() >= position)
            return navDrawerItems.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        NavDrawerItem item = navDrawerItems.get(position);

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        if (item.getIcon() != -1)
        {
            imgIcon.setImageResource(item.getIcon());
        }

        txtTitle.setText(item.getTitle());
        txtCount.setVisibility(UIUtils.booleanToVisibility(item.getCount() > 0));
        txtCount.setText(String.valueOf(item.getCount()));

        return convertView;
    }

}