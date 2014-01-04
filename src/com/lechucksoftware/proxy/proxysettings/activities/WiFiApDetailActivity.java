package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.WiFiApDetailFragment;

import java.util.UUID;


/**
 * Created by marco on 17/05/13.
 */
public class WiFiApDetailActivity extends BaseWifiActivity
{
    public static String TAG = WiFiApDetailActivity.class.getSimpleName();

    private static WiFiApDetailActivity instance;
    public static WiFiApDetailActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden

        instance = this;
        setContentView(R.layout.main_layout);

        FragmentManager fm = getFragmentManager();

        // Add the StatusFragment to the status_fragment_container
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.status_fragment_container, StatusFragment.getInstance()).commit();

        Intent callerIntent = getIntent();
        if (callerIntent != null)
        {
            UUID selectedId = (UUID) callerIntent.getExtras().getSerializable(Constants.SELECTED_AP_CONF_ARG);

            // Add the WiFiApListFragment to the main fragment_container
            WiFiApDetailFragment details = WiFiApDetailFragment.newInstance(selectedId);
            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment_container, details).commit();
        }
    }
}
