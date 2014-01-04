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
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyListFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.WiFiApListFragment;
import com.lechucksoftware.proxy.proxysettings.test.TestActivity;
import com.lechucksoftware.proxy.proxysettings.utils.AsyncStartupBetaTestTask;
import com.lechucksoftware.proxy.proxysettings.utils.AsyncStartupDialogTask;
import com.lechucksoftware.proxy.proxysettings.utils.AsyncStartupRateTask;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;


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

        // Add the StatusFragment to the status_fragment_container
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.status_fragment_container, StatusFragment.getInstance()).commit();

        // Add the WiFiApListFragment to the main fragment_container
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_container, ProxyListFragment.newInstance()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.proxy_prefs_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // Do nothing
                break;

            case R.id.menu_about:
                NavigationUtils.GoToHelpFragment(getFragmentManager());
                break;

            case R.id.menu_proxies:
                Intent proxyIntent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(proxyIntent);
                break;
//            case R.id.menu_feedbacks:
//                NavigationUtils.GoToAppFeedbacks(getFragmentManager());
//                return true;

            case R.id.menu_developer:
                Intent testIntent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(testIntent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
