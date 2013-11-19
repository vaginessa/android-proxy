package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.ActionManager;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.adapters.ProxiesSelectorListAdapter;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.lechucksoftware.proxy.proxysettings.utils.ProxyDBTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 17/05/13.
 */
public class ProxiesListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<DBProxy>>
{
    private static final String TAG = ProxiesListFragment.class.getSimpleName();
    private static ProxiesListFragment instance;
    int mCurCheckPosition = 0;
    private ProxiesSelectorListAdapter proxiesListAdapter;
    private TextView emptyText;
    private RelativeLayout progress;
    private static final int LOADER_PROXYDB = 1;
    private Loader<List<DBProxy>> loader;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.proxy_list, container, false);

        progress = (RelativeLayout) v.findViewById(R.id.progress);
        emptyText = (TextView) v.findViewById(android.R.id.empty);
        listView = (ListView) v.findViewById(android.R.id.list);

        // Reset selected configuration
        ApplicationGlobals.setSelectedConfiguration(null);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getActivity().getResources().getString(R.string.proxy_configurations));

        ActionManager.getInstance().hide();

        progress.setVisibility(View.VISIBLE);

        if (proxiesListAdapter == null)
        {
            proxiesListAdapter = new ProxiesSelectorListAdapter(getActivity());
        }

        listView.setAdapter(proxiesListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                showDetails(i);
            }
        });

        loader = getLoaderManager().initLoader(LOADER_PROXYDB, new Bundle(), this);
        loader.forceLoad();

        return v;
    }

    public static ProxiesListFragment getInstance()
    {
        if (instance == null)
            instance = new ProxiesListFragment();

        return instance;
    }

    @Override
    public Loader<List<DBProxy>> onCreateLoader(int i, Bundle bundle)
    {
        ProxyDBTaskLoader proxyDBTaskLoader = new ProxyDBTaskLoader(getActivity());
        return proxyDBTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<DBProxy>> listLoader, List<DBProxy> dbProxies)
    {
        if (dbProxies != null && dbProxies.size() > 0)
        {
            proxiesListAdapter.setData(dbProxies);
            emptyText.setVisibility(View.GONE);
        }
        else
        {
            proxiesListAdapter.setData(new ArrayList<DBProxy>());
            emptyText.setText(getResources().getString(R.string.proxy_empty_list));
            emptyText.setVisibility(View.VISIBLE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<DBProxy>> listLoader)
    {

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
            listView.setItemChecked(index, true);

            DBProxy selectedProxy = (DBProxy) listView.getItemAtPosition(index);
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
