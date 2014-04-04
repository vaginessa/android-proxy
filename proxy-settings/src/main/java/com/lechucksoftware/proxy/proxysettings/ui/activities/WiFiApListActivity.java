package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncStartupActions;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncStartupBetaTestTask;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncStartupChangelogDialogTask;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncStartupRateTask;
import com.lechucksoftware.proxy.proxysettings.ui.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.WiFiApListFragment;


/**
 * Created by marco on 17/05/13.
 */
public class WiFiApListActivity extends BaseWifiActivity
{
    public static String TAG = WiFiApListActivity.class.getSimpleName();

    AsyncStartupChangelogDialogTask asyncStartupChangelogDialogTask;
    AsyncStartupRateTask asyncStartupRateTask;
    AsyncStartupBetaTestTask asyncStartupBetaTestTask;
    AsyncStartupActions asyncStartupActionsTask;

    private static WiFiApListActivity instance;


    public static WiFiApListActivity getInstance()
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

        // Add the WiFiApListFragment to the main fragment_container
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_container, WiFiApListFragment.getInstance()).commit();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.app_name));

        asyncStartupChangelogDialogTask = new AsyncStartupChangelogDialogTask(this);
        asyncStartupChangelogDialogTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//        asyncStartupRateTask = new AsyncStartupRateTask(this);
//        asyncStartupRateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//        asyncStartupBetaTestTask = new AsyncStartupBetaTestTask(this);
//        asyncStartupBetaTestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        asyncStartupActionsTask = new AsyncStartupActions(this);
        asyncStartupActionsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ap_wifi_list, menu);
        return true;
    }
}
