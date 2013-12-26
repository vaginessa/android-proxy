package com.lechucksoftware.proxy.proxysettings.components;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.MainActivity;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Marco on 01/12/13.
 */
public class InputField extends LinearLayout
{
    private ImageButton valueActionButton;
    private ImageView fieldActionButton;
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

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
        refreshUI();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
        refreshUI();
    }

    public String getValue()
    {
        return value;
    }

    public boolean isReadonly()
    {
        return readonly;
    }

    public void setReadonly(boolean readonly)
    {
        this.readonly = readonly;
        refreshUI();
    }

    public boolean isFullsize()
    {
        return fullsize;
    }

    public void setFullsize(boolean fullsize)
    {
        this.fullsize = fullsize;
        refreshUI();
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
        refreshUI();
    }

    public InputField(Context context)
    {
        super(context);

        title = "";
        hint = "";
        value = "";
        readonly = false;
        fullsize = false;
        type = 0;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.input, this);
        if (v != null)
        {
            getUIComponents(v);
        }
    }

    public InputField(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.input, this);
        if (v != null)
        {
            getUIComponents(v);
        }
    }

    private void getUIComponents(View v)
    {
        titleTextView = (TextView) v.findViewById(R.id.field_title);
        valueReadOnlyTextView = (TextView) v.findViewById(R.id.field_value_readonly);
        valueEditText = (EditText) v.findViewById(R.id.field_value);
        valueEditText.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if (b && !TextUtils.isEmpty(value))
                {
//                    fieldActionButton.setOnClickListener(new OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View view)
//                        {
//                            value = null;
//                            valueActionButton.setOnClickListener(null);
//                            refreshUI();
//                        }
//                    });

                }
                else
                {
                    fieldActionButton.setOnClickListener(null);
                }

                refreshUI();
            }
        });
        valueEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                value = editable.toString();
            }
        });

        validationLayout = (ViewGroup) v.findViewById(R.id.field_validation);

        valueActionButton = (ImageButton) v.findViewById(R.id.field_input_action);
        fieldActionButton = (ImageButton) v.findViewById(R.id.field_action);

        refreshUI();
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
            // DO NOTHING
        }
        else
        {
            value = newValue;
            refreshUI();
        }
    }

    public void setError(java.lang.CharSequence error)
    {
        Crouton c = Crouton.makeText((Activity) getContext(), error, Style.ALERT, validationLayout);
        c.setConfiguration(new Configuration.Builder().setDuration(2000).build());
        c.show();
    }

    public void addTextChangedListener(TextWatcher watcher)
    {
        valueEditText.addTextChangedListener(watcher);
    }

    public void refreshUI()
    {
        if (!TextUtils.isEmpty(title))
        {
            titleTextView.setText(title.toUpperCase());
        }

        titleTextView.setVisibility(UIUtils.booleanToVisibility(fullsize));

        if (!TextUtils.isEmpty(value))
        {
            valueReadOnlyTextView.setText(value);
            valueEditText.setText(value);
        }
        else
        {
            valueEditText.setHint(hint);
            valueEditText.setText(value);
            valueReadOnlyTextView.setText("NOT SET");
        }

        valueReadOnlyTextView.setVisibility(UIUtils.booleanToVisibility(readonly));
        valueEditText.setVisibility(UIUtils.booleanToVisibility(!readonly));

        fieldActionButton.setVisibility(UIUtils.booleanToVisibility(fieldActionButton.hasOnClickListeners()));
        valueActionButton.setVisibility(UIUtils.booleanToVisibility(valueActionButton.hasOnClickListeners()));

        switch (type)
        {
            case 1:
                valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case 0:
            default:
                valueEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                break;
        }

        valueEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }
}
