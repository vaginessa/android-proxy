package com.lechucksoftware.proxy.proxysettings.test;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;

/**
* Created by mpagliar on 22/08/2014.
*/

public class EspressoTests extends ActivityInstrumentationTestCase2<MasterActivity>
{
    public EspressoTests()
    {
        super(MasterActivity.class);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        getActivity();
    }

    public void testEspressoClickingListViewPopulatesTextView()
    {
        //Given the ListView is populated
        Espresso.onView(ViewMatchers.withText(R.string.wifi_access_points)).perform(ViewActions.click());

//        // When I click on the an item in the ListView
//        Espresso.onData(Matchers.allOf(Matchers.is(
//                Matchers.instanceOf(String.class)), Matchers.is("Grape")))
//                .perform(ViewActions.click());
//
//        // Then the TextView shows the correct text
//        Espresso.onView(ViewMatchers.withId(R.id.my_hello_text_view))
//                .check(ViewAssertions.matches(ViewMatchers.withText(Matchers.containsString("Grape"))));
    }
}