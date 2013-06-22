package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.AccessPointListFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyDetailsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.lechucksoftware.proxy.proxysettings.utils.WhatsNewDialog;
import com.shouldit.android.utils.lib.log.LogWrapper;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.APLConstants;


/**
 * Created by marco on 17/05/13.
 */
public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity";
    private static boolean active = false;

    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
    private Scanner mScanner;
    private Scanner getScanner()
    {
        if (mScanner == null)
            mScanner = new Scanner();

        return mScanner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden
//        LogWrapper.d(TAG, "Creating MainActivity");

        setContentView(R.layout.main_layout);

        NavigationUtils.GoToAccessPointListFragment(getFragmentManager());

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction().add(R.id.status_fragment_container, StatusFragment.getInstance()).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.proxy_prefs_activity, menu);
        return true;
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        LogWrapper.d(TAG, "onNewIntent MainActivity");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavigationUtils.GoToAccessPointListFragment(getFragmentManager());
                return true;

            case R.id.menu_about:
                NavigationUtils.GoToHelpFragment(getFragmentManager());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        LogWrapper.d(TAG,"Destroying MainActivity");
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getScanner().resume();

        LogWrapper.d(TAG,"Resuming MainActivity");

        // Start register the status receivers
        IntentFilter ifilt = new IntentFilter();

        ifilt.addAction(APLConstants.APL_UPDATED_PROXY_CONFIGURATION);
        ifilt.addAction(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
        ifilt.addAction(Constants.PROXY_REFRESH_UI);
        registerReceiver(changeStatusReceiver, ifilt);

        ViewServer.get(this).setFocusedWindow(this);

        WhatsNewDialog wnd = new WhatsNewDialog(this);
        wnd.show();

        refreshUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        LogWrapper.d("TAG","Pause MainActivity");

        // Stop the registered status receivers
        unregisterReceiver(changeStatusReceiver);

        getScanner().pause();
        mScanner = null;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        LogWrapper.d(TAG,"Starting MainActivity");
        active = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        LogWrapper.d(TAG,"Stopping MainActivity");
        active = false;
    }

    private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            LogWrapper.logIntent(TAG, intent, Log.INFO, true);

            if (action.equals(APLConstants.APL_UPDATED_PROXY_CONFIGURATION))
            {
                LogWrapper.d(TAG, "Received broadcast for proxy configuration written on device -> RefreshUI");
                refreshUI();
            }
            else if (action.equals(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK))
            {
                LogWrapper.d(TAG, "Received broadcast for partial update on status of proxy configuration - RefreshUI");
                refreshUI();
            }
            else if (action.equals(Constants.PROXY_REFRESH_UI))
            {
                LogWrapper.d(TAG, "Received broadcast for update the Proxy Settings UI - RefreshUI");
                refreshUI();
            }
            else
            {
                LogWrapper.e(TAG, "Received intent not handled: " + intent.getAction());
            }
        }
    };

    private void refreshUI()
    {
//        this.invalidateOptionsMenu();

        AccessPointListFragment.getInstance().refreshUI();
        ProxyDetailsFragment.getInstance().refreshUI();
        StatusFragment.getInstance().refreshUI();
    }


    private class Scanner extends Handler
    {
        private int mRetry = 0;

        void resume()
        {
            if (!hasMessages(0))
            {
                LogWrapper.w(TAG, "Resume Wi-Fi scanner");
                sendEmptyMessage(0);
            }
        }

        void forceScan()
        {
            LogWrapper.w(TAG, "Force Wi-Fi scanner");
            removeMessages(0);
            sendEmptyMessage(0);
        }

        void pause()
        {
            LogWrapper.w(TAG, "Pause Wi-Fi scanner");
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message)
        {
            LogWrapper.w(TAG, "Calling Wi-Fi scanner");

            if (APL.getWifiManager().startScan())
            {
                mRetry = 0;
            }
            else if (++mRetry >= 3)
            {
                mRetry = 0;
                return;
            }

            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }
}
