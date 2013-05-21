package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.AccessPointListFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyDetailsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.shouldit.proxy.lib.APLConstants;


/**
 * Created by marco on 17/05/13.
 */
public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity";

    //    private ProxyDetailsFragment mainFragment;
//    private HelpPrefsFragment helpFragment;
//    private ProxyCheckerPrefsFragment checkFragment;
//    private AdvancedPrefsFragment advFragment;
//    private AccessPointListFragment apSelectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null)
        {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null)
            {
                return;
            }

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.fragment_container, AccessPointListFragment.getInstance()).commit();
        }

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.status_fragment_container) != null)
        {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null)
            {
                return;
            }

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.status_fragment_container, StatusFragment.getInstance()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        FragmentTransaction transaction = null;

        switch (item.getItemId())
        {
            case android.R.id.home:
                // Clean-up the backstack when going back to home
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, AccessPointListFragment.getInstance());
                //transaction.addToBackStack(null);
                transaction.commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onDestroy()
    {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Start register the status receivers
        IntentFilter ifilt = new IntentFilter();

        ifilt.addAction(APLConstants.APL_UPDATED_PROXY_CONFIGURATION);
        ifilt.addAction(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);

        ifilt.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        ifilt.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        ifilt.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        ifilt.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//		ifilt.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        // ifilt.addAction(Constants.PROXY_REFRESH_UI);
        registerReceiver(changeStatusReceiver, ifilt);

        ViewServer.get(this).setFocusedWindow(this);

        refreshUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Stop the registered status receivers
        unregisterReceiver(changeStatusReceiver);
    }

    private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(APLConstants.APL_UPDATED_PROXY_CONFIGURATION))
            {
                LogWrapper.d(TAG, "Received broadcast for updated proxy configuration - RefreshUI");
                refreshUI();
            }
            else if (action.equals(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK))
            {
                LogWrapper.d(TAG, "Received broadcast for partial update to proxy configuration - RefreshUI");
                refreshUI();
            }
            else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                    || action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)
                    || action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
                    || action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
            {
                LogWrapper.logIntent(TAG, intent, Log.DEBUG, true);
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
        this.invalidateOptionsMenu();

        if (ProxyDetailsFragment.getInstance().isVisible())
        {
            ProxyDetailsFragment.getInstance().refreshUI();
        }

        if (AccessPointListFragment.getInstance().isVisible())
        {
            AccessPointListFragment.getInstance().refreshUI();
        }

        if (StatusFragment.getInstance().isVisible())
        {
            StatusFragment.getInstance().refreshUI();
        }
//        if (checkFragment.isVisible())
//        {
//            checkFragment.refreshUI();
//        }
    }

    static boolean active = false;

    @Override
    public void onStart()
    {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        active = false;
    }
}
