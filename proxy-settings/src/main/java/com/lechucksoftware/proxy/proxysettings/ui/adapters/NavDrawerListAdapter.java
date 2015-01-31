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

import java.util.List;
import java.util.Map;

public class NavDrawerListAdapter extends BaseAdapter
{
    private Context context;
    private Map<NavigationAction,NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, Map<NavigationAction,NavDrawerItem> navDrawerItems)
    {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
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

        if (navDrawerItems.containsKey(action))
            return navDrawerItems.get(action);
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

        NavigationAction action = NavigationAction.parseInt(position);
        NavDrawerItem item = navDrawerItems.get(action);

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
//        TextView txtTag = (TextView) convertView.findViewById(R.id.tag);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        imgIcon.setImageResource(item.getIcon());
        txtTitle.setText(item.getTitle());
//        txtTag.setText(navDrawerItems.get(position).getTag());

        // displaying count
        // check whether it set visible or not
        if (item.getCounterVisibility())
        {
            txtCount.setText(item.getCount());
        }
        else
        {
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }

        return convertView;
    }

}