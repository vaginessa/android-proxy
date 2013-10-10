package com.lechucksoftware.proxy.proxysettings.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

/**
 * Created by marco on 10/10/13.
 */
public class TestActivity extends Activity
{
    public static final String TAG = "TestActivity";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden
        LogWrapper.d(TAG, "Creating TestActivity");

        setContentView(R.layout.test_layout);
    }

    public void testDBClicked(View caller)
    {
        AddAsyncProxy addAsyncProxy = new AddAsyncProxy();
        addAsyncProxy.execute();
    }

    public void dismissDialog()
    {
        dialog.dismiss();
    }

    public class AddAsyncProxy extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPostExecute(Void result)
        {
            dismissDialog();
//            getActivity().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        protected void onPreExecute()
        {
            dialog = new ProgressDialog(getApplicationContext());
            dialog.setTitle("AddAsyncProxy");
            dialog.setMessage("Inserting lot of proxies");
            dialog.show();
//            getActivity().setProgressBarIndeterminate(true);
//            getActivity().setProgressBarVisibility(true);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            TestDB.AddProxy();
            return null;
        }

    }
}
