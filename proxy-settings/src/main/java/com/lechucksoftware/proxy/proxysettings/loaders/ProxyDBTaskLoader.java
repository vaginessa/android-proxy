package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by marco on 04/10/13.
 */
public class ProxyDBTaskLoader extends AsyncTaskLoader<List<ProxyEntity>>
{
    private final Context ctx;

    public ProxyDBTaskLoader(Context context)
    {
        super(context);
        ctx = context;
    }

    @Override
    public List<ProxyEntity> loadInBackground()
    {
        Map<Long, ProxyEntity> savedProxies = App.getDBManager().getAllProxiesWithTAGs();
        List<ProxyEntity> proxyEntityList = new ArrayList<ProxyEntity>(savedProxies.values());
        Collections.sort(proxyEntityList);
        return  proxyEntityList;
    }
}
