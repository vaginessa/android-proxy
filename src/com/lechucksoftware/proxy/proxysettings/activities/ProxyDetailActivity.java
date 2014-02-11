package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.base.BaseWifiActivity;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyDetailFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.test.TestActivity;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;

import java.util.UUID;

public class ProxyDetailActivity extends BaseWifiActivity
{
    public static String TAG = ProxyDetailActivity.class.getSimpleName();

    private static ProxyDetailActivity instance;
    private UUID cachedProxyId;

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

        createCancelSaveActionBar();

        // Add the StatusFragment to the status_fragment_container
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.status_fragment_container, StatusFragment.getInstance()).commit();

        Intent callerIntent = getIntent();
        if (callerIntent != null)
        {
            Bundle extras = callerIntent.getExtras();
            ProxyDetailFragment detail = null;
            if (extras != null && extras.containsKey(Constants.SELECTED_PROXY_CONF_ARG))
            {
                cachedProxyId = (UUID) extras.getSerializable(Constants.SELECTED_PROXY_CONF_ARG);

                detail = ProxyDetailFragment.newInstance(cachedProxyId);

                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.fragment_container, detail).commit();
            }
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.proxy_details_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu)
//    {
//        super.onPrepareOptionsMenu(menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case android.R.id.home:
//                // Do nothing
//                break;
//
//            case R.id.menu_about:
//                NavigationUtils.GoToHelpFragment(getFragmentManager());
//                break;
//
//            case R.id.menu_proxies:
//                Intent proxyIntent = new Intent(getApplicationContext(), ProxyListActivity.class);
//                startActivity(proxyIntent);
//                break;
//
////            case R.id.menu_feedbacks:
////                NavigationUtils.GoToAppFeedbacks(getFragmentManager());
////                return true;
//
//            case R.id.menu_developer:
//                Intent testIntent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(testIntent);
//                break;
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//    {
//        super.onCreateOptionsMenu(menu);
//        inflater.inflate(R.menu.proxy_details_menu, menu);
//    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu)
//    {
//        super.onPrepareOptionsMenu(menu);
//    }

    private void createCancelSaveActionBar()
    {
        final ActionBar actionBar = getActionBar();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View customActionBarView = inflater.inflate(R.layout.save_cancel, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // "Done"
                        saveConfiguration();
                        ApplicationGlobals.getCacheManager().release(cachedProxyId);
                        finish();
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // "Done"
                        ApplicationGlobals.getCacheManager().release(cachedProxyId);
                        finish();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                                    ActionBar.DISPLAY_SHOW_CUSTOM |
                                    ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));


//        actionBar.setCustomView(customActionBarView);
    }



    private void saveConfiguration()
    {
        try
        {
//            ProxyEntity newProxy = ApplicationGlobals.getDBManager().getProxy(selectedProxyID);
//            newProxy.host = proxyHost.getValue();
//            newProxy.port = Integer.parseInt(proxyPort.getValue());
//            newProxy.exclusion = proxyBypass.getExclusionList();
//
//            ApplicationGlobals.getDBManager().updateProxy(selectedProxyID, newProxy);
////            ApplicationGlobals.getProxyManager().updateWifiConfiguration(selectProxy, newProxy);
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
        }
    }
}
