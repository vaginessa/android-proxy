package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import timber.log.Timber;

/**
 * Created by Marco on 15/06/14.
 */
public class ActionsView extends LinearLayout
{
    private Button configureWifiActionBtn;
    private Button enableWifiActionBtn;
//    private Button airplaneModeActionBtn;

    public ActionsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        readStyleParameters(context,attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.actions_view, this);
        if (v != null)
        {
            getUIComponents(v);
            refreshUI();
        }
    }

    protected void readStyleParameters(Context context, AttributeSet attributeSet)
    {
//        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.InputField);

        try
        {
//            title = a.getString(R.styleable.InputField_title);
        }
        finally
        {
//            a.recycle();
        }
    }

    private void getUIComponents(View v)
    {
//        airplaneModeActionBtn = (Button) v.findViewById(R.id.airplane_mode_action_btn);
//        airplaneModeActionBtn.setOnClickListener(airplaneModeClickListener);

        enableWifiActionBtn = (Button) v.findViewById(R.id.enable_wifi_action_btn);
        enableWifiActionBtn.setOnClickListener(enableWifiClickListener);

        configureWifiActionBtn = (Button) v.findViewById(R.id.configure_wifi_ap);
        configureWifiActionBtn.setOnClickListener(configureNewWifiAp);
    }

    View.OnClickListener airplaneModeClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

            try
            {

            }
            catch (Exception e)
            {
                Timber.e(e,"Exception during ActionsView airplaneModeClickListener action");
            }

            view.setVisibility(GONE);
            refreshUI();
        }
    };

    View.OnClickListener enableWifiClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

            try
            {
                APL.enableWifi();
            }
            catch (Exception e)
            {
                Timber.e(e,"Exception during ActionsView enableWifiClickListener action");
            }

            view.setVisibility(GONE);
            refreshUI();
        }
    };

    View.OnClickListener configureNewWifiAp = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            ProxyUtils.startAndroidWifiSettings(getContext());

            view.setVisibility(GONE);
            refreshUI();
        }
    };

    //    public void airplaneModeAction(boolean b)
//    {
//        airplaneModeActionBtn.setVisibility(UIUtils.booleanToVisibility(b));
//    }

    public void wifiOnOffEnable(boolean b)
    {
        enableWifiActionBtn.setVisibility(UIUtils.booleanToVisibility(b));

        refreshUI();
    }

    public void wifiConfigureEnable(boolean b)
    {
        configureWifiActionBtn.setVisibility(UIUtils.booleanToVisibility(b));

        refreshUI();
    }

    private void refreshUI()
    {
        if (
                enableWifiActionBtn.getVisibility() == GONE &&
                configureWifiActionBtn.getVisibility() == GONE)
        {
            this.setVisibility(GONE);
        }
        else
        {
            this.setVisibility(VISIBLE);
        }
    }
}
