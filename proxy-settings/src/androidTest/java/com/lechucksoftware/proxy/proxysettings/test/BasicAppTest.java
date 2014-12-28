package com.lechucksoftware.proxy.proxysettings.test;

import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputField;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.NavDrawFragment;
import com.robotium.solo.Condition;
import com.robotium.solo.RobotiumUtils;
import com.robotium.solo.Solo;
import com.squareup.spoon.Spoon;

public class BasicAppTest extends ActivityInstrumentationTestCase2<MasterActivity>
{
    private Solo solo;

    public BasicAppTest()
    {
        super(MasterActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testCreateNewProxy() throws Exception
    {
        Spoon.screenshot(getActivity(), "init");

        assertTrue(solo.waitForActivity(MasterActivity.class));

        dismissAnyStartupDialog();

        openNavigationDrawer();

        assertTrue(solo.waitForText(getActivity().getString(R.string.proxies_list)));
        solo.clickOnText(getActivity().getString(R.string.proxies_list));

        assertTrue(solo.waitForText(getActivity().getString(R.string.proxies_list)));

        solo.clickOnActionBarItem(R.id.menu_add_new_proxy);

        assertTrue(solo.waitForText(getActivity().getString(R.string.create_new_proxy)));

//        final InputField hostEdit = (InputField) solo.getView(R.id.proxy_host);
//        final InputField hostPort = (InputField) solo.getView(R.id.proxy_port);

//        EditText hostEditText = RobotiumUtils.filterViews(EditText.class, solo.getViews(hostEdit)).get(0);
//        EditText portEditText = RobotiumUtils.filterViews(EditText.class, solo.getViews(hostPort)).get(0);

        String proxyIp = TestUtils.getRandomIP();
        int proxyPort = TestUtils.getRandomPort();

        solo.enterText(0,proxyIp);
        solo.enterText(1,String.valueOf(proxyPort));

        solo.waitForView(R.id.menu_save);

        solo.clickOnActionBarItem(R.id.menu_save);

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

//    private void openNavigationDrawer()
//    {
//
////      NOT WORKING AT THE MOMENT:
////        solo.setNavigationDrawer(Solo.OPENED);
//
//        assertTrue(solo.waitForFragmentById(R.id.navigation_drawer));
//        NavDrawFragment navDrawerFragment = (NavDrawFragment) getActivity().getFragmentManager().findFragmentById(R.id.navigation_drawer);
//
//        if (!navDrawerFragment.isDrawerOpen())
//        {
//            navDrawerFragment.forceOpenDrawer();
//        }
//
//        try
//        {
//            Thread.sleep(1000);
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//    }

    private void dismissAnyStartupDialog()
    {
        if (solo.waitForDialogToOpen(1000))
        {
            assertTrue(solo.getButton(getActivity().getString(R.string.ok)).callOnClick());
            assertTrue(solo.waitForDialogToClose());
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