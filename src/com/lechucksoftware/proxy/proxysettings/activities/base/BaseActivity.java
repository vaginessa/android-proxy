package com.lechucksoftware.proxy.proxysettings.activities.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.analytics.tracking.android.EasyTracker;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.shouldit.proxy.lib.BuildConfig;
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
        LogWrapper.d(this.getClass().getSimpleName(),"onDestroy");
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
        LogWrapper.d(this.getClass().getSimpleName(),"onPause");
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
        LogWrapper.d(this.getClass().getSimpleName(),"onStop");
        active = false;

        EasyTracker.getInstance(this).activityStop(this);
    }

    public void refreshUI()
    {
        IBaseFragment f = (IBaseFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        f.refreshUI();
    }
}
