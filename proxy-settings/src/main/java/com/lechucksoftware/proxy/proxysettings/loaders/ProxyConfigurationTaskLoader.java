package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.lechucksoftware.proxy.proxysettings.App;

import java.util.List;

import be.shouldit.proxy.lib.WiFiAPConfig;

/**
 * Created by marco on 04/10/13.
 */
public class ProxyConfigurationTaskLoader extends AsyncTaskLoader<List<WiFiAPConfig>>
{
    private final Context ctx;

    public ProxyConfigurationTaskLoader(Context context)
    {
        super(context);
        ctx = context;
    }

    @Override
    public List<WiFiAPConfig> loadInBackground()
    {
        List<WiFiAPConfig> result = App.getWifiNetworksManager().getSortedWifiApConfigsList();
        return result;
    }
}
