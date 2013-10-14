package com.lechucksoftware.proxy.proxysettings.test;

import android.text.TextUtils;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.ProxyData;

import java.util.Random;

/**
 * Created by marco on 10/10/13.
 */
public class TestDB
{
    public static String getRandomExclusionList()
    {
        Random r = new Random();
        int maxEx = r.nextInt(10);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<maxEx; i++)
        {
            if (i!=0)
                sb.append(",");
            sb.append(getRandomIP());
        }

        return sb.toString();
    }

    public static String getRandomIP()
    {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }

    public static int getRandomPort()
    {
        Random r = new Random();
        return r.nextInt(65536);
    }

    public static ProxyData getRandomProxy()
    {
        ProxyData pd = new ProxyData();
        pd.host = getRandomIP();
        pd.port = getRandomPort();
        pd.exclusion = getRandomExclusionList();

        return pd;
    }

    public static void AddProxy()
    {
        ProxyData pd = getRandomProxy();
        ApplicationGlobals.getDBManager().upsertProxy(pd);
    }
}
