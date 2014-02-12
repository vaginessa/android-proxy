package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.exception.ProxyException;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.WifiNetworkId;
import com.shouldit.proxy.lib.enums.SecurityType;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;
import com.shouldit.proxy.lib.utils.ProxyUtils;

import java.util.*;

/**
 * Created by Marco on 15/09/13.
 */
public class CacheManager
{
    private static final String TAG = "CacheManager";
    private final Context context;
    private final Map<UUID, Object> cachedObjects;
    private Map<Long, ProxyEntity> savedProxies;

    public CacheManager(Context ctx)
    {
        context = ctx;
        cachedObjects = Collections.synchronizedMap(new HashMap<UUID, Object>());
        savedProxies = ApplicationGlobals.getDBManager().getAllProxiesWithTAGs();
    }

    public List<ProxyEntity> getAllProxiesList()
    {
        List<ProxyEntity> proxies = new ArrayList<ProxyEntity>(getAllProxies().values());
        return proxies;
    }

    private Map<Long,ProxyEntity> getAllProxies()
    {
        if (savedProxies == null)
            savedProxies = ApplicationGlobals.getDBManager().getAllProxiesWithTAGs();

        return savedProxies;
    }

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

        if (savedProxies != null)
        {
            savedProxies.clear();
            savedProxies = null;
        }
    }
}
