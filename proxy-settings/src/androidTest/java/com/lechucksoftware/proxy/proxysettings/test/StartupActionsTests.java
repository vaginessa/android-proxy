package com.lechucksoftware.proxy.proxysettings.test;

import android.test.ActivityInstrumentationTestCase2;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.robotium.solo.Solo;
import com.squareup.spoon.Spoon;
/**
 * Created by mpagliar on 18/12/2014.
 */
public class StartupActionsTests extends ActivityInstrumentationTestCase2<MasterActivity>
{
    private Solo solo;

    public StartupActionsTests()
    {
        super(MasterActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        TestUtils.resetPreferences(getInstrumentation().getContext());

        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testStartWhatsNewDialog() throws Exception
    {
        Spoon.screenshot(getActivity(), "init");

        assertTrue(solo.waitForActivity(MasterActivity.class));

        assertTrue(solo.waitForDialogToOpen());
        assertTrue(solo.waitForText(getActivity().getString(R.string.whatsnew)));
        assertTrue(solo.getButton(getActivity().getString(R.string.ok)).callOnClick());
        assertTrue(solo.waitForDialogToClose());

        Spoon.screenshot(getActivity(), "end");
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
