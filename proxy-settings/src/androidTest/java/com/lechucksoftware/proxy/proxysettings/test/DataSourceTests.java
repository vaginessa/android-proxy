package com.lechucksoftware.proxy.proxysettings.test;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.Smoke;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
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
        WifiConfiguration wifiConfiguration = DevelopmentUtils.prepareFakeWifiNetwork();
        ProxyEntity proxyEntity = DevelopmentUtils.createRandomHTTPProxy();
        PacEntity pacProxy = DevelopmentUtils.createRandomPACProxy();
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
        wae1.setProxyEntity(proxyEntity);
        assertTrue(!wae1.equals(wae2));
        assertTrue(!wae1.equals(wae3));

        WiFiAPEntity wae4 = App.getDBManager().upsertWifiAP(wae1);
        assertTrue(!wae1.equals(wae4));
        assertEquals(wae4.getSsid(), wae4.getSsid());
        assertEquals(wae4.getSecurityType(), wae4.getSecurityType());
        assertEquals(wae4.getProxySetting(), wae4.getProxySetting());
        assertEquals(wae4.getPacId(), wae4.getPacId());
        assertEquals(wae4.getProxyId(), wae4.getProxyId());

        assertTrue(!wae4.equals(wae2));
        assertTrue(!wae4.equals(wae3));

        long ae5Id = App.getDBManager().findWifiAp(wiFiApConfig);
        assertTrue(ae5Id != -1);
        assertEquals(ae3Id, ae5Id);
        WiFiAPEntity wae5 = App.getDBManager().getWifiAP(ae3Id);
        assertEquals(wae4,wae5);

        wae5.setProxySetting(ProxySetting.PAC);
        wae5.setPacEntity(pacProxy);
        WiFiAPEntity wae6 = App.getDBManager().upsertWifiAP(wae5);
        assertTrue(!wae5.equals(wae6));
        assertEquals(wae5.getSsid(), wae6.getSsid());
        assertEquals(wae5.getSecurityType(), wae6.getSecurityType());
        assertEquals(wae5.getProxySetting(), wae6.getProxySetting());
        assertEquals(wae5.getPacId(), wae6.getPacId());
        assertEquals(wae5.getProxyId(), wae6.getProxyId());

        wae6.setProxySetting(ProxySetting.NONE);
        wae6.setPACId(-1L);
        wae6.setProxyId(-1L);
        WiFiAPEntity wae7 = App.getDBManager().upsertWifiAP(wae6);

        App.getDBManager().deleteWifiAP(wae7.getId());
        long ae8id = App.getDBManager().findWifiAp(wiFiApConfig);
        assertEquals(ae8id, -1L);
    }
}