package com.lechucksoftware.proxy.proxysettings.activities;

import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.dialogs.BetaTestApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.dialogs.RateApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.fragments.AccessPointListFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.WifiAPDetailsFragment;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.lechucksoftware.proxy.proxysettings.test.TestActivity;
import com.lechucksoftware.proxy.proxysettings.utils.InstallationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.lechucksoftware.proxy.proxysettings.utils.WhatsNewDialog;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.BuildConfig;
import com.shouldit.proxy.lib.log.LogWrapper;

import java.util.Calendar;


/**
 * Created by marco on 17/05/13.
 */
public class MainActivity extends BaseActivity
{
    public static String TAG = MainActivity.class.getSimpleName();

    AsyncStartupDialogTask asyncStartupDialogTask;
    AsyncStartupRateTask asyncStartupRateTask;
    AsyncStartupBetaTestTask asyncStartupBetaTestTask;

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

            case R.id.menu_proxies:
                NavigationUtils.GoToProxiesList(getFragmentManager());
                return true;

//            case R.id.menu_feedbacks:
//                NavigationUtils.GoToAppFeedbacks(getFragmentManager());
//                return true;

            case R.id.menu_developer:
                final Intent intent = new Intent(this, TestActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getScanner().resume();

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

        asyncStartupDialogTask = new AsyncStartupDialogTask();
        asyncStartupDialogTask.execute();

        asyncStartupRateTask = new AsyncStartupRateTask();
        asyncStartupRateTask.execute();

        asyncStartupBetaTestTask = new AsyncStartupBetaTestTask();
        asyncStartupBetaTestTask.execute();

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
        WifiAPDetailsFragment.getInstance().refreshUI();
    }

    private class AsyncStartupDialogTask extends AsyncTask<Void, Void, Boolean>
    {
        WhatsNewDialog wnd = null;

        @Override
        protected void onPostExecute(Boolean showDialog)
        {
            super.onPostExecute(showDialog);

            if (wnd != null && showDialog)
            {
                LogWrapper.d(TAG,"show AsyncStartupDialogTask");
                wnd.show();
            }
            else
            {
                LogWrapper.d(TAG,"NOT show AsyncStartupDialogTask");
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            wnd = new WhatsNewDialog(MainActivity.this);
            return wnd.isToShow();
        }
    }

    private class AsyncStartupRateTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPostExecute(Boolean showDialog)
        {
            super.onPostExecute(showDialog);

            if (showDialog)
            {
                RateApplicationAlertDialog dialog = RateApplicationAlertDialog.newInstance();
                dialog.show(MainActivity.this.getFragmentManager(), TAG);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            return showAppRate();
        }
    }

    private class AsyncStartupBetaTestTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPostExecute(Boolean showDialog)
        {
            super.onPostExecute(showDialog);

            if (showDialog)
            {
                BetaTestApplicationAlertDialog dialog = BetaTestApplicationAlertDialog.newInstance();
                dialog.show(getFragmentManager(), TAG);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            return showAppBetaTest();
        }
    }

    public void dontDisplayAgainAppRate()
    {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        if (editor != null)
        {
            editor.putBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, true);
            editor.commit();
        }
    }

    public boolean showAppRate()
    {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        if (prefs.getBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, false))
        {
            return false;
        }

        InstallationStatistics statistics = InstallationStatistics.GetInstallationDetails(getApplicationContext());

        // Wait at least N days before opening
        if (statistics.launchCount >= Constants.APPRATE_LAUNCHES_UNTIL_PROMPT)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(statistics.launhcFirstDate);
            c.add(Calendar.DATE, Constants.APPRATE_DAYS_UNTIL_PROMPT);

            if (System.currentTimeMillis() >= c.getTime().getTime())
            {
                return true;
            }
        }

        return false;
    }

    public void dontDisplayAgainBetaTest()
    {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        if (editor != null)
        {
            editor.putBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, true);
            editor.commit();
        }
    }

    public boolean showAppBetaTest()
    {
//        return true;

        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        if (prefs.getBoolean(Constants.PREFERENCES_BETATEST_DONT_SHOW_AGAIN, false))
        {
            return false;
        }

        InstallationStatistics statistics = InstallationStatistics.GetInstallationDetails(getApplicationContext());

        // Wait at least N days before opening
        if (statistics.launchCount >= Constants.BETATEST_LAUNCHES_UNTIL_PROMPT)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(statistics.launhcFirstDate);
            c.add(Calendar.DATE, Constants.BETATEST_DAYS_UNTIL_PROMPT);

            if (System.currentTimeMillis() >= c.getTime().getTime())
            {
                return true;
            }
        }

        return false;
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
