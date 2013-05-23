package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.proxy.lib.ProxyConfiguration;

/**
 * Created by marco on 21/05/13.
 */
public class StatusFragment extends Fragment {
    public static StatusFragment instance;
    private Button statusButton;

    /**
     * Create a new instance of StatusFragment
     */
    public static StatusFragment getInstance() {
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
                    setStatus("Enable Wi-Fi", enableWifi, R.color.Holo_Red_Light);
                }
                else
                    hide();
            }
        }
    }


    public void setStatus(String status, View.OnClickListener listener, int resId)
    {
        if (listener != null)
            statusButton.setText(String.format("%s...",status));
        else
            statusButton.setText(status);

        statusButton.setBackgroundResource(resId);
        statusButton.setOnClickListener(listener);
    }

    View.OnClickListener enableWifi = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            ApplicationGlobals.getWifiManager().setWifiEnabled(true);
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        refreshUI();
    }

    public void hide()
    {
        statusButton.setVisibility(View.GONE);
    }

    public void show()
    {
        statusButton.setVisibility(View.VISIBLE);
    }
}
