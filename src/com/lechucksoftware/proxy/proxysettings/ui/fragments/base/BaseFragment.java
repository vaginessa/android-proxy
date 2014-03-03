package com.lechucksoftware.proxy.proxysettings.ui.fragments.base;

import android.app.Fragment;
import com.shouldit.proxy.lib.log.LogWrapper;

/**
 * Created by marco on 24/05/13.
 */
public class BaseFragment extends Fragment
{
    @Override
    public void onResume()
    {
        super.onResume();
        LogWrapper.d(this.getClass().getSimpleName(), "onResume " + this.getClass().getSimpleName());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LogWrapper.d(this.getClass().getSimpleName() ,"onPause " + this.getClass().getSimpleName());
    }
}
