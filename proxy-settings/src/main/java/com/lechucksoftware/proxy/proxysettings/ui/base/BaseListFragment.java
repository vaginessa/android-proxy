package com.lechucksoftware.proxy.proxysettings.ui.base;

import android.app.ListFragment;

/**
 * Created by marco on 24/05/13.
 */
public class BaseListFragment extends ListFragment
{
    @Override
    public void onResume()
    {
        super.onResume();
//        LogWrapper.d(this.getClass().getSimpleName(), "onResume " + this.getClass().getSimpleName());
    }

    @Override
    public void onPause()
    {
        super.onPause();
//        LogWrapper.d(this.getClass().getSimpleName(),"onPause " + this.getClass().getSimpleName());
    }
}
