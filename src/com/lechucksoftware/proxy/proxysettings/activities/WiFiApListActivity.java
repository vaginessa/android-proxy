package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.fragments.WiFiApListFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.test.TestActivity;
import com.lechucksoftware.proxy.proxysettings.utils.AsyncStartupBetaTestTask;
import com.lechucksoftware.proxy.proxysettings.utils.AsyncStartupDialogTask;
import com.lechucksoftware.proxy.proxysettings.utils.AsyncStartupRateTask;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;


/**
 * Created by marco on 17/05/13.
 */
public class WiFiApListActivity extends BaseWifiActivity
{
    public static String TAG = WiFiApListActivity.class.getSimpleName();

    AsyncStartupDialogTask asyncStartupDialogTask;
    AsyncStartupRateTask asyncStartupRateTask;
    AsyncStartupBetaTestTask asyncStartupBetaTestTask;

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
        setContentView(R.layout.main_layout);

        FragmentManager fm = getFragmentManager();

        // Add the StatusFragment to the status_fragment_container
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.status_fragment_container, StatusFragment.getInstance()).commit();

        // Add the WiFiApListFragment to the main fragment_container
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_container, WiFiApListFragment.getInstance()).commit();


        asyncStartupDialogTask = new AsyncStartupDialogTask(this);
        asyncStartupRateTask = new AsyncStartupRateTask(this);
        asyncStartupBetaTestTask = new AsyncStartupBetaTestTask(this);
        asyncStartupDialogTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        asyncStartupRateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        asyncStartupBetaTestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ap_wifi_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_proxies:
                Intent proxyIntent = new Intent(getApplicationContext(), ProxyListActivity.class);
                startActivity(proxyIntent);
                break;

//            case R.id.menu_feedbacks:
//                NavigationUtils.GoToAppFeedbacks(getFragmentManager());
//                return true;

//            case android.R.id.home:
//                // Do nothing
//                break;
//
//            case R.id.menu_about:
//                Intent helpIntent = new Intent(getApplicationContext(), HelpActivity.class);
//                startActivity(helpIntent);
//                break;
//
//            case R.id.menu_developer:
//                Intent testIntent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(testIntent);
//                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
