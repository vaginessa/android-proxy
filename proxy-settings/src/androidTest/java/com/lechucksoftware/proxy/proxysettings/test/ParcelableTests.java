package com.lechucksoftware.proxy.proxysettings.test;

import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.Smoke;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.WiFiAPEntity;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupAction;
import com.lechucksoftware.proxy.proxysettings.utils.startup.StartupActions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.reflection.ReflectionUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@Smoke
public class ParcelableTests
{
    @Before
    public void setUp() throws Exception
    {
        APL.setup(InstrumentationRegistry.getContext());

        TestUtils.addRandomProxy();
    }

    @After
    public void tearDown () throws Exception
    {

    }

    @Test
    public void testParcelableWiFiApConfig()
    {
        Map<APLNetworkId, WiFiApConfig> configurations = APL.getWifiAPConfigurations();
        assertTrue(configurations.size() > 0);

        APLNetworkId networkId = (APLNetworkId) configurations.keySet().toArray()[0];
        WiFiApConfig wiFiApConfig = configurations.get(networkId);

        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        wiFiApConfig.writeToParcel(parcel, 0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        WiFiApConfig wifiApConfigFromParcel = WiFiApConfig.CREATOR.createFromParcel(parcel);

        assertFalse(wiFiApConfig == wifiApConfigFromParcel);
        assertTrue(wiFiApConfig.isSameConfiguration(wifiApConfigFromParcel));
    }

    @Test
    public void testParcelableAPLNetworkId() throws Exception
    {
        Map<APLNetworkId, WiFiApConfig> configurations = APL.getWifiAPConfigurations();
        assertTrue(configurations.size() > 0);

        APLNetworkId networkId = (APLNetworkId) configurations.keySet().toArray()[0];

        testParcelability(APLNetworkId.class, networkId);
    }

    @Test
    public void testParcelableWiFiAPEntity() throws Exception
    {
        WiFiAPEntity wiFiAPEntity = App.getDBManager().getRandomWifiAp();
        testParcelability(WiFiAPEntity.class, wiFiAPEntity);
    }

    @Test
    public void testParcelableProxyEntity() throws Exception
    {
        ProxyEntity proxyEntity = App.getDBManager().getRandomProxy();
        testParcelability(ProxyEntity.class, proxyEntity);
    }

    @Test
    public void testParcelablePacEntity() throws Exception
    {
        PacEntity pacEntity = App.getDBManager().getRandomPac();
        testParcelability(PacEntity.class, pacEntity);
    }

    @Test
    public void testParcelableStartupAction() throws Exception
    {
        List<StartupAction> availableActions = StartupActions.getAvailableActions();

        for (StartupAction action : availableActions)
        {
            testParcelability(StartupAction.class, action);
        }
    }

    public void testParcelability(Class cl, Object originalObject) throws Exception
    {
        assertNotNull(originalObject);

        Method writeToParcelMethod = ReflectionUtils.getMethod(cl.getMethods(),"writeToParcel");
        writeToParcelMethod.setAccessible(true);

        Class[] knownParam = new Class[1];
        knownParam[0] = Parcel.class;
        Constructor parcelConstructor = ReflectionUtils.getConstructor(cl.getDeclaredConstructors(), knownParam);
        parcelConstructor.setAccessible(true);

        Method equalsMethod = ReflectionUtils.getMethod(cl.getMethods(), "equals");
        equalsMethod.setAccessible(true);

        assertNotNull(writeToParcelMethod);
        assertNotNull(parcelConstructor);
        assertNotNull(equalsMethod);

        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        writeToParcelMethod.invoke(originalObject, parcel, 0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        Object objectFromParcel = parcelConstructor.newInstance(parcel);

        assertNotNull(objectFromParcel);
        assertFalse(originalObject == objectFromParcel);
        assertTrue((Boolean) equalsMethod.invoke(originalObject, objectFromParcel));
    }
}