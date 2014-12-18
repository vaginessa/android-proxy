package com.lechucksoftware.proxy.proxysettings.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
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

        assertTrue(solo.waitForText(getActivity().getString(R.string.proxies_list)));
        assertTrue(solo.getView(getActivity().getString(R.string.proxies_list)).callOnClick());

        assertTrue(solo.waitForText(getActivity().getString(R.string.proxies_list)));
        assertTrue(solo.getButton(R.id.menu_add_new_proxy).callOnClick());
        assertTrue(solo.waitForText(getActivity().getString(R.string.create_new_proxy)));

        EditText hostEdit = solo.getEditText(R.id.proxy_host);
        EditText hostPort = solo.getEditText(R.id.proxy_port);
        hostEdit.setText("10.11.12.13");
        hostPort.setText("1234");

        assertTrue(solo.getButton(R.id.menu_save).callOnClick());

        Spoon.screenshot(getActivity(), "end");
    }

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