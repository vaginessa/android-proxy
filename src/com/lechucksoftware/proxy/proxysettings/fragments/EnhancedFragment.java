package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.Fragment;

/**
 * Created by marco on 24/05/13.
 */
public class EnhancedFragment extends Fragment
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
//        LogWrapper.d(this.getClass().getSimpleName() ,"onPause " + this.getClass().getSimpleName());
    }
}
