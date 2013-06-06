package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.MainActivity;
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
        StatusFragment.getInstance().refreshUI();

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
        if (isAdded())
        {
            if (apListAdapter == null)
            {
                apListAdapter = new ProxySelectorListAdapter(getActivity());
                setListAdapter(apListAdapter);
            }

            if (ApplicationGlobals.getWifiManager().isWifiEnabled())
            {
                LogWrapper.d(TAG,"Refresh listview: get updated configuration list");
                List<ProxyConfiguration> results = ApplicationGlobals.getInstance().getConfigurationsList();
                if (results != null && results.size() > 0)
                {
//                    int duration = Toast.LENGTH_SHORT;
//                    Toast toast = Toast.makeText(getActivity(), "Proxy configurations received", duration);
//                    toast.show();

                    apListAdapter.setData(results);
                }
                else
                {
//                    int duration = Toast.LENGTH_SHORT;
//                    Toast toast = Toast.makeText(getActivity(), "No proxy configurations received", duration);
//                    toast.show();

                    // Wi-Fi is enabled, but no Wi-Fi access point configured
                    apListAdapter.setData(new ArrayList<ProxyConfiguration>());
                    emptyText.setText(getResources().getString(R.string.wifi_empty_list_no_ap));
                }
            }
            else
            {
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(getActivity(), "Wi-Fi is not enabled", duration);
//                toast.show();

                // Do not display results when Wi-Fi is not enabled
                apListAdapter.setData(new ArrayList<ProxyConfiguration>());
                emptyText.setText(getResources().getString(R.string.wifi_empty_list_wifi_off));
            }
        }
        else
        {
//            LogWrapper.d(TAG,"AccessPointListFragment is not added to activity");
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
//        LogWrapper.d(TAG,"Selected proxy configuration: " + selectedConfiguration.toShortString());

        MainActivity.GoToProxyDetailsFragment(getFragmentManager());
    }
}
