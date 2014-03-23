package com.lechucksoftware.proxy.proxysettings.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.List;

public class TagsListAdapter extends ArrayAdapter<TagEntity>
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
        ViewGroup layout;
        CheckBox checkBox;
    }

    public void setData(List<TagEntity> confList)
    {
        clear();
        if (confList != null)
        {
            for (TagEntity conf : confList)
            {
                add(conf);
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ApViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = vi.inflate(R.layout.tags_dialog_list_item, null);


            viewHolder = new ApViewHolder();
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.li_tag_checkbox);
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.li_tag_layout);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ApViewHolder) convertView.getTag();
        }

        final TagEntity listItem = getItem(position);

        if (listItem != null)
        {
            viewHolder.checkBox.setText(listItem.tag);
            viewHolder.checkBox.setChecked(listItem.isSelected);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    CheckBox checkBox = (CheckBox) view;
                    if (checkBox.isChecked())
                    {
                        listItem.isSelected = true;
                    }
                    else
                    {
                        listItem.isSelected = false;
                    }
                }
            });

            viewHolder.checkBox.setBackgroundColor(UIUtils.getTagsColor(getContext(), listItem.tagColor));
        }

        return convertView;
    }
}
