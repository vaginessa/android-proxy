package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;

import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.AccessPoint;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.utils.ProxyUtils;

/**
 * Created by marco on 02/12/13.
 */
public class WifiSignal extends LinearLayout
{
    private ViewGroup layout;
    private ImageView iconImageView;
    private TextView securityTextView;

    private String text;
    private WiFiAPConfig configuration;

    public WifiSignal(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.wifi_signal, this);

        if (inflater != null)
        {
            layout = (ViewGroup) v.findViewById(R.id.ap_layout);
            iconImageView = (ImageView) v.findViewById(R.id.ap_icon);
            securityTextView = (TextView) v.findViewById(R.id.ap_security);
        }
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.WifiSignal);

        try
        {
            text = a.getString(R.styleable.WifiSignal_text);
        }
        finally
        {
            a.recycle();
        }
    }

    private void refreshUI()
    {
        String sec = ProxyUtils.getSecurityString(configuration, getContext(), true);
        if (!TextUtils.isEmpty(sec))
            securityTextView.setText(sec);
        else
            securityTextView.setText("");

        if (configuration.getLevel() == -1)
        {
            iconImageView.setImageResource(R.drawable.ic_action_notvalid);
            layout.setBackgroundResource(R.color.DarkGrey);
        }
        else
        {
            iconImageView.setImageLevel(configuration.getLevel());
            iconImageView.setImageResource(R.drawable.wifi_signal);
            iconImageView.setImageState((configuration.security != SecurityType.SECURITY_NONE) ? AccessPoint.STATE_SECURED : AccessPoint.STATE_NONE, true);

            if (configuration.isCurrentNetwork())
            {
                layout.setBackgroundResource(R.color.Holo_Blue_Dark);
            }
            else
            {
                layout.setBackgroundResource(R.color.Holo_Green_Dark);
            }
        }
    }

    public void setConfiguration(WiFiAPConfig configuration)
    {
        this.configuration = configuration;
        refreshUI();
    }
}

