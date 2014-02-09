package com.lechucksoftware.proxy.proxysettings.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Measures;
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
    private LinearLayout fieldMainLayout;
    private TextView readonlyValueTextView;
    private LinearLayout bypassContainer;
    private TextView titleTextView;
    private String title;
    //    private boolean fullsize;
    private boolean readonly;
    private String exclusionListString = "";
    private List<String> exclusionList;
    private boolean singleLine;
    private float textSize;
    private float titleSize;

    public InputExclusionList(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.input_exclusion, this);

        if (v != null)
        {
            fieldMainLayout = (LinearLayout) v.findViewById(R.id.field_main_layout);
            titleTextView = (TextView) v.findViewById(R.id.field_title);
            bypassContainer = (LinearLayout) v.findViewById(R.id.bypass_container);
            bypassContainer.removeAllViews();
            readonlyValueTextView = (TextView) v.findViewById(R.id.field_value_readonly);

            refreshUI();
        }
    }

    private void refreshUI()
    {
        // Layout
        if (singleLine)
        {
            fieldMainLayout.setOrientation(HORIZONTAL);
            titleTextView.setWidth((int) UIUtils.convertDpToPixel(80, getContext()));
        }
        else
        {
            fieldMainLayout.setOrientation(VERTICAL);
            titleTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        // Title
        if (!TextUtils.isEmpty(title))
        {
            titleTextView.setText(title.toUpperCase());
        }

        if (readonly)
        {
            readonlyValueTextView.setVisibility(VISIBLE);
            bypassContainer.setVisibility(GONE);

            if (exclusionList != null && exclusionList.size() > 0)
            {
                readonlyValueTextView.setText(TextUtils.join("\n", exclusionList));
            }
            else
            {
                readonlyValueTextView.setText(R.string.not_set);
            }
        }
        else
        {
//            readonlyValueTextView.setVisibility(GONE);
            bypassContainer.setVisibility(VISIBLE);

            bypassContainer.removeAllViews();
            if (exclusionList != null && exclusionList.size() > 0)
            {
                for (String bypass : exclusionList)
                {
                    final InputField i = new InputField(getContext());

                    // TODO: Show inputfield readonly and enable the edit only on click
//                    i.setOnClickListener(new OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View view)
//                        {
//
//                        }
//                    });

                    i.setFullsize(false);
                    i.setReadonly(false);
                    i.setVisibility(VISIBLE);
                    i.setValue(bypass);
                    i.setFieldAction(new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            String addressToRemove = i.getValue();
                            exclusionList.remove(addressToRemove);
                            refreshUI();
                        }
                    });

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
        }

        titleTextView.setTextSize(titleSize);
        readonlyValueTextView.setTextSize(textSize);
    }

    protected void readStyleParameters(Context context, AttributeSet attributeSet)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.InputFieldTags);

        try
        {
            title = a.getString(R.styleable.InputField_title);
            singleLine = a.getBoolean(R.styleable.InputField_singleLine, false);
//            fullsize = a.getBoolean(R.styleable.InputField_fullsize, false);
            readonly = a.getBoolean(R.styleable.InputField_readonly, false);
            titleSize = a.getDimension(R.styleable.InputField_titleSize, Measures.DefaultTitleSize);
            textSize = a.getDimension(R.styleable.InputField_textSize, Measures.DefaultTextFontSize);
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
        String result = TextUtils.join(",", exclusionList);
        return result;
    }
}
