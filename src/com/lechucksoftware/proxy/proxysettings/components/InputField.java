package com.lechucksoftware.proxy.proxysettings.components;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Marco on 01/12/13.
 */
public class InputField extends LinearLayout
{
    private ViewGroup validationLayout;
    private EditText valueEditText;
    private TextView valueReadOnlyTextView;
    private TextView titleTextView;
    private String title;
    private String hint;
    private String value;
    private boolean readonly;
    private boolean fullsize;
    private int type;

    public InputField(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.input, this);

        if (v != null)
        {
            titleTextView = (TextView) v.findViewById(R.id.field_title);
            valueReadOnlyTextView = (TextView) v.findViewById(R.id.field_value_readonly);
            valueEditText = (EditText) v.findViewById(R.id.field_value);
            validationLayout = (ViewGroup) v.findViewById(R.id.field_validation);

            refreshUI();
        }
    }

    protected void readStyleParameters(Context context, AttributeSet attributeSet)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.InputField);

        try
        {
            title = a.getString(R.styleable.InputField_title);
            hint = a.getString(R.styleable.InputField_hint);
            value = a.getString(R.styleable.InputField_value);
            readonly = a.getBoolean(R.styleable.InputField_readonly, false);
            fullsize = a.getBoolean(R.styleable.InputField_fullsize, false);
            type = a.getInt(R.styleable.InputField_inputType, 0);
        }
        finally
        {
            a.recycle();
        }
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        readonly = !enabled;
        refreshUI();
    }

    public void setValue(Object text)
    {
        String newValue = text.toString();
        if (newValue.equals(value))
        {

        }
        else
        {
            value = newValue;
            refreshUI();
        }
    }

    public void setError(java.lang.CharSequence error)
    {
        Crouton c = Crouton.makeText((Activity)getContext(), error, Style.ALERT, validationLayout);
        c.setConfiguration(new Configuration.Builder().setDuration(2000).build());
        c.show();
    }

    public void addTextChangedListener(TextWatcher watcher)
    {
        valueEditText.addTextChangedListener(watcher);
    }

    public void refreshUI()
    {
        if (fullsize || readonly)
        {
            titleTextView.setText(title);
            titleTextView.setVisibility(VISIBLE);
        }
        else
        {
            titleTextView.setVisibility(GONE);
        }

        if (value != null && value.length() > 0)
        {
            valueReadOnlyTextView.setText(value);
            valueEditText.setText(value);
        }
        else
        {
            valueEditText.setHint(hint);
            valueReadOnlyTextView.setText("NOT SET");
        }

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

        switch (type)
        {
            case 1:
                valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case 0:
            default:
                valueEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }
}
