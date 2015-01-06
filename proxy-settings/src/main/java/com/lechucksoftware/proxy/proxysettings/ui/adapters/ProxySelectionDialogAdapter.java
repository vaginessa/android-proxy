package com.lechucksoftware.proxy.proxysettings.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Marco on 06/01/15.
 */
public class ProxySelectionDialogAdapter extends FragmentPagerAdapter
{
    public ProxySelectionDialogAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return null;
    }

    @Override
    public int getCount()
    {
        return 0;
    }
}
