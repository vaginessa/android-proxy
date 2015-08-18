package com.lechucksoftware.proxy.proxysettings.ui.base;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import timber.log.Timber;

/**
 * Created by marco on 24/05/13.
 */
public class BaseDialogFragment extends DialogFragment
{
    /**
     *  Fragment life-cycle
     *
     *  onAttach()	The fragment instance is associated with an activity instance.The activity is not yet fully initialized
     *
     *  onCreate()	Fragment is created
     *
     *  onCreateView()	The fragment instance creates its view hierarchy. The inflated views become part of the view hierarchy of its containing activity.
     *
     *  onActivityCreated()	 Activity and fragment instance have been created as well as thier view hierarchy.
     *
     *  onResume()	 Fragment becomes visible and active.
     *
     *  onPause()	 Fragment is visibile but becomes not active anymore, e.g., if another activity is animating on top of the activity which contains the fragment.
     *
     *  onStop()	 Fragment becomes not visible.
     */

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public BaseDialogFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onResume");
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();

        Timber.tag(this.getClass().getSimpleName());
        Timber.d("onPause");
    }
}
