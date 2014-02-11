package com.lechucksoftware.proxy.proxysettings.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.*;

/**
 * Created by Marco on 22/06/13.
 */
public class NavigationUtils
{
    public static void GoToAccessPointListFragment(FragmentManager fm)
    {
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // Clean-up the backstack when going back to home

        FragmentTransaction ft = fm.beginTransaction();

        Fragment f = fm.findFragmentById(R.id.fragment_container);

        if (f != null)
        {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.replace(R.id.fragment_container, WiFiApListFragment.getInstance());
        }
        else
        {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.add(R.id.fragment_container, WiFiApListFragment.getInstance());
        }

        // Do NOT add WiFiApListFragment to back stack
        ft.commit();
    }

    public static void GoToHelpFragment(FragmentManager fm)
    {
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        ft.replace(R.id.fragment_container, HelpPrefsFragment.getInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

//    public static void GoToAPDetailsFragment(FragmentManager fm, ProxyConfiguration proxyConfiguration)
//    {
//        WiFiApDetailFragment details = WiFiApDetailFragment.newInstance(proxyConfiguration);
//
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.replace(R.id.fragment_container, details);
//        ft.addToBackStack(null);
//        ft.commit();
//    }

//    public static void GoToProxyDetailsFragment(FragmentManager fm, ProxyEntity selectedProxy)
//    {
//        ProxyDetailFragment details = ProxyDetailFragment.newInstance(selectedProxy);
//
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.replace(R.id.fragment_container, details);
//        ft.addToBackStack(null);
//        ft.commit();
//    }

    public static void GoToProxiesList(FragmentManager fm)
    {
        ProxyListFragment list = ProxyListFragment.newInstance();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_container, list);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static void GoToAppFeedbacks(FragmentManager fm)
    {
        ApplicationsFeedbackFragment list = ApplicationsFeedbackFragment.getInstance();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_container, list);
        ft.addToBackStack(null);
        ft.commit();
    }
}
