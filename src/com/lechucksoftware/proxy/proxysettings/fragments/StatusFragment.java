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
                statusButton.setText(selConf.getAPConnectionStatus());

                if (selConf.isCurrentNetwork())
                {
                    statusButton.setBackgroundResource(R.color.Holo_Blue_Light);
                }
                else if (selConf.ap.mRssi < Integer.MAX_VALUE)
                {
                    statusButton.setBackgroundResource(R.color.Holo_Green_Light);
                }
                else
                {
                    statusButton.setBackgroundResource(R.color.Gray);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    public void Hide() {
//        statusButton.setHeight(0);
        statusButton.setVisibility(View.GONE);
        refreshUI();
    }

    public void Show() {
        statusButton.setVisibility(View.VISIBLE);
        refreshUI();
    }
}
