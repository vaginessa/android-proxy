package com.lechucksoftware.proxy.proxysettings.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.List;

import timber.log.Timber;

/**
 * Created by marco on 07/11/13.
 */
public class BaseActivity extends ActionBarActivity
{
    private static final String TAG = BaseActivity.class.getName();
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

        IntentFilter ifilt = new IntentFilter();
        ifilt.addAction(Intents.SERVICE_COMUNICATION);

        try
        {
            registerReceiver(broadcastReceiver, ifilt);
        }
        catch (IllegalArgumentException e)
        {
            Timber.e(e,"Exception resuming BaseActivity");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onPause");

        try
        {
            // Stop the registered status receivers
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Timber.e(e,"Exception pausing BaseWifiActivity");
        }
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            App.getTraceUtils().logIntent(TAG, intent, Log.DEBUG, true);

            if (action.equals(Intents.SERVICE_COMUNICATION))
            {
                final String title;
                final String message;
                final Boolean closeActivty;

                if (intent.hasExtra(Constants.SERVICE_COMUNICATION_TITLE))
                {
                    title = intent.getStringExtra(Constants.SERVICE_COMUNICATION_TITLE);
                }
                else
                {
                    title = "";
                }

                if (intent.hasExtra(Constants.SERVICE_COMUNICATION_MESSAGE))
                {
                    message = intent.getStringExtra(Constants.SERVICE_COMUNICATION_MESSAGE);
                }
                else
                {
                    message = "";
                }

                if (intent.hasExtra(Constants.SERVICE_COMUNICATION_CLOSE_ACTIVITY))
                {
                    closeActivty = intent.getBooleanExtra(Constants.SERVICE_COMUNICATION_CLOSE_ACTIVITY, false);
                }
                else
                {
                    closeActivty = false;
                }

                UIUtils.showDialog(BaseActivity.this,message,title, new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        if (closeActivty)
                            finish();
                    }

                });
            }
            else
            {
                Timber.e("Received intent not handled: " + intent.getAction());
            }
        }
    };
}
