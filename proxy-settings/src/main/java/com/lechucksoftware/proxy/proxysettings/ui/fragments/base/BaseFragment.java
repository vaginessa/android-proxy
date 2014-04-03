package com.lechucksoftware.proxy.proxysettings.ui.fragments.base;

import android.app.Fragment;
import android.view.View;

import io.should.proxy.lib.log.LogWrapper;

/**
 * Created by marco on 24/05/13.
 */
public class BaseFragment extends Fragment
{
    public View progress;
    public View content;

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

    @Override
    public void onResume()
    {
        super.onResume();
        App.getLogger().d(this.getClass().getSimpleName(), "onResume " + this.getClass().getSimpleName());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        App.getLogger().d(this.getClass().getSimpleName(), "onPause " + this.getClass().getSimpleName());
    }
}
