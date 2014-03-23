package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import java.util.*;

/**
 * Created by Marco on 15/09/13.
 */
public class CacheManager
{
    private static final String TAG = "CacheManager";
    private final Context context;
    private final Map<UUID, Object> cachedObjects;
//    private Map<Long, ProxyEntity> savedProxies;
//    private Object proxyLock = new Object();

    public CacheManager(Context ctx)
    {
        context = ctx;

        cachedObjects = Collections.synchronizedMap(new HashMap<UUID, Object>());
//        savedProxies = ApplicationGlobals.getDBManager().getAllProxiesWithTAGs();
    }

    public List<ProxyEntity> getAllProxiesList()
    {
        List<ProxyEntity> proxies;

        Map<Long, ProxyEntity> savedProxies = ApplicationGlobals.getDBManager().getAllProxiesWithTAGs();
        proxies = new ArrayList<ProxyEntity>(savedProxies.values());

        return proxies;
    }

//    private Map<Long, ProxyEntity> getAllProxies()
//    {
//        synchronized (proxyLock)
//        {
//            if (savedProxies == null)
//                savedProxies = ApplicationGlobals.getDBManager().getAllProxiesWithTAGs();
//        }
//
//        return savedProxies;
//    }

    public void put(UUID key, Object obj)
    {
        if (cachedObjects.containsKey(key))
        {
            release(key);
        }

        cachedObjects.put(key, obj);
    }

    public Object get(UUID key)
    {
        return cachedObjects.get(key);
    }

    public void release(UUID key)
    {
        cachedObjects.remove(key);
    }

    public void clear()
    {
        if (cachedObjects != null)
        {
            cachedObjects.clear();
        }

//        synchronized (proxyLock)
//        {
//            if (savedProxies != null)
//            {
//                savedProxies.clear();
//                savedProxies = null;
//            }
//        }
    }
}

