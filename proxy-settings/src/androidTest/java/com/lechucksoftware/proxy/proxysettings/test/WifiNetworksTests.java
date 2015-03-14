package com.lechucksoftware.proxy.proxysettings.test;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.lechucksoftware.proxy.proxysettings.App;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import be.shouldit.proxy.lib.APL;

/**
 * Created by mpagliar on 22/08/2014.
 */
public class WifiNetworksTests
{
    @BeforeClass
    public static void setUpAPL()
    {
        APL.setup(App.getInstance());
    }

    @Test
    public void toggleWifi() throws Exception
    {
        for(int i=0;i<10;i++)
        {
            APL.enableWifi();
            Thread.sleep(5000);
            Assert.assertEquals(true, APL.getWifiManager().isWifiEnabled());

            APL.disableWifi();
            Thread.sleep(5000);
            Assert.assertEquals(false, APL.getWifiManager().isWifiEnabled());

            APL.enableWifi();
            Thread.sleep(5000);
            Assert.assertEquals(true, APL.getWifiManager().isWifiEnabled());
        }
    }
}
