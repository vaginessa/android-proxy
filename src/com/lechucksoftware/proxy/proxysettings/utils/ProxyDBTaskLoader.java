package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;

import java.util.List;

/**
 * Created by marco on 04/10/13.
 */
public class ProxyDBTaskLoader extends AsyncTaskLoader<List<DBProxy>>
{
    private final Context ctx;

    public ProxyDBTaskLoader(Context context)
    {
        super(context);
        ctx = context;
    }

    @Override
    public List<DBProxy> loadInBackground()
    {
        return ApplicationGlobals.getDBManager().getAllProxiesWithTAGs();
    }
}
