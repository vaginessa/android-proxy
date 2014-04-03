package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.lechucksoftware.proxy.proxysettings.App;
import be.shouldit.proxy.lib.ProxyConfiguration;

import java.util.List;

/**
 * Created by marco on 04/10/13.
 */
public class ProxyConfigurationTaskLoader extends AsyncTaskLoader<List<ProxyConfiguration>>
{
    private final Context ctx;

    public ProxyConfigurationTaskLoader(Context context)
    {
        super(context);
        ctx = context;
    }

    @Override
    public List<ProxyConfiguration> loadInBackground()
    {
        return App.getProxyManager().getSortedConfigurationsList();
    }
}
