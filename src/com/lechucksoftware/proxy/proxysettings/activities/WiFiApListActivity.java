package com.lechucksoftware.proxy.proxysettings.activities;

import android.content.*;
import android.os.*;
import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.lechucksoftware.proxy.proxysettings.utils.*;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.BuildConfig;
import com.shouldit.proxy.lib.log.LogWrapper;


/**
 * Created by marco on 17/05/13.
 */
public class WiFiApListActivity extends BaseWifiActivity
{
    public static String TAG = WiFiApListActivity.class.getSimpleName();

    AsyncStartupDialogTask asyncStartupDialogTask;
    AsyncStartupRateTask asyncStartupRateTask;
    AsyncStartupBetaTestTask asyncStartupBetaTestTask;

    private static WiFiApListActivity instance;
    public static WiFiApListActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden

        setContentView(R.layout.main_layout);

        NavigationUtils.GoToAccessPointListFragment(getFragmentManager());

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction().add(R.id.status_fragment_container, StatusFragment.getInstance()).commit();

        asyncStartupDialogTask = new AsyncStartupDialogTask(this);
        asyncStartupRateTask = new AsyncStartupRateTask(this);
        asyncStartupBetaTestTask = new AsyncStartupBetaTestTask(this);
        asyncStartupDialogTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        asyncStartupRateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        asyncStartupBetaTestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        instance = this;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Start register the status receivers
        IntentFilter ifilt = new IntentFilter();

        ifilt.addAction(APLConstants.APL_UPDATED_PROXY_CONFIGURATION);
        ifilt.addAction(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
        ifilt.addAction(Constants.PROXY_REFRESH_UI);
        registerReceiver(changeStatusReceiver, ifilt);

        if (BuildConfig.DEBUG)
        {
            // ONLY on DEBUG
            ViewServer.get(this).setFocusedWindow(this);
        }

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
        IBaseFragment f = (IBaseFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        f.refreshUI();
    }
}
