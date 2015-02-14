package com.lechucksoftware.proxy.proxysettings.test;

import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.robotium.solo.Solo;
import com.squareup.spoon.Spoon;

public class BasicAppTests extends ActivityInstrumentationTestCase2<MasterActivity>
{
    private Solo solo;

    public BasicAppTests()
    {
        super(MasterActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Smoke
    public void testCreateNewProxy() throws Exception
    {
        Spoon.screenshot(getActivity(), "init");

        assertTrue(solo.waitForActivity(MasterActivity.class));

        dismissAnyStartupDialog();

        openNavigationDrawer();

        assertTrue(solo.waitForText(getActivity().getString(R.string.static_proxies)));
        solo.clickOnText(getActivity().getString(R.string.static_proxies));

        assertTrue(solo.waitForText(getActivity().getString(R.string.static_proxies)));

        solo.clickOnActionBarItem(R.id.menu_add_new_proxy);

        assertTrue(solo.waitForText(getActivity().getString(R.string.create_new_proxy)));

        String proxyIp = TestUtils.getRandomIP();
        int proxyPort = TestUtils.getRandomPort();

        solo.enterText(0,proxyIp);
        solo.enterText(1,String.valueOf(proxyPort));

        assertTrue(solo.waitForView(R.id.menu_save));

        solo.clickOnActionBarItem(R.id.menu_save);

        Spoon.screenshot(getActivity(), "end");
    }

    @Smoke
    public void testEnableProxyForWifiNetwork()
    {
        Spoon.screenshot(getActivity(), "init");

        assertTrue(solo.waitForActivity(MasterActivity.class));

        dismissAnyStartupDialog();

        openNavigationDrawer();

        assertTrue(solo.waitForText(getActivity().getString(R.string.wifi_access_points)));
        solo.clickOnText(getActivity().getString(R.string.wifi_access_points));

        assertTrue(solo.waitForText(getActivity().getString(R.string.wifi_access_points)));

        solo.scrollListToTop(0);
        solo.clickInList(0);

        assertTrue(solo.waitForText(getActivity().getString(R.string.edit_wifi_ap)));

        Spoon.screenshot(getActivity(), "end");
    }

    /**
     * Open the navigation drawer with a drag gesture. Click based triggering is
     * flaky on SDK < 18
     */
    public void openNavigationDrawer() {

        Point deviceSize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(deviceSize);

        int screenWidth = deviceSize.x;
        int screenHeight = deviceSize.y;
        int fromX = 0;
        int toX = screenWidth / 2;
        int fromY = screenHeight / 2;
        int toY = fromY;

        solo.drag(fromX, toX, fromY, toY, 1);
    }

    private void dismissAnyStartupDialog()
    {
        if (solo.waitForDialogToOpen(1000))
        {
            if (solo.waitForText(getActivity().getString(R.string.ok)))
            {
                assertTrue(solo.getButton(getActivity().getString(R.string.ok)).callOnClick());
                assertTrue(solo.waitForDialogToClose());
            }
            else
            {
                solo.goBack();
            }
        }
    }

    @Override
    public void tearDown() throws Exception
    {
        try
        {
            solo.finishOpenedActivities();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        super.tearDown();
    }
}