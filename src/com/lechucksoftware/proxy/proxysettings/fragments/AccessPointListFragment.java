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
public class AccessPointListFragment extends ListFragment {
    private static AccessPointListFragment instance;
    int mCurCheckPosition = 0;
    private ProxySelectorListAdapter apListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ap_selector_fragment, container, false);
    }

    public static AccessPointListFragment getInstance() {
        if (instance == null)
            instance = new AccessPointListFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apListAdapter = new ProxySelectorListAdapter(getActivity());
        setListAdapter(apListAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshUI();

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

//        StatusFragment.getInstance().Hide();
    }

    public void refreshUI() {
        if (apListAdapter != null) {
            apListAdapter.setData(ApplicationGlobals.getConfigurationsList());
        }
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
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
        if (index == 0) {
            ft.replace(R.id.fragment_container, details);
        } else {
            // TODO Check here
            ft.replace(R.id.fragment_container, details);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }
}
