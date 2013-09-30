package com.lechucksoftware.proxy.proxysettings.db;

import android.content.Context;
import android.os.AsyncTask;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;

import java.util.List;

/**
 * Created by marco on 30/09/13.
 */
public class DBManager
{
    private final Context context;
    private ProxyDataSource pds;
    private boolean dbAvailable;

    public DBManager(Context ctx)
    {
        context = ctx;
        pds = new ProxyDataSource(ctx);
        SetupDB();
    }

    public void SetupDB()
    {
        dbAvailable = false;
        OpenDatabaseTask task = new OpenDatabaseTask();
        task.execute();
    }

    public void CloseDB()
    {
        dbAvailable = false;
        pds.close();
    }

    public List<ProxyData> getAllProxies()
    {
        if (dbAvailable)
            return pds.getAllProxies();
        else
        {
            BugReportingUtils.sendException(new Exception("getAllProxies - ProxyDataSource not initialized correctly"));
            return null;
        }
    }

    public void upsertProxy(ProxyData pd)
    {
        if (dbAvailable)
        {
            pds.upsertProxy(pd);
        }
        else
        {
            BugReportingUtils.sendException(new Exception("upsertProxy - ProxyDataSource not initialized correctly"));
        }
    }

    private class OpenDatabaseTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            pds.openWritable();
            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            dbAvailable = true;
        }
    }
}
