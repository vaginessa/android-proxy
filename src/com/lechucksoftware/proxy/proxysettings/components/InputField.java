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
import com.lechucksoftware.proxy.proxysettings.constants.Measures;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.UUID;

/**
 * Created by Marco on 01/12/13.
 */
public class InputField extends LinearLayout
{
    private UUID id;
    private LinearLayout fieldMainLayout;
    //    private ImageButton valueActionButton;
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
    private boolean singleLine;
    private float textSize;
    private float titleSize;
    public boolean enableTextListener;
    private Object linkedObj;
//    private CharSequence emptyMessage;

    @Override
    public void setTag(Object obj)
    {
        super.setTag(obj);

        fieldMainLayout.setTag(obj);
        fieldActionButton.setTag(obj);
        validationLayout.setTag(obj);
        valueEditText.setTag(obj);
        valueReadOnlyTextView.setTag(obj);
        titleTextView.setTag(obj);
    }

    @Override
    public void setTag(int key, Object obj)
    {
        super.setTag(key, obj);

        fieldMainLayout.setTag(key, obj);
        fieldActionButton.setTag(key, obj);
        validationLayout.setTag(key, obj);
        valueEditText.setTag(key, obj);
        valueReadOnlyTextView.setTag(key, obj);
        titleTextView.setTag(key, obj);
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(CharSequence hint)
    {
        this.hint = hint.toString();
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

    public boolean isFullSize()
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

    public void setFieldAction(OnClickListener fieldAction)
    {
        fieldActionButton.setOnClickListener(fieldAction);
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
        titleSize = Measures.DefaultTitleSize;
        textSize = Measures.DefaultTextFontSize;
        id = UUID.randomUUID();
        enableTextListener = true;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.input, this);
        if (v != null)
        {
            getUIComponents(v);
            refreshUI();
        }
    }

    public InputField(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        id = UUID.randomUUID();
        enableTextListener = true;

        readStyleParameters(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.input, this);
        if (v != null)
        {
            getUIComponents(v);
            refreshUI();
        }
    }

    private void getUIComponents(View v)
    {
//        valueActionButton = (ImageButton) v.findViewById(R.id.field_input_action);
        fieldActionButton = (ImageButton) v.findViewById(R.id.field_action);

        fieldMainLayout = (LinearLayout) v.findViewById(R.id.field_main_layout);
        titleTextView = (TextView) v.findViewById(R.id.field_title);
        valueReadOnlyTextView = (TextView) v.findViewById(R.id.field_value_readonly);
        valueEditText = (EditText) v.findViewById(R.id.field_value);

        // TODO: Re eneable field action button with visibility triggered by focus change on EditText
//        valueEditText.setOnFocusChangeListener(new OnFocusChangeListener()
//        {
//            @Override
//            public void onFocusChange(View view, boolean b)
//            {
//                if (b && !TextUtils.isEmpty(value))
//                {
////                    fieldActionButton.setOnClickListener(new OnClickListener()
////                    {
////                        @Override
////                        public void onClick(View view)
////                        {
////                            value = null;
////                            valueActionButton.setOnClickListener(null);
////                            refreshUI();
////                        }
////                    });
//
//                }
//                else
//                {
//                    fieldActionButton.setOnClickListener(null);
//                }
//
//                refreshUI();
//            }
//        });

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
            singleLine = a.getBoolean(R.styleable.InputField_singleLine, false);
            titleSize = a.getDimension(R.styleable.InputField_titleSize, Measures.DefaultTitleSize);
            textSize = a.getDimension(R.styleable.InputField_textSize, Measures.DefaultTextFontSize);
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

    public void setValue(Object obj)
    {
        enableTextListener = false;

        linkedObj = obj;

        String newValue = "";

        if (obj != null)
        {
             newValue = obj.toString();
        }

        if (newValue.equals(value))
        {
            // DO NOTHING
        }
        else
        {
            value = newValue;
            refreshUI();
        }
//        if (!TextUtils.isEmpty(value))
//        {
//            valueActionButton.setOnClickListener(new OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    value = "";
//                    refreshUI();
//                }
//            });
//        }
//        else
//        {
//            valueActionButton.setOnClickListener(null);
//        }

        enableTextListener = true;
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

        // Show actions button only when not in READONLY
        fieldActionButton.setVisibility(UIUtils.booleanToVisibility(!readonly && fieldActionButton.hasOnClickListeners()));
//        valueActionButton.setVisibility(UIUtils.booleanToVisibility(valueActionButton.hasOnClickListeners()));

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

        titleTextView.setTextSize(titleSize);

        valueReadOnlyTextView.setTextSize(textSize);
        valueEditText.setTextSize(textSize);
    }

    public UUID getUUID()
    {
        return id;
    }
}
