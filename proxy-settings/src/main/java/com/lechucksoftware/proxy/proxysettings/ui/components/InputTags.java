package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 08/12/13.
 */
public class InputTags extends LinearLayout
{
    private LinearLayout fieldMainLayout;
    private TextView noTagsTextView;
    private Button addTagsButton;
    private TagsView tagsView;
    private TextView titleTextView;
    private String title;
    private boolean fullsize;
    private List<TagEntity> tags;
    private boolean readonly;
    private boolean singleLine;
    private float titleSize;
    private float textSize;

    public InputTags(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.input_tags, this);

        if (v != null)
        {
            fieldMainLayout = (LinearLayout) v.findViewById(R.id.field_main_layout);
            titleTextView = (TextView) v.findViewById(R.id.field_title);
            noTagsTextView = (TextView) v.findViewById(R.id.field_no_tags);
            addTagsButton = (Button) v.findViewById(R.id.field_add_tags);
            tagsView = (TagsView) v.findViewById(R.id.field_tags);
        }
    }

    public void setTagsViewOnClickListener(OnClickListener onClickListener)
    {
        if (tagsView != null)
        {
            tagsView.setOnClickListener(onClickListener);
        }

        if (addTagsButton != null)
        {
            addTagsButton.setOnClickListener(onClickListener);
        }
    }

    public void setTags(List<TagEntity> intags)
    {
        if (tags == null || !tags.equals(intags))
        {
            if (intags != null)
                tags = new ArrayList<TagEntity>(intags);
            else
                tags = null;

            refreshUI();
        }
        else
        {
            // DO Nothings: tags list already updated
        }
    }

    private void refreshUI()
    {
        if (singleLine)
        {
            fieldMainLayout.setOrientation(HORIZONTAL);
        }
        else
        {
            fieldMainLayout.setOrientation(VERTICAL);
        }

        if (!TextUtils.isEmpty(title))
        {
            titleTextView.setText(title.toUpperCase());
        }

        if (tags != null && tags.size() > 0)
        {
            tagsView.setTags(tags);
            tagsView.setVisibility(VISIBLE);
//            addTagsButton.setVisibility(GONE);
            noTagsTextView.setVisibility(GONE);
        }
        else
        {
            tagsView.setTags(null);
            tagsView.setVisibility(GONE);

            noTagsTextView.setVisibility(UIUtils.booleanToVisibility(readonly));
//            addTagsButton.setVisibility(UIUtils.booleanToVisibility(!readonly));
        }

        addTagsButton.setVisibility(UIUtils.booleanToVisibility(!readonly));
        titleTextView.setTextSize(titleSize);
        tagsView.setTextSize(textSize);
    }

    protected void readStyleParameters(Context context, AttributeSet attributeSet)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.InputFieldTags);

        try
        {
            title = a.getString(R.styleable.InputFieldTags_title);
            fullsize = a.getBoolean(R.styleable.InputFieldTags_fullsize, false);
            readonly = a.getBoolean(R.styleable.InputFieldTags_readonly, false);
            singleLine = a.getBoolean(R.styleable.InputFieldTags_singleLine, false);
            titleSize = a.getDimension(R.styleable.InputFieldTags_titleSize, (float) 16.0);
            textSize = a.getDimension(R.styleable.InputFieldTags_textSize, (float) 16.0);
        }
        finally
        {
            a.recycle();
        }
    }
}
