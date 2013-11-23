package com.lechucksoftware.proxy.proxysettings.fragments.base;

import android.app.DialogFragment;

/**
 * Created by marco on 24/05/13.
 */
public class BaseDialogFragment extends DialogFragment
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
