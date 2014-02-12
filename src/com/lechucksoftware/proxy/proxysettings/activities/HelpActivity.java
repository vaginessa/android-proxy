package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;

public class HelpActivity extends BaseWifiActivity
{
    public static String TAG = HelpActivity.class.getSimpleName();

    private static HelpActivity instance;

    public static HelpActivity getInstance()
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

        HelpPrefsFragment helpPrefsFragment = HelpPrefsFragment.getInstance();

        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_container, helpPrefsFragment).commit();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.help));
    }
}
