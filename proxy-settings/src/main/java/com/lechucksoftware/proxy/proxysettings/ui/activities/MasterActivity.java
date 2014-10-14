package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.MainStatusFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.NavDrawFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyListFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.WiFiApListFragment;

public class MasterActivity extends Activity implements NavDrawFragment.NavigationDrawerCallbacks
{
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
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MainStatusFragment.newInstance(position + 1))
                        .addToBackStack(MainStatusFragment.class.getSimpleName())
                        .commit();
                break;

            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, WiFiApListFragment.getInstance())
                        .addToBackStack(WiFiApListFragment.class.getSimpleName())
                        .commit();
                break;

            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ProxyListFragment.newInstance())
                        .addToBackStack(ProxyListFragment.class.getSimpleName())
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number)
    {
        switch (number)
        {
            case 1:
                mTitle = getString(R.string.app_name);
                break;
            case 2:
                mTitle = getString(R.string.wifi_access_points);
                break;
            case 3:
                mTitle = getString(R.string.proxies_list);
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
}
