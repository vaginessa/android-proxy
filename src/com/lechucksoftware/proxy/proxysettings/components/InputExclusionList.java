package com.lechucksoftware.proxy.proxysettings.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.utils.ProxyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marco on 08/12/13.
 */
public class InputExclusionList extends LinearLayout
{
    private TextView emptyTextView;
    private LinearLayout bypassContainer;
    private TextView titleTextView;
    private String title;
    private boolean fullsize;
    private boolean readonly;
    private String exclusionListString = "";
    private List<String> exclusionList;

    public InputExclusionList(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.input_exclusion, this);

        if (v != null)
        {
            titleTextView = (TextView) v.findViewById(R.id.field_title);

            bypassContainer = (LinearLayout) v.findViewById(R.id.bypass_container);
            bypassContainer.removeAllViews();

            emptyTextView = (TextView) v.findViewById(R.id.field_empty);

            refreshUI();
        }
    }

    private void refreshUI()
    {
        if (!TextUtils.isEmpty(title))
        {
            titleTextView.setText(title.toUpperCase());
        }

        if (exclusionList != null && exclusionList.size() > 0)
        {
            bypassContainer.removeAllViews();

            if (fullsize)
            {
                for(String bypass : exclusionList)
                {
                    InputField i = new InputField(getContext());
                    i.setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            ((InputField)view).setEnabled(true);
                        }
                    });

                    i.setReadonly(true);
                    i.setValue(bypass);
                    bypassContainer.addView(i);
                }

                if (!readonly)
                {
                    // Always add the new empty field
                    InputField i = new InputField(getContext());
                    i.setHint("Add bypass address");
                    i.setReadonly(readonly);
                    bypassContainer.addView(i);
                }
            }
            else
            {
                InputField i = new InputField(getContext());
                i.setReadonly(true);
                i.setValue(exclusionListString);
                bypassContainer.addView(i);
            }

            bypassContainer.setVisibility(VISIBLE);
            emptyTextView.setVisibility(GONE);
        }
        else
        {
            bypassContainer.setVisibility(GONE);
            emptyTextView.setVisibility(VISIBLE);
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

    public void setExclusionString(String exclusionString)
    {
        if (!exclusionListString.equals(exclusionString))
        {
            exclusionListString = exclusionString;

            if (!TextUtils.isEmpty(exclusionListString))
            {
                exclusionList = new ArrayList<String>();
                exclusionList.addAll(Arrays.asList(ProxyUtils.parseExclusionList(exclusionListString)));
            }
            else
            {
                exclusionList = null;
            }

            refreshUI();
        }
        else
        {
            // DO Nothing: No need to update UI
        }
    }

    public String getExclusionList()
    {
        String result = TextUtils.join(",",exclusionList);
        return result;
    }
}
