package com.lechucksoftware.proxy.proxysettings.test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.RequiresDevice;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.test.suitebuilder.annotation.Smoke;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.IntroActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.squareup.spoon.Spoon;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Map;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import timber.log.Timber;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@Smoke
public class BasicAppTests
{
    @Rule
    public ActivityTestRule<MasterActivity> mActivityRule = new ActivityTestRule<>(MasterActivity.class);

    private MasterActivity mActivity;

    private Activity currentActivity;

    @BeforeClass
    public static void setupClass() throws InterruptedException
    {
        Timber.d("Class setup");

        App.getAppStats().updateInstallationDetails();

        for (int i = 0; i <= 10; i++)
        {
            DevelopmentUtils.addRandomProxy();
            String ssid = DevelopmentUtils.createFakeWifiNetwork(InstrumentationRegistry.getContext());
            Timber.d("Created fake Wi-Fi ap: '%s'",ssid);

            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                Timber.e(e, "Exception during sleep");
            }
        }

        int i = 0;
        Map<APLNetworkId, WiFiApConfig> configurations = APL.getWifiAPConfigurations();
        if (configurations != null)
        {
            for (WiFiApConfig config: configurations.values())
            {
                if (TextUtils.isEmpty(config.getSSID()))
                {
                    Timber.d("Removing Wi-Fi AP with Empty SSID: %d",i++);
                    APL.getWifiManager().removeNetwork(config.getNetworkId());
                    Thread.sleep(100);
                }
            }
        }
    }

    @Before
    public void setUp()
    {
        Timber.d("Test setup");
        Timber.d("Launch count: '%d'", App.getAppStats().launchCount);

        mActivity = mActivityRule.getActivity();

        if (getActivityInstance() instanceof IntroActivity)
        {
            Timber.d("Closing IntroActivity");
            getActivityInstance().finish();
        }
    }

    @After
    public void tearDown()
    {
        mActivity.finish();

        Timber.d("Test teardown");
    }

    @AfterClass
    public static void tearDownClasS()
    {
        Timber.d("Class teardown");
    }

    @Test
    public void testCheckPreconditions()
    {
        assertThat(mActivity, notNullValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateNewStaticProxy()
    {
        Spoon.screenshot(getActivityInstance(), "init");

        openDrawer(R.id.drawer_layout);

        onView(withText(R.string.static_proxies)).perform(click());
        onView(withId(R.id.add_new_static_proxy)).perform(click());

        Spoon.screenshot(getActivityInstance(), "new");

        ProxyEntity staticProxy = DevelopmentUtils.createRandomHTTPProxy();
        onView(allOf(withId(R.id.field_value), isDescendantOfA(withId(R.id.proxy_host)))).perform(typeText(staticProxy.getHost()));
        onView(allOf(withId(R.id.field_value), isDescendantOfA(withId(R.id.proxy_port)))).perform(typeText(String.valueOf(staticProxy.getPort())));

        Spoon.screenshot(getActivityInstance(), "edit");

        onView(withId(R.id.menu_save)).perform(click());

        assertTrue(App.getDBManager().findProxy(staticProxy.getHost(), staticProxy.getPort(), "") != -1);

        Spoon.screenshot(getActivityInstance(), "save");
    }

    @SuppressWarnings("unchecked")
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    public void testCreateNewPACProxy()
    {
        openDrawer(R.id.drawer_layout);

        onView(withText(R.string.pac_proxies)).perform(click());
        onView(withId(R.id.add_new_pac_proxy)).perform(click());

        PacEntity pacProxy = DevelopmentUtils.createRandomPACProxy();

        onView(allOf(withId(R.id.field_value), isDescendantOfA(withId(R.id.pac_url)))).perform(typeText(String.valueOf(pacProxy.getPacUriFile())));

        onView(withId(R.id.menu_save)).perform(click());
    }

    @SuppressWarnings("unchecked")
    @Test
    @RequiresDevice
    public void testsEnableStaticProxyForWifiNetwork()
    {
        assertTrue(APL.getWifiManager().isWifiEnabled());

        openDrawer(R.id.drawer_layout);
        onView(withText(R.string.wifi_networks)).perform(click());

        Map<APLNetworkId, WiFiApConfig> configuredNetworks = APL.getWifiAPConfigurations();
        assertTrue(App.getDBManager().getProxiesCount() > 0);
        assertTrue(configuredNetworks.size() > 0);

        WiFiApConfig selectedWifiApConfig = null;
        ProxyEntity selectedProxyEntity = App.getDBManager().getRandomProxy();

        for (APLNetworkId networkId : configuredNetworks.keySet())
        {
            WiFiApConfig wifiApConfig = configuredNetworks.get(networkId);

            if ((wifiApConfig.getSecurityType() != SecurityType.SECURITY_EAP)
                    && wifiApConfig.getProxySetting() == ProxySetting.NONE)
            {
                selectedWifiApConfig = wifiApConfig;
                break;
            }
        }

        assertNotNull(selectedWifiApConfig);
        assertNotNull(selectedProxyEntity);

        // Enable proxy
        onData(hasToString(containsString(String.format("SSID: %s\n", selectedWifiApConfig.getSSID()))))
                .inAdapterView(withId(android.R.id.list))
                .perform(click());

        onView(withId(R.id.wifi_name)).check(matches(withText(selectedWifiApConfig.getSSID())));

        onView(withId(R.id.wifi_proxy_switch)).perform(click());
        onView(withId(R.id.proxy_selector)).perform(click());

        onData(hasToString(containsString(String.format("%s", selectedProxyEntity.getHost()))))
                .inAdapterView(allOf(withId(android.R.id.list), isDescendantOfA(withId(R.id.static_proxy_list))))
                .perform(click());

        onView(isRoot()).perform(ViewActions.pressBack());


        // Disable proxy
        onData(hasToString(containsString(String.format("SSID: %s\n", selectedWifiApConfig.getSSID()))))
                .inAdapterView(withId(android.R.id.list))
                .perform(click());

        onView(withId(R.id.wifi_name)).check(matches(withText(selectedWifiApConfig.getSSID())));
        onView(withId(R.id.wifi_proxy_switch)).perform(click());

        onView(isRoot()).perform(ViewActions.pressBack());

        ViewActions.pressKey(KeyEvent.KEYCODE_HOME);
    }

    @SuppressWarnings("unchecked")
    @Test
    @RequiresDevice
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.LOLLIPOP)
    public void testsEnablePACProxyForWifiNetwork()
    {
        assertTrue(APL.getWifiManager().isWifiEnabled());

        openDrawer(R.id.drawer_layout);
        onView(withText(R.string.wifi_networks)).perform(click());

        Map<APLNetworkId, WiFiApConfig> configuredNetworks = APL.getWifiAPConfigurations();

        assertTrue(App.getDBManager().getPacCount() > 0);
        assertTrue(configuredNetworks.size() > 0);

        WiFiApConfig selectedWifiApConfig = null;
        PacEntity selectedPacProxyEntity = App.getDBManager().getRandomPac();

        for (APLNetworkId networkId : configuredNetworks.keySet())
        {
            WiFiApConfig wifiApConfig = configuredNetworks.get(networkId);

            if ((wifiApConfig.getSecurityType() != SecurityType.SECURITY_EAP)
                    && wifiApConfig.getProxySetting() == ProxySetting.NONE)
            {
                selectedWifiApConfig = wifiApConfig;
                break;
            }
        }

        assertNotNull(selectedWifiApConfig);
        assertNotNull(selectedPacProxyEntity);

        onData(hasToString(containsString(String.format("SSID: %s\n", selectedWifiApConfig.getSSID()))))
                .inAdapterView(withId(android.R.id.list))
                .perform(click());

        onView(withId(R.id.wifi_name)).check(matches(withText(selectedWifiApConfig.getSSID())));

        onView(withId(R.id.wifi_proxy_switch)).perform(click());
        onView(withId(R.id.proxy_selector)).perform(click());

        onView(withText(R.string.pac_proxies)).perform(click());

        onData(hasToString(containsString(String.format("%s", selectedPacProxyEntity.getPacUriFile().toString()))))
                .inAdapterView(allOf(withId(android.R.id.list), isDescendantOfA(withId(R.id.pac_proxy_list))))
                .perform(click());

        onView(isRoot()).perform(ViewActions.pressBack());

        // Disable proxy
        onData(hasToString(containsString(String.format("SSID: %s\n", selectedWifiApConfig.getSSID()))))
                .inAdapterView(withId(android.R.id.list))
                .perform(click());

        onView(withId(R.id.wifi_name)).check(matches(withText(selectedWifiApConfig.getSSID())));
        onView(withId(R.id.wifi_proxy_switch)).perform(click());

        onView(isRoot()).perform(ViewActions.pressBack());
    }

    public Activity getActivityInstance()
    {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable()
        {
            public void run()
            {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext())
                {
                    currentActivity = (Activity) resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity;
    }

}