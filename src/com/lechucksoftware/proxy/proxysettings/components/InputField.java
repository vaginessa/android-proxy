package com.lechucksoftware.proxy.proxysettings.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;

/**
 * Created by Marco on 01/12/13.
 */
public class InputField extends LinearLayout
{
    private EditText valueEditText;
    private TextView valueReadOnlyTextView;
    private TextView titleTextView;
    private String title;
    private String hint;
    private String value;
    private boolean readonly;

    public InputField(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.from(context).inflate(R.layout.input, this);

        View v = inflater.inflate(R.layout.input, this);

        if (inflater != null)
        {
            titleTextView = (TextView) v.findViewById(R.id.field_title);
            valueReadOnlyTextView = (TextView) v.findViewById(R.id.field_value_readonly);
            valueEditText = (EditText) v.findViewById(R.id.field_value);

            if (title != null && title.length()>0)
            {
                titleTextView.setText(title);
                titleTextView.setVisibility(VISIBLE);
            }
            else
            {
                titleTextView.setVisibility(GONE);
            }

            valueReadOnlyTextView.setText(value);
            valueEditText.setHint(hint);
            valueEditText.setText(value);

            if (readonly)
            {
                valueReadOnlyTextView.setVisibility(VISIBLE);
                valueEditText.setVisibility(GONE);
            }
            else
            {
                valueEditText.setVisibility(VISIBLE);
                valueReadOnlyTextView.setVisibility(GONE);
            }


        }
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.InputField);

        try
        {
            title = a.getString(R.styleable.InputField_title);
            hint = a.getString(R.styleable.InputField_hint);
            value = a.getString(R.styleable.InputField_value);
            readonly = a.getBoolean(R.styleable.InputField_readonly, false);
        }
        finally
        {
            a.recycle();
        }
    }
}
