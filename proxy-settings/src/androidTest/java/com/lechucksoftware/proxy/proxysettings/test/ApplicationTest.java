package com.lechucksoftware.proxy.proxysettings.test;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.lechucksoftware.proxy.proxysettings.App;

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
    }

    @SmallTest
    public void testPreconditions()
    {
    }

    @SmallTest
    public void testSimpleCreate()
    {
        createApplication();
    }

    @SmallTest
    public void testSimpleTerminate()
    {
        terminateApplication();
    }

}
