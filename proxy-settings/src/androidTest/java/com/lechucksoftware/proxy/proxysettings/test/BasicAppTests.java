package com.lechucksoftware.proxy.proxysettings.test;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

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
    public void createNewProxy()
    {
        openDrawer(R.id.drawer_layout);

        onView(withText(R.string.static_proxies)).perform(click());
        onView(withId(R.id.add_new_proxy)).perform(click());

//        onView(withId(R.id.action_save))
//                .perform(click());
//
//        onView(withId(R.id.text_action_bar_result))
//                .check(matches(withText("Save")));
//
//        assertTrue(solo.waitForActivity(MasterActivity.class));
//
//        dismissAnyStartupDialog();
//
//        openNavigationDrawer();
//
//        assertTrue(solo.waitForText(getActivity().getString(R.string.static_proxies)));
//        solo.clickOnText(getActivity().getString(R.string.static_proxies));
//
//        assertTrue(solo.waitForText(getActivity().getString(R.string.static_proxies)));
//
//        solo.clickOnActionBarItem(R.id.menu_add_new_proxy);
//
//        assertTrue(solo.waitForText(getActivity().getString(R.string.create_new_proxy)));
//
//        String proxyIp = TestUtils.getRandomIP();
//        int proxyPort = TestUtils.getRandomPort();
//
//        solo.enterText(0,proxyIp);
//        solo.enterText(1,String.valueOf(proxyPort));
//
//        assertTrue(solo.waitForView(R.id.menu_save));
//
//        solo.clickOnActionBarItem(R.id.menu_save);
//
    }

    @Test
    public void enableProxyForWifiNetwork()
    {
//
//        assertTrue(solo.waitForActivity(MasterActivity.class));
//
//        dismissAnyStartupDialog();
//
//        openNavigationDrawer();
//
//        assertTrue(solo.waitForText(getActivity().getString(R.string.wifi_access_points)));
//        solo.clickOnText(getActivity().getString(R.string.wifi_access_points));
//
//        assertTrue(solo.waitForText(getActivity().getString(R.string.wifi_access_points)));
//
//        solo.scrollListToTop(0);
//        solo.clickInList(0);
//
//        assertTrue(solo.waitForText(getActivity().getString(R.string.edit_wifi_ap)));
//
    }

    /**
     * Open the navigation drawer with a drag gesture. Click based triggering is
     * flaky on SDK < 18
     */
    public void openNavigationDrawer()
    {

//        Point deviceSize = new Point();
//        getActivity().getWindowManager().getDefaultDisplay().getSize(deviceSize);
//
//        int screenWidth = deviceSize.x;
//        int screenHeight = deviceSize.y;
//        int fromX = 0;
//        int toX = screenWidth / 2;
//        int fromY = screenHeight / 2;
//        int toY = fromY;
//
//        solo.drag(fromX, toX, fromY, toY, 1);
    }

    private void dismissAnyStartupDialog()
    {
//        if (solo.waitForDialogToOpen(1000))
//        {
//            if (solo.waitForText(getActivity().getString(R.string.ok)))
//            {
//                assertTrue(solo.getButton(getActivity().getString(R.string.ok)).callOnClick());
//                assertTrue(solo.waitForDialogToClose());
//            }
//            else
//            {
//                solo.goBack();
//            }
//        }
    }
}