package com.lechucksoftware.proxy.proxysettings.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.lechucksoftware.proxy.proxysettings.test.TestActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.HelpActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;

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

        App.getLogger().d(this.getClass().getSimpleName(), "onCreate");

        App.getEventsReporter().sendScreenView(this.getClass().getSimpleName());
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        App.getLogger().d(this.getClass().getSimpleName(), "onNewIntent");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        App.getLogger().d(this.getClass().getSimpleName(), "onDestroy");
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

        App.getLogger().d(this.getClass().getSimpleName(), "onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        App.getLogger().d(this.getClass().getSimpleName(), "onPause");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        App.getLogger().d(this.getClass().getSimpleName(), "onStart");
        active = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        App.getLogger().d(this.getClass().getSimpleName(), "onStop");
        active = false;
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case R.id.menu_add_new_proxy:
//                Intent i = new Intent(getApplicationContext(), ProxyDetailActivity.class);
//                startActivity(i);
//                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action, R.string.analytics_act_button_click, R.string.analytics_lab_create_new_proxy);
//                break;
//
//            case R.id.menu_help:
//                Intent helpIntent = new Intent(getApplicationContext(), HelpActivity.class);
//                startActivity(helpIntent);
//                break;
//
//            case R.id.menu_developer:
//                Intent testIntent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(testIntent);
//                break;
//
////            case R.id.menu_feedbacks:
////                NavigationUtils.GoToAppFeedbacks(getFragmentManager());
////                return true;
////
////            case R.id.menu_about:
////                Intent helpIntent = new Intent(getApplicationContext(), HelpActivity.class);
////                startActivity(helpIntent);
////                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void refreshUI()
    {
        try
        {
            IBaseFragment f = (IBaseFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
            f.refreshUI();
        }
        catch (Exception e)
        {
            App.getLogger().e(this.getClass().getSimpleName(), "cannot call refresh fragment");
        }
    }
}
