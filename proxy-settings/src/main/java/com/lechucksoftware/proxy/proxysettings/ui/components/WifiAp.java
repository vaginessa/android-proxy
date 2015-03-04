package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
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
    private Context context;
    private WiFiApConfig wifiApConfig;
    private StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

    @InjectView(R.id.wifi_name) TextView wifiName;
    @InjectView(R.id.wifi_status) TextView wifiStatus;
//    @InjectView(R.id.wifi_status) TextView wifiStatus;
    @InjectView(R.id.wifi_ap_signal_icon) WifiSignal wifiSignal;

    public WifiAp(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);

        context = ctx;
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

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        String securityTitle = context.getString(R.string.security);
        String securityString = ProxyUtils.getSecurityString(wifiApConfig, context, true);

//        if (!TextUtils.isEmpty(securityString))
//        {
//            wifiStatus.setText(getContext().getString(R.string.security,securityString));
//        }
//        else
//        {
//            wifiStatus.setText("");
//        }

        ssb.append(securityTitle);
        ssb.append("  " + securityString);
        ssb.setSpan(bss, 0, securityTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        wifiStatus.setText(ssb);


//        wifiStatus.setText(wifiApConfig.getProxyStatusString());

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

