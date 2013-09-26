package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.ActionManager;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyData;
import com.lechucksoftware.proxy.proxysettings.db.ProxyDataSource;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.lechucksoftware.proxy.proxysettings.utils.ProxiesSelectorListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 17/05/13.
 */
public class ProxiesListFragment extends EnhancedListFragment
{
    private static final String TAG = ProxiesListFragment.class.getSimpleName();
    private static ProxiesListFragment instance;
    int mCurCheckPosition = 0;
    private ProxiesSelectorListAdapter proxiesListAdapter;
    private TextView emptyText;
    private ProxyDataSource datasource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.base_list_fragment, container, false);
        emptyText = (TextView) v.findViewById(android.R.id.empty);
        return v;
    }

    public static ProxiesListFragment getInstance()
    {
        if (instance == null)
            instance = new ProxiesListFragment();

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
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getActivity().getResources().getString(R.string.proxy_configurations));

        ActionManager.getInstance().hide();

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
            if (proxiesListAdapter == null)
            {
                proxiesListAdapter = new ProxiesSelectorListAdapter(getActivity());
                setListAdapter(proxiesListAdapter);
            }

            LogWrapper.d(TAG, "Refresh listview UI: get configuration list");
            datasource = new ProxyDataSource(getActivity());
            datasource.openReadable();

            List<ProxyData> results = datasource.getAllProxies();
            if (results != null && results.size() > 0)
            {
                proxiesListAdapter.setData(results);
            }
            else
            {
                proxiesListAdapter.setData(new ArrayList<ProxyData>());
                emptyText.setText(getResources().getString(R.string.wifi_empty_list_no_ap));
            }

            datasource.close();
        }
        else
        {
//            LogWrapper.d(TAG,"ProxiesListFragment is not added to activity");
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

        try
        {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            ProxyData selectedProxy = (ProxyData) getListView().getItemAtPosition(index);
            ApplicationGlobals.setSelectedProxy(selectedProxy);
//            LogWrapper.d(TAG, "Selected proxy configuration: " + selectedConfiguration.toShortString());
            NavigationUtils.GoToProxyDetailsFragment(getFragmentManager());
        }
        catch (Exception e)
        {
            BugReportingUtils.sendException(new Exception("Exception during AccessPointListFragment showDetails(" + index + ") " + e.toString()));
        }
    }
}
