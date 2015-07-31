package com.lechucksoftware.proxy.proxysettings.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.WiFiApDetailFragment;

import be.shouldit.proxy.lib.APLNetworkId;
import timber.log.Timber;

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

        FragmentManager fm = getSupportFragmentManager();

        Intent callerIntent = getIntent();
        if (callerIntent != null && callerIntent.hasExtra(Constants.SELECTED_AP_CONF_ARG))
        {
            APLNetworkId selectedId = callerIntent.getExtras().getParcelable(Constants.SELECTED_AP_CONF_ARG);

            WiFiApDetailFragment detail = WiFiApDetailFragment.newInstance(selectedId);
            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment_container, detail).commit();

            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setTitle(getString(R.string.edit_wifi_ap));
                actionBar.setDisplayUseLogoEnabled(false);
            }
        }
        else
        {
            Timber.e(new Exception(),"Intent not received or not containing extra");
        }
    }
}
