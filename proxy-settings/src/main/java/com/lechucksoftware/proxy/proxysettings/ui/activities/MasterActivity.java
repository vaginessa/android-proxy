package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.test.DeveloperOptionsActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.MainStatusFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.NavDrawFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyListFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.WiFiApListFragment;
import com.lechucksoftware.proxy.proxysettings.utils.FragmentsUtils;

public class MasterActivity extends BaseWifiActivity implements NavDrawFragment.NavigationDrawerCallbacks
{
    private static final String TAG = MasterActivity.class.getSimpleName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavDrawFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        mNavigationDrawerFragment = (NavDrawFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        switch (position)
        {
//            case 0:
//                FragmentsUtils.changeFragment(getFragmentManager(), R.id.container, MainStatusFragment.newInstance(position), false);
//                break;

            case 0:
                FragmentsUtils.changeFragment(getFragmentManager(), R.id.fragment_container, WiFiApListFragment.newInstance(position), false);
                break;

            case 1:
                FragmentsUtils.changeFragment(getFragmentManager(), R.id.fragment_container, ProxyListFragment.newInstance(position), true);
                break;

            case 2:
                FragmentsUtils.changeFragment(getFragmentManager(), R.id.fragment_container, HelpPrefsFragment.newInstance(position), true);
                break;

            case 3:
                Intent testIntent = new Intent(getApplicationContext(), DeveloperOptionsActivity.class);
                startActivity(testIntent);
                break;
        }
    }

    public void onSectionAttached(int number)
    {
        switch (number)
        {
//            case 0:
//                mTitle = getString(R.string.app_name);
//                break;
            case 0:
                mTitle = getString(R.string.wifi_access_points);
                break;
            case 1:
                mTitle = getString(R.string.proxies_list);
                break;
            case 2:
                mTitle = getString(R.string.help);
                break;
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        if (!isDrawerOpen())
//        {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.master, menu);
//            restoreActionBar();
//            return true;
//        }
//        return super.onCreateOptionsMenu(menu);
//    }

    public boolean isDrawerOpen() {return mNavigationDrawerFragment.isDrawerOpen();}

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings)
//        {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_help:
                Intent helpIntent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(helpIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
