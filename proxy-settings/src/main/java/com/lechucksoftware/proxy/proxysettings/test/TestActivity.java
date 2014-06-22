package com.lechucksoftware.proxy.proxysettings.test;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.lechucksoftware.proxy.proxysettings.exception.ProxyException;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncStartupActions;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import java.util.List;

import be.shouldit.proxy.lib.ProxyConfiguration;

/**
 * Created by marco on 10/10/13.
 */
public class TestActivity extends Activity
{
    public static final String TAG = "TestActivity";
    public LinearLayout testDBContainer;
    private ScrollView testLogScroll;

    public enum TestAction
    {
        ADD_PROXY,
        ADD_TEST_WIFI_NETWORKS,
        REMOVE_TEST_WIFI_NETWORKS,
        ADD_EXAMPLE_PROXIES,
        ADD_TAGS,
        SET_ALL_PROXIES,
        CLEAR_ALL_PROXIES,
        CLEAR_IN_USE,
        TEST_VALIDATION,
        TEST_SERIALIZATION,
        UPDATE_TAGS,
        LIST_TAGS,
        CLEAR_ALL,
        TOGGLE_DEMO_MODE,
        RUN_STARTUP_ACTIONS,
        ASSIGN_PROXY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden
        App.getLogger().d(TAG, "Creating TestActivity");

        setContentView(R.layout.test_layout);

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

    public void addProxyClicked(View caller)
    {
        AsyncTest addAsyncProxy = new AsyncTest(this, TestAction.ADD_PROXY);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addWifiNetworks(View view)
    {
        AsyncTest addAsyncWifiNetworks = new AsyncTest(this, TestAction.ADD_TEST_WIFI_NETWORKS);
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

    public void clearProxyInUse(View view)
    {
        AsyncTest clearInUseAsync = new AsyncTest(this, TestAction.CLEAR_IN_USE);
        clearInUseAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void testBugReporting(View caller)
    {
        EventReportingUtils.sendException(new Exception("EXCEPTION ONLY FOR TEST"));
        EventReportingUtils.sendException(new ProxyException(App.getProxyManager().getSortedConfigurationsList()));
        EventReportingUtils.sendEvent("EVENT ONLY FOR TEST");

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
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
        TestActivity _testActivity;
        TextView textViewTest;
        TestAction _action;

        public AsyncTest(TestActivity testActivity, TestAction action)
        {
            _testActivity = testActivity;
            _action = action;
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
            else if (_action == TestAction.CLEAR_IN_USE)
            {
                TestUtils.clearInUse();
            }
            else if (_action == TestAction.ADD_EXAMPLE_PROXIES)
            {
                TestUtils.addProxyExamples(_testActivity);
            }
            else if (_action == TestAction.ADD_TEST_WIFI_NETWORKS)
            {
                for (int i=0;i<3;i++)
                {
                    String ssid = TestUtils.createFakeWifiNetwork(_testActivity);
                    publishProgress(String.format("Created #[%d] TEST Wi-Fi network: %s", i, ssid));
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

                for (ProxyConfiguration conf : App.getProxyManager().getSortedConfigurationsList())
                {
                    if (App.getInstance().demoMode)
                        conf.setAPDescription(UIUtils.getRandomCodeName().toString());
                    else
                        conf.setAPDescription(null);
                }
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
}
