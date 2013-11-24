package com.lechucksoftware.proxy.proxysettings.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.TagsView;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;
import com.lechucksoftware.proxy.proxysettings.fragments.TagsListSelectorFragment;

import java.util.List;

public class TagsPreference extends Preference
{
    private TagsView tagsView;
    private List<DBTag> tags;
    private TextView summary;

    public TagsPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setLayoutResource(R.layout.tags_preference);
    }

    @Override
    protected void onBindView(View view)
    {
        super.onBindView(view);

        summary = (TextView) view.findViewById(android.R.id.summary);
        tagsView = (TagsView) view.findViewById(R.id.preference_proxy_tags);

        refreshUI();
    }

    public void setTags(DBProxy proxy)
    {
        if (proxy != null)
        {
            tags = proxy.getTags();
        }

        refreshUI();
    }

    private void refreshUI()
    {
        if (tagsView != null)
        {
            if (tags != null)
            {
                tagsView.setTags(tags);
                tagsView.setVisibility(View.VISIBLE);
                summary.setVisibility(View.GONE);
            }
            else
            {
                tagsView.setVisibility(View.GONE);
                summary.setVisibility(View.VISIBLE);
                summary.setText(R.string.not_set);
            }
        }
    }
}