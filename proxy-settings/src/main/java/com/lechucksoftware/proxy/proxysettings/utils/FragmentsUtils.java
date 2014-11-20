package com.lechucksoftware.proxy.proxysettings.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;

/**
 * Created by Marco on 22/06/13.
 */
public class FragmentsUtils
{
    private static final String TAG = FragmentsUtils.class.getSimpleName();

    public static void goToMainActivity(Context context)
    {
        Intent mainIntent = new Intent(context, MasterActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);
    }

    public static void changeFragment(FragmentManager fragmentManager, int frameId, Fragment fragment, boolean addToBackStack)
    {
        String newFragmentName = fragment.getClass().getName();
        String currentFragmentName = "";

        try
        {
            Fragment currentFragment = fragmentManager.findFragmentById(frameId);

            if (currentFragment != null)
            {
                currentFragmentName = currentFragment.getClass().getName();
                App.getLogger().d(TAG, String.format("changeFragment from '%s' to '%s' ", currentFragmentName, newFragmentName));

                if (currentFragmentName.equals(newFragmentName))
                {
                    // No need to do anything the fragment just return
                    // TODO: Evaluate if at least a refresh of the current fragment can be executed
                    return;
                }
            }
            else
            {
                App.getLogger().d(TAG, String.format("changeFragment add '%s' ", newFragmentName));
            }

            boolean fragmentPopped = fragmentManager.popBackStackImmediate(newFragmentName, 0);
            App.getLogger().d(TAG, "fragmentPopped: " + fragmentPopped);

            if (!fragmentPopped)
            {
                //Fragment not in back stack, create it.
                FragmentTransaction ft = fragmentManager.beginTransaction();

//                TODO: Add animation to the transaction
//                ft.setCustomAnimations(enter, exit, pop_enter, pop_exit);

                if (currentFragment == null)
                {
                    ft.add(frameId, fragment);
                }
                else
                {
                    ft.replace(frameId, fragment);
                }

                if (addToBackStack)
                {
                    App.getLogger().d(TAG, "fragment added to back stack");
                    ft.addToBackStack(newFragmentName);
                }

                ft.commit();
            }
        }
        catch (IllegalStateException e)
        {
            App.getEventsReporter().sendException(new Exception("Unable to commit fragment, could be activity as been killed in background. ", e));
        }
    }
}
