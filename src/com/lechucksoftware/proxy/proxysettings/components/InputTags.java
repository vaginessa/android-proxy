package com.lechucksoftware.proxy.proxysettings.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;

import java.util.List;

/**
 * Created by Marco on 08/12/13.
 */
public class InputTags extends LinearLayout
{
    private Button addTagsButton;
    private TagsView tagsView;
    private TextView titleTextView;
    private String title;
    private boolean fullsize;
    private List<TagEntity> tags;
    private boolean readonly;

    public InputTags(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.input_tags, this);

        if (v != null)
        {
            titleTextView = (TextView) v.findViewById(R.id.field_title);
            addTagsButton = (Button) v.findViewById(R.id.field_add_tags);
            tagsView = (TagsView) v.findViewById(R.id.field_tags);
        }
    }

    public void setTags(List<TagEntity> intags)
    {
        if (tags == null || !tags.equals(intags))
        {
            tags = intags;
            refreshUI();
        }
        else
        {
            // DO Nothings: tags list already updated
        }

    }

    private void refreshUI()
    {
        titleTextView.setText(title);

        tagsView.setTags(tags);
        if (tags != null && tags.size() > 0)
        {
            tagsView.setVisibility(VISIBLE);
            addTagsButton.setVisibility(GONE);
        }
        else
        {
            tagsView.setVisibility(GONE);
            addTagsButton.setVisibility(VISIBLE);
        }
    }

    protected void readStyleParameters(Context context, AttributeSet attributeSet)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.InputFieldTags);

        try
        {
            title = a.getString(R.styleable.InputField_title);
            fullsize = a.getBoolean(R.styleable.InputField_fullsize, false);
            readonly = a.getBoolean(R.styleable.InputField_readonly, false);
        }
        finally
        {
            a.recycle();
        }
    }
}
