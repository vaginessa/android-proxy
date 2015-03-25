package com.lechucksoftware.proxy.proxysettings.test;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.Smoke;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.WiFiAPEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@Smoke
public class DataSourceTests
{
    private Context context;

    @Before
    public void setUp() throws Exception
    {
        context = InstrumentationRegistry.getContext();
        APL.setup(context);
    }

    @After
    public void tearDown () throws Exception
    {

    }

    @Test
    public void testWiFiApEntityDB() throws Exception
    {
        WifiConfiguration wifiConfiguration = TestUtils.prepareFakeWifiNetwork();
        ProxyEntity proxyEntity = TestUtils.createRandomHTTPProxy();
        App.getDBManager().upsertProxy(proxyEntity);

        WiFiApConfig wiFiApConfig = APL.getWiFiAPConfiguration(wifiConfiguration);

        WiFiAPEntity wae1 = new WiFiAPEntity();
        wae1.setSsid(wiFiApConfig.getSSID());
        wae1.setSecurityType(wiFiApConfig.getSecurityType());
        wae1.setProxySetting(wiFiApConfig.getProxySetting());

        assertTrue(App.getDBManager().findWifiAp(wae1) == -1);

        WiFiAPEntity wae2 = App.getDBManager().upsertWifiAP(wae1);
        assertEquals(wae1,wae2);

        long ae3Id = App.getDBManager().findWifiAp(wiFiApConfig);
        assertTrue(ae3Id != -1);
        WiFiAPEntity wae3 = App.getDBManager().getWifiAP(ae3Id);
        assertEquals(wae1, wae3);

        wae1.setProxySetting(ProxySetting.STATIC);
        wae1.setProxy(proxyEntity);
        assertTrue(!wae1.equals(wae2));
        assertTrue(!wae1.equals(wae3));

        WiFiAPEntity wae4 = App.getDBManager().upsertWifiAP(wae1);
        assertTrue(!wae1.equals(wae4));
        assertTrue(!wae4.equals(wae2));
        assertTrue(!wae4.equals(wae3));

        wae1 = null;
        wae2 = null;
        wae3 = null;
        wae4 = null;

    }
}