package com.lechucksoftware.proxy.proxysettings.test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncStartupActions;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.DatabaseUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marco on 10/10/13.
 */
public class TestActivity extends Activity
{
    public static final String TAG = TestActivity.class.getSimpleName();
    public LinearLayout testDBContainer;
    private ScrollView testLogScroll;
    private Button addWifiNetworksBtn;
    private TestActivity testActivity;


    public enum TestAction
    {
        ADD_PROXY,
        ADD_TEST_WIFI_NETWORKS,
        REMOVE_TEST_WIFI_NETWORKS,
        ADD_EXAMPLE_PROXIES,
        ADD_TAGS,
        SET_ALL_PROXIES,
        CLEAR_ALL_PROXIES,
        TEST_VALIDATION,
        TEST_SERIALIZATION,
        UPDATE_TAGS,
        LIST_TAGS,
        CLEAR_ALL,
        BACKUP_DB,
        TOGGLE_DEMO_MODE,
        RUN_STARTUP_ACTIONS,
        ASSIGN_PROXY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden
        App.getLogger().d(TAG, "Creating TestActivity");

        testActivity = this;

        setContentView(R.layout.test_layout);

        addWifiNetworksBtn = (Button) findViewById(R.id.add_wifi_networks);
        addWifiNetworksBtn.setOnTouchListener(new View.OnTouchListener() {

            private Date touchEventStarted;
            private Toast toast;
            private Context context;
            private Boolean touching;
            public AsyncToast asyncToast;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                App.getLogger().d(TAG, "Touch Event: " + String.valueOf(motionEvent.getActionMasked()));
                context = view.getContext();

                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    // Start touch
                    touchEventStarted = new Date();
                    touching = true;

                    asyncToast = new AsyncToast(testActivity, touchEventStarted);
                    asyncToast.execute();
                }
                else if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    // End touch
                    if (touchEventStarted != null)
                    {
                        Date touchEventEnd = new Date();
                        Long diff = touchEventEnd.getTime() - touchEventStarted.getTime();
                        int numWifis = (int) ((diff / 100) % 200) + 1;
                        addWifiNetworks(view,numWifis);

                        touching = false;
                        asyncToast.stop();
                    }
                }

                return true;
            }

        });

        testLogScroll = (ScrollView) findViewById(R.id.test_log);
        testDBContainer = (LinearLayout) findViewById(R.id.testDBContainer);
    }

    public void APNTest(View view)
    {
        if (Build.VERSION.SDK_INT >= 19)
        {
            TestUtils.testAPN(this);
        }
    }

    public void backupDB(View view)
    {
        DatabaseUtils.backupDB(this);
    }

    public void addProxyClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.ADD_PROXY);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addWifiNetworks(View view, int numWifiToAdd)
    {
        AsyncTest addAsyncWifiNetworks = new AsyncTest(this, TestAction.ADD_TEST_WIFI_NETWORKS, numWifiToAdd);
        addAsyncWifiNetworks.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removeWifiNetworks(View view)
    {
        AsyncTest removeAsyncWifiNetworks = new AsyncTest(this, TestAction.REMOVE_TEST_WIFI_NETWORKS);
        removeAsyncWifiNetworks.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void startStartupActions(View view)
    {
        AsyncTest startupActionsAsync = new AsyncTest(this, TestAction.RUN_STARTUP_ACTIONS);
        startupActionsAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addExampleProxyClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.ADD_EXAMPLE_PROXIES);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void toggleDemoModeClicked(View caller)
    {
        AsyncTest toggleDemoMode = new AsyncTest(this, TestAction.TOGGLE_DEMO_MODE);
        toggleDemoMode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addTagsDBClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.ADD_TAGS);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setProxyForAllAp(View view)
    {
        AsyncTest setAllProxies = new AsyncTest(this, TestAction.SET_ALL_PROXIES);
        setAllProxies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void clearProxyForAllAp(View view)
    {
        AsyncTest clearAsyncProxy = new AsyncTest(this, TestAction.CLEAR_ALL_PROXIES);
        clearAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void testProxyValidations(View view)
    {
        AsyncTest testValidation = new AsyncTest(this, TestAction.TEST_VALIDATION);
        testValidation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void testBugReporting(View caller)
    {
        Map<String,String> map = new HashMap<String, String>();
        map.put("config_list", App.getWifiNetworksManager().configListToDBG().toString());

        App.getEventsReporter().sendException(new Exception("EXCEPTION ONLY FOR TEST"), map);
        App.getEventsReporter().sendEvent("EVENT ONLY FOR TEST");

//        GoogleAnalytics.getInstance(this).dispatchLocalHits();

        // Force a CRASH
//        throw new RuntimeException("Application forced to crash!");
    }

    public void listDBProxies(View caller)
    {
        TextView textViewTest = new TextView(this);
        testDBContainer.addView(textViewTest);
        List<ProxyEntity> list = App.getCacheManager().getAllProxiesList();
        for (ProxyEntity p : list)
        {
            textViewTest.append(p.toString() + "\n\n");
        }
    }

    public void listDBTags(View caller)
    {
        TextView textViewTest = new TextView(this);
        testDBContainer.addView(textViewTest);
        List<TagEntity> list = App.getDBManager().getAllTags();
        for (TagEntity t : list)
        {
            textViewTest.append(t.toString() + "\n\n");
        }
    }

    public void clearOutput(View caller)
    {
        testDBContainer.removeAllViews();
    }

    public void testSerializationClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.TEST_SERIALIZATION);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void clearPrefAndDB(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.CLEAR_ALL);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class AsyncTest extends AsyncTask<Void, String, Void>
    {
        private final Object[] _params;
        TestActivity _testActivity;
        TextView textViewTest;
        TestAction _action;

        public AsyncTest(TestActivity testActivity, TestAction action, Object... params)
        {
            _testActivity = testActivity;
            _action = action;
            _params = params;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            _testActivity.testLogScroll.fullScroll(View.FOCUS_DOWN);
        }

        @Override
        protected void onPreExecute()
        {
            textViewTest = new TextView(_testActivity);
            textViewTest.setText("Started AsyncTestAction: " + _action);
            _testActivity.testDBContainer.addView(textViewTest);
        }

        @Override
        protected void onProgressUpdate(String... progress)
        {
            if (progress != null && progress.length > 0)
            {
                String msg = TextUtils.join("\n",progress);
                textViewTest.setText(msg);
            }
            else
                textViewTest.setText(_action.toString());
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            if (_action == TestAction.CLEAR_ALL)
            {
                SharedPreferences preferences = _testActivity.getSharedPreferences(Constants.PREFERENCES_FILENAME, MODE_MULTI_PROCESS);

                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

//                ApplicationStatistics.updateInstallationDetails(getApplicationContext());

                App.getDBManager().resetDB();
            }
            else if (_action == TestAction.ADD_EXAMPLE_PROXIES)
            {
                TestUtils.addProxyExamples(_testActivity);
            }
            else if (_action == TestAction.ADD_TEST_WIFI_NETWORKS)
            {
                int numWifis = (Integer) _params[0];

                for (int i=0;i<numWifis;i++)
                {
                    String ssid = TestUtils.createFakeWifiNetwork(_testActivity);
                    publishProgress(String.format("Created #[%d] TEST Wi-Fi network: %s", 1, ssid));
                }
            }
            else if (_action == TestAction.REMOVE_TEST_WIFI_NETWORKS)
            {
                int removedCount = TestUtils.deleteFakeWifiNetworks(_testActivity);
                publishProgress(String.format("Removed #[%d] TEST Wi-Fi networks", removedCount));
            }
            else if (_action == TestAction.RUN_STARTUP_ACTIONS)
            {
                ApplicationStatistics.updateInstallationDetails(_testActivity);

                ApplicationStatistics statistics = ApplicationStatistics.getInstallationDetails(_testActivity);
                publishProgress(statistics.toString());

                AsyncStartupActions async = new AsyncStartupActions(_testActivity);
                async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else if (_action == TestAction.TOGGLE_DEMO_MODE)
            {
                // TODO: improve handling of preference cache
                Utils.checkDemoMode(_testActivity);
                Utils.setDemoMode(_testActivity, !App.getInstance().demoMode);
                Utils.checkDemoMode(_testActivity);

//                for (WiFiAPConfig conf : App.getWifiNetworksManager().getSortedWifiApConfigsList())
//                {
//                    if (App.getInstance().demoMode)
//                        conf.setAPDescription(UIUtils.getRandomCodeName().toString());
//                    else
//                        conf.setAPDescription(null);
//                }
            }
            else if (_action == TestAction.SET_ALL_PROXIES)
            {
                TestUtils.setAllProxies(_testActivity);
            }
            else if (_action == TestAction.CLEAR_ALL_PROXIES)
            {
                TestUtils.clearAllProxies(_testActivity);
            }
            else if (_action == TestAction.TEST_VALIDATION)
            {
                TestUtils.testValidation();
            }
            else
            {
                for (int i = 0; i < 10; i++)
                {
                    switch (_action)
                    {
                        case ADD_PROXY:
                            TestUtils.addProxy();
                            break;
                        case TEST_SERIALIZATION:
                            TestUtils.testSerialization();
                            break;
                        case ADD_TAGS:
                            TestUtils.addTags();
                            break;
                        case UPDATE_TAGS:
//                            TestUtils.addProxy();
                            break;
                    }

                    publishProgress(String.valueOf(i));
                }
            }


            return null;
        }

    }

    private class AsyncToast extends AsyncTask<Void, String, Void>
    {
        private final Activity activity;
        private final Date start;
        Toast toast;
        boolean run;

        public AsyncToast(Activity callingActivity, Date eventStarted)
        {
            activity = callingActivity;
            start = eventStarted;
            run = true;
        }

        @Override
        protected void onPostExecute(Void result)
        {

        }

        @Override
        protected void onPreExecute()
        {

        }

        public void stop()
        {
            run = false;
        }

        @Override
        protected void onProgressUpdate(String... progress)
        {
            if (toast != null)
            {
                toast.cancel();
            }

            App.getLogger().d(TAG, progress[0]);
            toast = Toast.makeText(activity, progress[0], Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            while(run)
            {
                Date touchEventPartial = new Date();
                Long diff = touchEventPartial.getTime() - start.getTime();
                int numWifis = (int) ((diff / 100) % 200) + 1;

                publishProgress("Num: " + String.valueOf(numWifis));

                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            return null;
        }

    }

}
