package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import com.lechucksoftware.proxy.proxysettings.R;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.utils.ProxySelectorListAdapter;
import com.shouldit.proxy.lib.ProxyConfiguration;

import java.util.ArrayList;

/**
 * Created by marco on 17/05/13.
 */
public class AccessPointListFragment extends ListFragment
{
    private static AccessPointListFragment instance;
    boolean mDualPane;
    int mCurCheckPosition = 0;
    private ProxySelectorListAdapter apListAdapter;


    public static AccessPointListFragment getInstance()
    {
        if (instance == null)
            instance = new AccessPointListFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
        {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
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
    }

    public void refreshUI()
    {
        if (apListAdapter != null)
            apListAdapter.clear();

        final ArrayList<ProxyConfiguration> confsList = (ArrayList<ProxyConfiguration>) ApplicationGlobals.getConfigurationsList();
        apListAdapter = new ProxySelectorListAdapter(getActivity(), android.R.id.list, confsList);
        setListAdapter(apListAdapter);
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

        // Check what fragment is currently shown, replace if needed.
        ProxyDetailsFragment details = (ProxyDetailsFragment) getFragmentManager().findFragmentById(R.id.details);
        if (details == null)
        {
            // Make new fragment to show this selection.
            details = ProxyDetailsFragment.getInstance();

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
}
