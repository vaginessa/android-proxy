package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lechucksoftware.proxy.proxysettings.R;

import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.enums.SecurityType;

/**
 * Created by marco on 02/12/13.
 */
public class WifiSignal extends LinearLayout
{
    private ViewGroup layout;
    private ImageView iconImageView;
//    private TextView securityTextView;

    private String text;
    private boolean showSecurityText;

    private WiFiApConfig configuration;
    private String securityString;


    public WifiSignal(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.wifi_signal, this);

        if (inflater != null)
        {
            layout = (ViewGroup) v.findViewById(R.id.wifi_proxy_layout);
            iconImageView = (ImageView) v.findViewById(R.id.wifi_ap_signal);
//            securityTextView = (TextView) v.findViewById(R.id.ap_security);
        }
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.WifiSignal);

        try
        {
            text = a.getString(R.styleable.WifiSignal_text);
            showSecurityText = a.getBoolean(R.styleable.WifiSignal_showSecurityText, true);
        }
        finally
        {
            a.recycle();
        }
    }

    private void refreshUI()
    {
        if (configuration == null || configuration.getLevel() == -1)
        {
            iconImageView.setImageResource(R.drawable.ic_action_nowifi);
        }
        else
        {
            iconImageView.setImageLevel(configuration.getLevel());
            iconImageView.setImageResource(R.drawable.wifi_signal);
            iconImageView.setImageState((configuration.getSecurityType() != SecurityType.SECURITY_NONE) ? WiFiApConfig.STATE_SECURED : WiFiApConfig.STATE_NONE, true);
        }
    }

    public void setConfiguration(WiFiApConfig configuration)
    {
        this.configuration = configuration;
        refreshUI();
    }
}

