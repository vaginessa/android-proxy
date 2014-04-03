package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import java.util.Collections;
import java.util.List;

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
        List<ProxyEntity> proxyEntityList = App.getCacheManager().getAllProxiesList();
        Collections.sort(proxyEntityList);
        return  proxyEntityList;
    }
}
