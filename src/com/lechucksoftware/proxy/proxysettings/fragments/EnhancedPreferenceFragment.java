package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.Fragment;
import android.preference.PreferenceFragment;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

/**
 * Created by marco on 24/05/13.
 */
public class EnhancedPreferenceFragment extends PreferenceFragment
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
