package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.ProxySelectorListAdapter;
import com.shouldit.proxy.lib.ProxyConfiguration;

import java.util.List;

/**
 * Created by marco on 17/05/13.
 */
public class AccessPointListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<ProxyConfiguration>>
{
    private static AccessPointListFragment instance;
    boolean mDualPane;
    int mCurCheckPosition = 0;
    private ProxySelectorListAdapter apListAdapter;

    public ListView mList;
    boolean mListShown;
    View mProgressContainer;
    View mListContainer;

    public void setListShown(boolean shown, boolean animate)
    {
        if (mListShown == shown)
        {
            return;
        }

        mListShown = shown;

        if (shown)
        {
            if (animate)
            {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        }
        else
        {
            if (animate)
            {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void setListShown(boolean shown)
    {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown)
    {
        setListShown(shown, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        int INTERNAL_EMPTY_ID = 0x00ff0001;
        View root = inflater.inflate(R.layout.ap_selector_fragment, container, false);
        (root.findViewById(R.id.internalEmpty)).setId(INTERNAL_EMPTY_ID);
        mList = (ListView) root.findViewById(android.R.id.list);
        mListContainer = root.findViewById(R.id.listContainer);
        mProgressContainer = root.findViewById(R.id.progressContainer);
        mListShown = true;
        return root;
    }

    public static AccessPointListFragment getInstance()
    {
        if (instance == null)
            instance = new AccessPointListFragment();

        return instance;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        // Initially there is no data
//        setEmptyText("No Data Here");

        // Create an empty adapter we will use to display the loaded data.
        apListAdapter = new ProxySelectorListAdapter(getActivity());
        setListAdapter(apListAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        View v = inflater.inflate(R.layout.ap_selector_fragment, null);
//        return v;
//    }

    @Override
    public Loader<List<ProxyConfiguration>> onCreateLoader(int arg0, Bundle arg1)
    {
        return new DataListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ProxyConfiguration>> arg0, List<ProxyConfiguration> data)
    {
        apListAdapter.setData(data);

        // The list should now be shown.
        if (isResumed())
        {
            setListShown(true);
        }
        else
        {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ProxyConfiguration>> arg0)
    {
        apListAdapter.setData(null);
    }

    public static class DataListLoader extends AsyncTaskLoader<List<ProxyConfiguration>>
    {
        List<ProxyConfiguration> mModels;

        public DataListLoader(Context context)
        {
            super(context);
        }

        @Override
        public List<ProxyConfiguration> loadInBackground()
        {
            // You should perform the heavy task of getting data from
            // Internet or database or other source
            // Here, we are generating some Sample data

            // Create corresponding array of entries and load with data.
            List<ProxyConfiguration> entries = ApplicationGlobals.getConfigurationsList();
            return entries;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override
        public void deliverResult(List<ProxyConfiguration> listOfData)
        {
            if (isReset())
            {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (listOfData != null)
                {
                    onReleaseResources(listOfData);
                }
            }
            List<ProxyConfiguration> oldApps = listOfData;
            mModels = listOfData;

            if (isStarted())
            {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(listOfData);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null)
            {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading()
        {
            if (mModels != null)
            {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mModels);
            }


            if (takeContentChanged() || mModels == null)
            {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading()
        {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override
        public void onCanceled(List<ProxyConfiguration> apps)
        {
            super.onCanceled(apps);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset()
        {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mModels != null)
            {
                onReleaseResources(mModels);
                mModels = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<ProxyConfiguration> apps)
        {
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        showDetails(position);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        refreshUI();

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

//        StatusFragment.getInstance().Hide();
    }

    public void refreshUI()
    {
        if (apListAdapter != null)
            apListAdapter.clear();

//        final ArrayList<ProxyConfiguration> confsList = (ArrayList<ProxyConfiguration>) ApplicationGlobals.getConfigurationsList();
//        apListAdapter = new ProxySelectorListAdapter(getActivity(), R.layout.ap_selector_fragment, confsList);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index)
    {
        mCurCheckPosition = index;

        // We can display everything in-place with fragments, so update
        // the list to highlight the selected item and show the data.
        getListView().setItemChecked(index, true);

        ProxyConfiguration selectedConfiguration = (ProxyConfiguration) getListView().getItemAtPosition(index);
        ApplicationGlobals.setSelectedConfiguration(selectedConfiguration);

        // Make new fragment to show this selection.
        ProxyDetailsFragment details = ProxyDetailsFragment.getInstance();

        // Execute a transaction, replacing any existing fragment
        // with this one inside the frame.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (index == 0)
        {
            ft.replace(R.id.fragment_container, details);
        }
        else
        {
            // TODO Check here
            ft.replace(R.id.fragment_container, details);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }
}
