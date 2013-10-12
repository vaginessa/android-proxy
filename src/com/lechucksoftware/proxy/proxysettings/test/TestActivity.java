package com.lechucksoftware.proxy.proxysettings.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyData;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

/**
 * Created by marco on 10/10/13.
 */
public class TestActivity extends Activity
{
    public static final String TAG = "TestActivity";
    public LinearLayout testDBContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden
        LogWrapper.d(TAG, "Creating TestActivity");

        setContentView(R.layout.test_layout);

        testDBContainer = (LinearLayout) findViewById(R.id.testDBContainer);
    }

    public void testDBClicked(View caller)
    {
        AddAsyncProxy addAsyncProxy = new AddAsyncProxy(this);
        addAsyncProxy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class AddAsyncProxy extends AsyncTask<Void, Integer, Void>
    {
        TestActivity _testActivity;
        TextView textViewTest;

        public AddAsyncProxy(TestActivity testActivity)
        {
            _testActivity=testActivity;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            _testActivity.testDBContainer.removeView(textViewTest);
        }

        @Override
        protected void onPreExecute()
        {
            textViewTest = new TextView(_testActivity);
            textViewTest.setText("Started AsyncProxyTest");
            _testActivity.testDBContainer.addView(textViewTest);
        }

        @Override
        protected void onProgressUpdate(Integer... progress)
        {
            textViewTest.setText(String.format("Added proxy %d",progress));
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            for(int i=0; i<100;i++)
            {
                TestDB.AddProxy();
                publishProgress(i);
            }

            return null;
        }

    }
}
