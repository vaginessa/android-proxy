package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
        setContentView(R.layout.main_layout_with_status);

        FragmentManager fm = getFragmentManager();

        // Add the StatusFragment to the status_fragment_container
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.status_fragment_container, StatusFragment.getInstance()).commit();

        Intent callerIntent = getIntent();
        if (callerIntent != null)
        {
            UUID selectedId = (UUID) callerIntent.getExtras().getSerializable(Constants.SELECTED_AP_CONF_ARG);

            WiFiApDetailFragment detail = WiFiApDetailFragment.newInstance(selectedId);
            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment_container, detail).commit();
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ap_wifi_list, menu);
        return true;
    }
}
