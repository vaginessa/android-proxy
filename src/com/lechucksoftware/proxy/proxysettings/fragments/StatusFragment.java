package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.shouldit.proxy.lib.ProxyConfiguration;

/**
 * Created by marco on 21/05/13.
 */
public class StatusFragment extends EnhancedFragment
{
    private static final String TAG = "StatusFragment";
    public static StatusFragment instance;
    private Button statusButton;

    /**
     * Create a new instance of StatusFragment
     */
    public static StatusFragment getInstance()
    {
        if (instance == null)
            instance = new StatusFragment();

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.status, container, false);
        statusButton = (Button) view.findViewById(R.id.status_button);
        statusButton.setBackgroundResource(R.color.Holo_Green_Light);
        return view;
    }

    public void refreshUI()
    {
//        if (isVisible())
        {
            if (statusButton != null)
            {
                ProxyConfiguration selConf = ApplicationGlobals.getSelectedConfiguration();

                if (selConf != null)
                {
                    // Write something about selected configuration
                    show();

                    if (selConf.isCurrentNetwork())
                    {
                        setStatus(selConf.getAPConnectionStatus(), null, R.color.Holo_Blue_Light);
                    }
                    else if (selConf.ap.mRssi < Integer.MAX_VALUE)
                    {
                        setStatus(selConf.getAPConnectionStatus(), null, R.color.Holo_Green_Light);
                    }
                    else
                    {
                        setStatus(selConf.getAPConnectionStatus(), null, R.color.Gray);
                    }
                }
                else
                {
                    // No configuration selected
                    if (!ApplicationGlobals.getWifiManager().isWifiEnabled())
                    {
                        // Wi-Fi disabled -> ask to enable!
                        setStatus(getResources().getString(R.string.enable_wifi_action), enableWifi, R.color.Holo_Red_Light);
                    }
                    else
                    {
                        // Wi-Fi enabled
                        if (ApplicationGlobals.isConnectedToWiFi())
                        {
                            // Connected to Wi-Fi ap
                        }
                        else
                        {
                            if (ApplicationGlobals.getInstance().getNotConfiguredWifi().values().size() > 0)
                            {
                                // Wi-Fi AP available to connection -> Go to Wi-Fi Settings
                                setStatus(getResources().getString(R.string.setupap_wifi_action), configureNewWifiAp, R.color.Holo_Green_Light);
                            }
                            else
                            {
                                // Wi-Fi AP available to connection
//                                setStatus(getResources().getString(R.string.enable_wifi_action), configureNewWifiAp, R.color.Holo_Green_Light);
                            }
                        }
                    }
                }
            }
        }
    }


    public void setStatus(String status, View.OnClickListener listener, int resId)
    {
        if (listener != null)
            statusButton.setText(String.format("%s...", status));
        else
            statusButton.setText(status);

        statusButton.setBackgroundResource(resId);
        statusButton.setOnClickListener(listener);
        show();
    }

    View.OnClickListener enableWifi = new View.OnClickListener()
{
    @Override
    public void onClick(View view)
    {
        ApplicationGlobals.getWifiManager().setWifiEnabled(true);
        hide();
    }
};

    View.OnClickListener configureNewWifiAp = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            hide();
            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            startActivity(intent);
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        refreshUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void hide()
    {
        statusButton.setVisibility(View.GONE);
    }

    private void show()
    {
        statusButton.setVisibility(View.VISIBLE);
    }
}
