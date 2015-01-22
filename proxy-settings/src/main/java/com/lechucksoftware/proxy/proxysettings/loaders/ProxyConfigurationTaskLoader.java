package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.lechucksoftware.proxy.proxysettings.App;

import java.util.List;

import be.shouldit.proxy.lib.WiFiApConfig;

/**
 * Created by marco on 04/10/13.
 */
public class ProxyConfigurationTaskLoader extends AsyncTaskLoader<List<WiFiApConfig>>
{
    private final Context ctx;

    public ProxyConfigurationTaskLoader(Context context)
    {
        super(context);
        ctx = context;
    }

    @Override
    public List<WiFiApConfig> loadInBackground()
    {
        List<WiFiApConfig> result = App.getWifiNetworksManager().getSortedWifiApConfigsList();
        return result;
    }
}
