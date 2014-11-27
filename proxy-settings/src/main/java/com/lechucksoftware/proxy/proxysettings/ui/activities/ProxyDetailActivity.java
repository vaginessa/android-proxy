package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncUpdateLinkedWiFiAP;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyDetailFragment;

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

        FragmentManager fm = getFragmentManager();

        ActionBar actionBar = getActionBar();
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
            App.getEventsReporter().sendException(new Exception("No caller intent received"));
        }
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments)
    {
        if (requestCode == Requests.UPDATE_LINKED_WIFI_AP)
        {
            ProxyEntity updated = App.getDBManager().getProxy(proxyId);
            ProxyEntity current = App.getDBManager().getProxy(updated.getId());

            AsyncUpdateLinkedWiFiAP asyncUpdateLinkedWiFiAP = new AsyncUpdateLinkedWiFiAP(this, current, updated);
            asyncUpdateLinkedWiFiAP.execute();

            App.getDBManager().upsertProxy(updated);
            finish();
        }
    }
}
