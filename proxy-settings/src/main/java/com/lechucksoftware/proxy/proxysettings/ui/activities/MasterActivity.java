package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncStartupActions;
import com.lechucksoftware.proxy.proxysettings.test.DeveloperOptionsActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.PacListFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyListFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.WiFiApListFragment;
import com.lechucksoftware.proxy.proxysettings.utils.FragmentsUtils;

public class MasterActivity extends BaseWifiActivity
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
//    private NavDrawFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        mTitle = getTitle();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_pac_proxies).setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        menu.findItem(R.id.nav_developer).setVisible(BuildConfig.DEBUG);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {

                FragmentManager fragmentManager = getSupportFragmentManager();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId())
                {
                    case R.id.nav_wifi_networks:
                    default:
                        mTitle = getString(R.string.wifi_networks);
                        FragmentsUtils.changeFragment(fragmentManager,
                                R.id.fragment_container,
                                WiFiApListFragment.newInstance(),
                                false);
                        break;

                    case R.id.nav_static_proxies:
                        mTitle = getString(R.string.static_proxies);
                        FragmentsUtils.changeFragment(fragmentManager,
                                R.id.fragment_container,
                                ProxyListFragment.newInstance(),
                                false);
                        break;

                    case R.id.nav_pac_proxies:
                        mTitle = getString(R.string.pac_proxies);
                        FragmentsUtils.changeFragment(fragmentManager,
                                R.id.fragment_container,
                                PacListFragment.newInstance(),
                                false);
                        break;

                    case R.id.nav_help:
                        mTitle = getString(R.string.help);
                        FragmentsUtils.changeFragment(fragmentManager,
                                R.id.fragment_container,
                                HelpPrefsFragment.newInstance(),
                                false);
                        break;

                    case R.id.nav_developer:
                        Intent testIntent = new Intent(getApplicationContext(), DeveloperOptionsActivity.class);
                        startActivity(testIntent);
                        break;
                }

                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setTitle(mTitle);

                return true;
            }
        });

        FragmentsUtils.changeFragment(getSupportFragmentManager(),
                R.id.fragment_container,
                WiFiApListFragment.newInstance(),
                false);

        AsyncStartupActions asyncStartupActionsTask = new AsyncStartupActions(this);
        asyncStartupActionsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(App.getAppStats().launchCount == 0)
        {
            Intent introIntent = new Intent(this, IntroActivity.class);
            startActivity(introIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                if (isDrawerOpen())
                {
                    drawerLayout.closeDrawers();
                }
                else
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                }

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isDrawerOpen()
    {
        return drawerLayout.isDrawerOpen(navigationView);
    }
}
