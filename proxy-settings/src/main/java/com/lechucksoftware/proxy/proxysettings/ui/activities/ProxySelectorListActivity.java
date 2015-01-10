package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.ProxySelectorFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyListFragment;

import java.net.ProxySelector;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiAPConfig;


/**
 * Created by marco on 17/05/13.
 */
public class ProxySelectorListActivity extends BaseWifiActivity
{
    public static String TAG = ProxySelectorListActivity.class.getSimpleName();
    private static ProxySelectorListActivity instance;
    public static ProxySelectorListActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden
        super.onCreate(null);
        instance = this;

        setContentView(R.layout.main_layout);

        APLNetworkId wifiAplNetworkId = (APLNetworkId) getIntent().getSerializableExtra(Constants.WIFI_AP_NETWORK_ARG);

        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_container, ProxySelectorFragment.newInstance(wifiAplNetworkId)).commit();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.select_proxy));
        actionBar.setDisplayUseLogoEnabled(false);
    }
}
