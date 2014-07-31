package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyListFragment;


/**
 * Created by marco on 17/05/13.
 */
public class ProxyListActivity extends BaseWifiActivity
{
    public static String TAG = ProxyListActivity.class.getSimpleName();
    private static ProxyListActivity instance;
    public static ProxyListActivity getInstance()
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

        FragmentManager fm = getFragmentManager();

        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_container, ProxyListFragment.newInstance()).commit();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.proxy_list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.proxy_list, menu);
        return true;
    }
}
