package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyDetailFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.WiFiApDetailFragment;

import java.util.UUID;


/**
 * Created by marco on 17/05/13.
 */
public class ProxyDetailActivity extends BaseWifiActivity
{
    public static String TAG = ProxyDetailActivity.class.getSimpleName();

    private static ProxyDetailActivity instance;
    public static ProxyDetailActivity getInstance()
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
            ProxyEntity selectedProxy = (ProxyEntity) callerIntent.getExtras().getSerializable(Constants.SELECTED_PROXY_CONF_ARG);

            // Add the WiFiApListFragment to the main fragment_container
            ProxyDetailFragment detail = ProxyDetailFragment.newInstance(selectedProxy);
            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment_container, detail).commit();
        }
    }
}
