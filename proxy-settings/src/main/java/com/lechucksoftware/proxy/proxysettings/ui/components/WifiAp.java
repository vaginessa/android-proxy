package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;

import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by marco on 02/12/13.
 */
public class WifiAp extends LinearLayout
{
    private WiFiApConfig wifiApConfig;

    @InjectView(R.id.wifi_name) TextView wifiName;
//    @InjectView(R.id.wifi_security) TextView wifiSecurity;
//    @InjectView(R.id.wifi_status) TextView wifiStatus;
    @InjectView(R.id.wifi_ap_signal_icon) WifiSignal wifiSignal;

    public WifiAp(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.wifi_ap, this);
        ButterKnife.inject(this, v);
    }


    private void readStyleParameters(Context context, AttributeSet attributeSet)
    {
//        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.WifiSignal);
//
//        try
//        {
//            text = a.getString(R.styleable.WifiSignal_text);
//        }
//        finally
//        {
//            a.recycle();
//        }
    }

    private void refreshUI()
    {
        wifiName.setText(ProxyUtils.cleanUpSSID(wifiApConfig.getSSID()));
//        wifiStatus.setText(wifiApConfig.getProxyStatusString());

//        String securityString = ProxyUtils.getSecurityString(wifiApConfig, getContext(), true);
//
//        if (!TextUtils.isEmpty(securityString))
//        {
//            wifiStatus.setText(getContext().getString(R.string.security,securityString));
//        }
//        else
//        {
//            wifiStatus.setText("");
//        }

//        String sec = ProxyUtils.getSecurityString(configuration, getContext(), true);
//        if (!TextUtils.isEmpty(sec))
//        {
//            securityTextView.setText(sec);
//        }
//        else
//        {
//            securityTextView.setText("");
//        }
//
//        if (configuration.getLevel() == -1)
//        {
//            iconImageView.setImageResource(R.drawable.ic_action_nowifi);
//            layout.setBackgroundResource(R.color.DarkGrey);
//        }
//        else
//        {
//            iconImageView.setImageLevel(configuration.getLevel());
//            iconImageView.setImageResource(R.drawable.wifi_signal);
//            iconImageView.setImageState((configuration.getSecurityType() != SecurityType.SECURITY_NONE) ? AccessPoint.STATE_SECURED : AccessPoint.STATE_NONE, true);
//
//            if (configuration.isActive())
//            {
//                layout.setBackgroundResource(R.color.Holo_Blue_Dark);
//            }
//            else
//            {
//                layout.setBackgroundResource(R.color.Holo_Green_Dark);
//            }
//        }
    }

    public void setConfiguration(WiFiApConfig configuration)
    {
        wifiApConfig = configuration;
        wifiSignal.setConfiguration(wifiApConfig);

        refreshUI();
    }
}

