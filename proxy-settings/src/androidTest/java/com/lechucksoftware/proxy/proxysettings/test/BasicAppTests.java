package com.lechucksoftware.proxy.proxysettings.test;

import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.enums.SecurityType;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

//import static android.support.test.espresso.contrib.DrawerActions.openDrawer;

@RunWith(AndroidJUnit4.class)
public class BasicAppTests extends ActivityInstrumentationTestCase2<MasterActivity>
{
    private MasterActivity mActivity;

    public BasicAppTests()
    {
        super(MasterActivity.class);
    }

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    @After
    public void tearDown () throws Exception
    {
        mActivity.finish();
    }

    @Test
    public void checkPreconditions()
    {
        assertThat(mActivity, notNullValue());

        // Check that Instrumentation was correctly injected in setUp()
        assertThat(getInstrumentation(), notNullValue());
    }

    @Test
    public void createNewStaticProxy()
    {
        openDrawer(R.id.drawer_layout);

        onView(withText(R.string.static_proxies)).perform(click());
        onView(withId(R.id.add_new_proxy)).perform(click());

        ProxyEntity staticProxy = TestUtils.createRandomHTTPProxy();
        onView(allOf(withId(R.id.field_value), isDescendantOfA(withId(R.id.proxy_host)))).perform(typeText(staticProxy.getHost()));
        onView(allOf(withId(R.id.field_value), isDescendantOfA(withId(R.id.proxy_port)))).perform(typeText(String.valueOf(staticProxy.getPort())));

//        onView(allOf(withId(R.id.field_value), isDescendantOfA(withId(R.id.proxy_bypass)))).perform(typeText(String.valueOf(proxyPort)));

        onView(withId(R.id.menu_save)).perform(click());

        assertTrue(App.getDBManager().findProxy(staticProxy.getHost(),staticProxy.getPort(),"") != -1);
    }

    @Test
    public void createNewPACProxy()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            openDrawer(R.id.drawer_layout);

            onView(withText(R.string.pac_proxies)).perform(click());
            onView(withId(R.id.add_new_proxy)).perform(click());

            PacEntity pacProxy = TestUtils.createRandomPACProxy();

            onView(allOf(withId(R.id.field_value), isDescendantOfA(withId(R.id.pac_url)))).perform(typeText(String.valueOf(pacProxy.getPacUriFile())));

            onView(withId(R.id.menu_save)).perform(click());
        }
    }

    @Test
    public void enableStaticProxyForWifiNetwork()
    {
        assertTrue(APL.getWifiManager().isWifiEnabled());

        openDrawer(R.id.drawer_layout);
        onView(withText(R.string.wifi_networks)).perform(click());

        Map<APLNetworkId, WiFiApConfig> configuredNetworks = APL.getWifiAPConfigurations();
        assertTrue(configuredNetworks.size() > 0);

        WiFiApConfig selectedWifiApConfig = null;

        for (APLNetworkId networkId : configuredNetworks.keySet())
        {
            WiFiApConfig wifiApConfig = configuredNetworks.get(networkId);

            if (wifiApConfig.getSecurityType() != SecurityType.SECURITY_EAP)
            {
                selectedWifiApConfig = wifiApConfig;
                break;
            }
        }

        assertNotNull(selectedWifiApConfig);

        onData(allOf(is(instanceOf(WiFiApConfig.class)), hasEntry(equalTo("ssid"), is(selectedWifiApConfig.getSSID())))).perform(click());
    }
}