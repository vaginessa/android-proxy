package com.lechucksoftware.proxy.proxysettings.preferences;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.components.TagsView;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;

import java.util.List;

public class TagsPreference extends Preference
{
    private TagsView tagsView;
    private List<TagEntity> tags;
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

    public void setTags(ProxyEntity proxy)
    {
        if (proxy != null)
        {
            tags = proxy.getTags();
        }
        else
        {
            tags = null;
        }

        refreshUI();
    }

    private void refreshUI()
    {
        if (tagsView != null)
        {
            summary.setEnabled(this.isEnabled());
            tagsView.setEnabled(this.isEnabled());

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