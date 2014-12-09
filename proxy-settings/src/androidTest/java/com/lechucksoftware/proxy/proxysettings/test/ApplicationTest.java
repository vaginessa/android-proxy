package com.lechucksoftware.proxy.proxysettings.test;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.lechucksoftware.proxy.proxysettings.App;

import java.util.List;

import be.shouldit.proxy.lib.WiFiAPConfig;

/**
 * Created by mpagliar on 22/08/2014.
 */
public class ApplicationTest extends ApplicationTestCase<App>
{
    public ApplicationTest()
    {
        super(App.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        createApplication();
    }

    @SmallTest
    public void testWifiNetworksManagerStartup() throws Exception
    {
        List<WiFiAPConfig> result = App.getWifiNetworksManager().getSortedWifiApConfigsList();
        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        terminateApplication();
    }
}
