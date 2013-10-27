package com.lechucksoftware.proxy.proxysettings.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.List;

/**
 * Created by marco on 12/09/13.
 */
public class TagsView extends LinearLayout
{
    private LinearLayout tagsContainer;

    public TagsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = null;

        if (inflater != null)
        {
            v = inflater.inflate(R.layout.tags, this);
            tagsContainer = (LinearLayout) v.findViewById(R.id.tags_container);
        }
    }

    public void setTags(List<DBTag> tags)
    {
        if (tagsContainer != null)
        {
            tagsContainer.removeAllViews();

            if (tags.size() > 0)
            {
                for (DBTag tag : tags)
                {
                    TextView t = new TextView(getContext());
                    t.setBackgroundColor(UIUtils.getTagsColor(getContext(), tag.tagColor));
                    t.setPadding(2,2,2,2);
                    t.setText(tag.tag);
                    tagsContainer.addView(t);
                }
            }
        }
    }
}
