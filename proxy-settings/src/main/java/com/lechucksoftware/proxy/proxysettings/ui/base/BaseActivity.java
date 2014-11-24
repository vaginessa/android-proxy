package com.lechucksoftware.proxy.proxysettings.ui.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;

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

    public void refreshUI()
    {
        try
        {
            Fragment containedFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
            if (containedFragment instanceof IBaseFragment)
            {
                IBaseFragment f = (IBaseFragment) containedFragment;
                f.refreshUI();
            }
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(new Exception(String.format("Exception during IBaseFragment refresh from %s",this.getClass().getSimpleName()),e));
        }
    }
}
