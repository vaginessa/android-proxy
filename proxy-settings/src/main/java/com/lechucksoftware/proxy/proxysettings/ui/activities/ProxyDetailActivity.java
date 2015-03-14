package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyDetailFragment;

import timber.log.Timber;

public class ProxyDetailActivity extends BaseActivity
{
    public static String TAG = ProxyDetailActivity.class.getSimpleName();

    private static ProxyDetailActivity instance;
    private Long proxyId;

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

        FragmentManager fm = getSupportFragmentManager();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        Intent callerIntent = getIntent();
        if (callerIntent != null)
        {
            Bundle extras = callerIntent.getExtras();
            ProxyDetailFragment detail = null;

            if (extras != null && extras.containsKey(Constants.SELECTED_PROXY_CONF_ARG))
            {
                proxyId = (Long) extras.getSerializable(Constants.SELECTED_PROXY_CONF_ARG);
                detail = ProxyDetailFragment.newInstance(proxyId);
                actionBar.setTitle(getString(R.string.edit_proxy));
            }
            else
            {
                detail = ProxyDetailFragment.newInstance();
                actionBar.setTitle(getString(R.string.create_new_proxy));
            }

            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment_container, detail).commit();
        }
        else
        {
            Timber.e(new Exception(),"No caller intent received");
        }
    }
}
