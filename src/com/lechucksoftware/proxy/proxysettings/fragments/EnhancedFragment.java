package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.Fragment;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

/**
 * Created by marco on 24/05/13.
 */
public class EnhancedFragment extends Fragment
{
    private static final String TAG = "EnhancedFragment";

    @Override
    public void onResume()
    {
        super.onResume();
        LogWrapper.d(TAG, "onResume " + this.getClass().getName());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LogWrapper.d(TAG,"onPause " + this.getClass().getName());
    }
}
