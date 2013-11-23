package com.lechucksoftware.proxy.proxysettings.adapters;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.TagsView;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;

import java.util.List;

public class TagsListAdapter extends ArrayAdapter<DBTag>
{
    private final LayoutInflater vi;
    private Context ctx;

    public TagsListAdapter(Context context)
    {
        super(context, R.layout.tags_dialog_list_item);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx = context;
    }

    static class ApViewHolder
    {
        CheckBox checkBox;
    }

    public void setData(List<DBTag> confList)
    {
        clear();
        if (confList != null)
        {
            for (DBTag conf : confList)
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
            view = vi.inflate(R.layout.tags_dialog_list_item, null);

            viewHolder = new ApViewHolder();
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.li_tag_checkbox);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ApViewHolder) view.getTag();
        }

        DBTag listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.checkBox.setText(listItem.tag);
            viewHolder.checkBox.setChecked(false);
        }

        return view;
    }
}
