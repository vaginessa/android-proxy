package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.WiFiApDetailFragment;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;

import be.shouldit.proxy.lib.APLNetworkId;

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

        Intent callerIntent = getIntent();
        if (callerIntent != null && callerIntent.hasExtra(Constants.SELECTED_AP_CONF_ARG))
        {
            APLNetworkId selectedId = (APLNetworkId) callerIntent.getExtras().getSerializable(Constants.SELECTED_AP_CONF_ARG);

            WiFiApDetailFragment detail = WiFiApDetailFragment.newInstance(selectedId);
            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment_container, detail).commit();

            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(selectedId.SSID);
            actionBar.setDisplayUseLogoEnabled(false);
        }
        else
        {
            App.getEventsReporter().sendException(new Exception("Intent not received or not containing extra"));
            NavigationUtils.GoToMainActivity(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ap_wifi_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent mainIntent = new Intent(this, WiFiApListActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
