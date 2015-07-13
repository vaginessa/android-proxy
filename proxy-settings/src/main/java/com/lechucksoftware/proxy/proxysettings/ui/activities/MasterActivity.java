package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.NavigationAction;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncStartupActions;
import com.lechucksoftware.proxy.proxysettings.test.DeveloperOptionsActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.NavDrawFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.PacListFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyListFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.WiFiApListFragment;
import com.lechucksoftware.proxy.proxysettings.utils.FragmentsUtils;

public class MasterActivity extends BaseWifiActivity implements NavDrawFragment.NavigationDrawerCallbacks
{
    private static final String TAG = MasterActivity.class.getSimpleName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
//    private NavDrawFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private AsyncStartupActions asyncStartupActionsTask;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

//        mNavigationDrawerFragment = (NavDrawFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        asyncStartupActionsTask = new AsyncStartupActions(this);
        asyncStartupActionsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        NavigationAction navigationAction = App.getNavigationManager().getAction(position);

        switch (navigationAction)
        {
            case WIFI_NETWORKS:
            case NOT_DEFINED:
            default:
                FragmentsUtils.changeFragment(fragmentManager,
                        R.id.fragment_container,
                        WiFiApListFragment.newInstance(position),
                        false);
                break;

            case HTTP_PROXIES_LIST:
                FragmentsUtils.changeFragment(fragmentManager,
                        R.id.fragment_container,
                        ProxyListFragment.newInstance(position),
                        false);
                break;

            case PAC_PROXIES_LIST:
                FragmentsUtils.changeFragment(fragmentManager,
                        R.id.fragment_container,
                        PacListFragment.newInstance(position),
                        false);
                break;

            case HELP:
                FragmentsUtils.changeFragment(fragmentManager,
                        R.id.fragment_container,
                        HelpPrefsFragment.newInstance(position),
                        false);
                break;

            case DEVELOPER:
                Intent testIntent = new Intent(getApplicationContext(), DeveloperOptionsActivity.class);
                startActivity(testIntent);
                break;
        }
    }

    public void onSectionAttached(int number)
    {
        NavigationAction navigationAction = App.getNavigationManager().getAction(number);

        switch (navigationAction)
        {
            case WIFI_NETWORKS:
            case NOT_DEFINED:
            default:
                mTitle = getString(R.string.wifi_networks);
                break;

            case HTTP_PROXIES_LIST:
                mTitle = getString(R.string.static_proxies);
                break;

            case PAC_PROXIES_LIST:
                mTitle = getString(R.string.pac_proxies);
                break;

            case HELP:
                mTitle = getString(R.string.help);
                break;
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public boolean isDrawerOpen()
    {
        return navigationView.isActivated();
    }
}
