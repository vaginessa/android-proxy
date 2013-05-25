package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.ProxySelectorListAdapter;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 17/05/13.
 */
public class AccessPointListFragment extends EnhancedListFragment
{
    private static final String TAG = "AccessPointListFragment";
    private static AccessPointListFragment instance;
    int mCurCheckPosition = 0;
    private ProxySelectorListAdapter apListAdapter;
    private TextView emptyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.ap_selector_fragment, container, false);

        emptyText = (TextView) v.findViewById(android.R.id.empty);

        return v;
    }

    public static AccessPointListFragment getInstance()
    {
        if (instance == null)
            instance = new AccessPointListFragment();

        return instance;
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

        // Reset selected configuration
        ApplicationGlobals.setSelectedConfiguration(null);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(getResources().getString(R.string.app_name));

        refreshUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    public void refreshUI()
    {
        if (isVisible())
        {
            if (apListAdapter == null)
            {
                apListAdapter = new ProxySelectorListAdapter(getActivity());
                setListAdapter(apListAdapter);
            }

            if (ApplicationGlobals.getWifiManager().isWifiEnabled())
            {
                List<ProxyConfiguration> results = ApplicationGlobals.getInstance().getConfigurationsList();
                if (results.size() > 0)
                {
                    apListAdapter.setData(results);
                }
                else
                {
                    // Wi-Fi is enabled, but no Wi-Fi access point configured
                    apListAdapter.setData(new ArrayList<ProxyConfiguration>());
                    emptyText.setText(getResources().getString(R.string.wifi_empty_list_no_ap));
                }
            }
            else
            {
                // Do not display results when Wi-Fi is not enabled
                apListAdapter.setData(new ArrayList<ProxyConfiguration>());
                emptyText.setText(getResources().getString(R.string.wifi_empty_list_wifi_off));
            }
        }
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
