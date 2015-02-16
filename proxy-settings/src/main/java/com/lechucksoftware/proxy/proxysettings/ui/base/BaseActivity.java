package com.lechucksoftware.proxy.proxysettings.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;

import java.util.List;

import timber.log.Timber;

/**
 * Created by marco on 07/11/13.
 */
public class BaseActivity extends ActionBarActivity
{
    private static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onNewIntent");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onDestroy");
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

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onPause");
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onStart");
        active = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onStop");
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
            List<Fragment> fragments = getSupportFragmentManager().getFragments(); //findFragmentById(R.id.fragment_container);
            for (Fragment f : fragments)
            {
                if (f instanceof IBaseFragment)
                {
                    IBaseFragment ibf = (IBaseFragment) f;
                    ibf.refreshUI();
                }
            }
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception during IBaseFragment refresh from %s",this.getClass().getSimpleName());
        }
    }
}
