package com.lechucksoftware.proxy.proxysettings.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.AccessPointListFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyDetailsFragment;

/**
 * Created by Marco on 22/06/13.
 */
public class NavigationUtils
{
    public static void GoToAccessPointListFragment(FragmentManager fm)
    {
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // Clean-up the backstack when going back to home

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        Fragment f = fm.findFragmentById(R.id.fragment_container);

        if (f != null)
        {
            ft.replace(R.id.fragment_container, AccessPointListFragment.getInstance());
        }
        else
        {
            ft.add(R.id.fragment_container, AccessPointListFragment.getInstance());
        }

        // Do NOT add AccessPointListFragment to back stack
        ft.commit();
    }

    public static void GoToHelpFragment(FragmentManager fm)
    {
        HelpPrefsFragment help = HelpPrefsFragment.getInstance();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.replace(R.id.fragment_container, help);

        ft.addToBackStack(null);
        ft.commit();
    }

    public static void GoToProxyDetailsFragment(FragmentManager fm)
    {
        ProxyDetailsFragment details = ProxyDetailsFragment.getInstance();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.replace(R.id.fragment_container, details);

        ft.addToBackStack(null);
        ft.commit();
    }
}
