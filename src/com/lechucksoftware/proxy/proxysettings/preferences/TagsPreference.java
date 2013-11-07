package com.lechucksoftware.proxy.proxysettings.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.TagsView;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;
import com.shouldit.proxy.lib.CheckStatusValues;
import com.shouldit.proxy.lib.ProxyStatusItem;

import java.util.List;

public class TagsPreference extends DialogPreference
{
    private TagsView tagsView;
    private List<DBTag> _tags;

    public TagsPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
        setLayoutResource(R.layout.tags_preference);
        setWidgetLayoutResource(android.R.string.ok);
        setWidgetLayoutResource(android.R.string.cancel);
        setDialogIcon(null);
	}

    @Override
    protected void onBindView(View view)
    {
        super.onBindView(view);

        tagsView = (TagsView) view.findViewById(R.id.preference_proxy_tags);
        refreshUI();
    }

    public void setTags(List<DBTag> tags)
    {
        _tags = tags;
        refreshUI();
    }

    private void refreshUI()
    {
        if (tagsView != null && _tags != null)
            tagsView.setTags(_tags);
    }
}