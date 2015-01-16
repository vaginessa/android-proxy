package com.lechucksoftware.proxy.proxysettings.test;

import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.WiFiAPEntity;

import java.util.Map;
import java.util.Random;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;

/**
 * Created by mpagliar on 22/08/2014.
 */
public class WifiNetworksTests extends InstrumentationTestCase
{
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        APL.setup(App.getInstance());
    }

    @SmallTest
    public void testToggleWifi() throws Exception
    {
        for(int i=0;i<10;i++)
        {
            toggleWifi();
        }
    }

    public void toggleWifi() throws Exception
    {
        APL.enableWifi();
        Thread.sleep(5000);
        assertEquals(true, APL.getWifiManager().isWifiEnabled());

        APL.disableWifi();
        Thread.sleep(5000);
        assertEquals(false, APL.getWifiManager().isWifiEnabled());

        APL.enableWifi();
        Thread.sleep(5000);
        assertEquals(true, APL.getWifiManager().isWifiEnabled());
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
}
