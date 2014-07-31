package com.lechucksoftware.proxy.proxysettings.ui.components;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import be.shouldit.proxy.lib.APL;

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
//            refreshUI();
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
                EventReportingUtils.sendException(new Exception("Exception during ActionsView airplaneModeClickListener action", e));
            }

            view.setVisibility(GONE);
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
                EventReportingUtils.sendException(new Exception("Exception during ActionsView enableWifiClickListener action", e));
            }

            view.setVisibility(GONE);
        }
    };

    View.OnClickListener configureNewWifiAp = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            getContext().startActivity(intent);

            view.setVisibility(GONE);
        }
    };

//    public void airplaneModeAction(boolean b)
//    {
//        airplaneModeActionBtn.setVisibility(UIUtils.booleanToVisibility(b));
//    }

    public void enableWifiAction(boolean b)
    {
        enableWifiActionBtn.setVisibility(UIUtils.booleanToVisibility(b));
    }

    public void configureWifiAction(boolean b)
    {
        configureWifiActionBtn.setVisibility(UIUtils.booleanToVisibility(b));
    }
}
