package com.lechucksoftware.proxy.proxysettings.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.BaseActions;
import com.lechucksoftware.proxy.proxysettings.constants.EventCategories;
import com.lechucksoftware.proxy.proxysettings.ui.activities.HelpActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyListActivity;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.lechucksoftware.proxy.proxysettings.test.TestActivity;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.shouldit.proxy.lib.log.LogWrapper;

/**
 * Created by marco on 07/11/13.
 */
public class BaseActivity extends Activity
{
    private static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogWrapper.d(this.getClass().getSimpleName(), "onCreate");

        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        LogWrapper.d(this.getClass().getSimpleName(), "onNewIntent");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        LogWrapper.d(this.getClass().getSimpleName(), "onDestroy");
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (BuildConfig.DEBUG)
        {
            // ONLY on DEBUG
            ViewServer.get(this).setFocusedWindow(this);
        }

        LogWrapper.d(this.getClass().getSimpleName(), "onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LogWrapper.d(this.getClass().getSimpleName(), "onPause");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        LogWrapper.d(this.getClass().getSimpleName(), "onStart");
        active = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        LogWrapper.d(this.getClass().getSimpleName(), "onStop");
        active = false;

        EasyTracker.getInstance(this).activityStop(this);
    }


    public void onDialogResult(int requestCode, int resultCode, Bundle arguments)
    {
        // Intentionally left blank
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (!BuildConfig.DEBUG)
        {
            menu.removeItem(R.id.menu_developer);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    //TODO: remove handling of menu from the BaseActivity class
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavigationUtils.GoToMainActivity(getApplicationContext());
                break;

            case R.id.menu_proxies:
                Intent proxyIntent = new Intent(getApplicationContext(), ProxyListActivity.class);
                startActivity(proxyIntent);
                break;

            case R.id.menu_add_new_proxy:
                Intent i = new Intent(getApplicationContext(), ProxyDetailActivity.class);
                ProxyEntity emptyProxy = new ProxyEntity();
                ApplicationGlobals.getCacheManager().put(emptyProxy.getUUID(), emptyProxy);
                i.putExtra(Constants.SELECTED_PROXY_CONF_ARG, emptyProxy.getUUID());
                startActivity(i);
                EventReportingUtils.sendEvent(EventCategories.UI, BaseActions.BUTTON_PRESS, "create_new_proxy", null);
                break;

            case R.id.menu_about:
                Intent helpIntent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(helpIntent);
                break;

            case R.id.menu_developer:
                Intent testIntent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(testIntent);
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

    public void refreshUI()
    {
        try
        {
            IBaseFragment f = (IBaseFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
            f.refreshUI();
        }
        catch (Exception e)
        {
            LogWrapper.e(this.getClass().getSimpleName(), "cannot call refresh fragment");
        }
    }
}
