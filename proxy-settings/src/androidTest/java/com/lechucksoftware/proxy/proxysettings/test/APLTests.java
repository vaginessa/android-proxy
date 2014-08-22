package com.lechucksoftware.proxy.proxysettings.test;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.lechucksoftware.proxy.proxysettings.App;

import be.shouldit.proxy.lib.APL;

/**
 * Created by mpagliar on 22/08/2014.
 */
public class APLTests extends InstrumentationTestCase
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
        for(int i=0;i<3;i++)
        {
            toggleWifi();
        }
    }

    public void toggleWifi() throws Exception
    {
        APL.enableWifi();
        Thread.sleep(3000);
        assertEquals(true, APL.getWifiManager().isWifiEnabled());

        APL.disableWifi();
        Thread.sleep(3000);
        assertEquals(false, APL.getWifiManager().isWifiEnabled());

        APL.enableWifi();
        Thread.sleep(3000);
        assertEquals(true, APL.getWifiManager().isWifiEnabled());
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
}
