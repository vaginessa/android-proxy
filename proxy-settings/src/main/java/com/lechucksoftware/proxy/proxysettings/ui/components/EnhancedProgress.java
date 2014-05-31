package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;

/**
 * Created by Marco on 31/05/14.
 */
public class EnhancedProgress extends LinearLayout
{
    private ViewGroup layout;
    private String text;
    private TextView progressText;

    public EnhancedProgress(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.progress, this);

        if (inflater != null)
        {
            getUI(v);
            refreshUI();
        }
    }

    public void getUI(View v)
    {
        layout = (ViewGroup) v.findViewById(R.id.ap_layout);
        progressText = (TextView) v.findViewById(R.id.progressText);
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.EnhancedProgress);

        try
        {
            text = a.getString(R.styleable.EnhancedProgress_progressText);
        }
        finally
        {
            a.recycle();
        }
    }

    private void refreshUI()
    {
        progressText.setText(text);
    }
}
