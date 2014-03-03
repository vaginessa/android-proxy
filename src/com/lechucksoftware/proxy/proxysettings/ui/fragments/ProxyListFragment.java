package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.ProxiesSelectorListAdapter;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.FragmentMode;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.loaders.ProxyDBTaskLoader;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 17/05/13.
 */
public class ProxyListFragment extends BaseDialogFragment implements IBaseFragment, LoaderManager.LoaderCallbacks<List<ProxyEntity>>
{
    private static final String TAG = ProxyListFragment.class.getSimpleName();
//    private static ProxyListFragment instance;
    int mCurCheckPosition = 0;
    private ProxiesSelectorListAdapter proxiesListAdapter;
    private TextView emptyText;
    private RelativeLayout progress;

    private Loader<List<ProxyEntity>> loader;
    private ListView listView;

    private FragmentMode fragmentMode;

    // Loaders
    private static final int LOADER_PROXYDB = 1;

    // Arguments
    private static final String FRAGMENT_MODE_ARG = "FRAGMENT_MODE_ARG";
    private static final String PROXY_CONF_ARG = "PROXY_CONF_ARG";
    private ProxyConfiguration apConf;


    public static ProxyListFragment newInstance()
    {
        return newInstance(FragmentMode.FULLSIZE, null);
    }

    public static ProxyListFragment newInstance(FragmentMode mode, ProxyConfiguration apConf)
    {
        ProxyListFragment instance = new ProxyListFragment();

        Bundle args = new Bundle();
        args.putSerializable(FRAGMENT_MODE_ARG, mode);
        args.putSerializable(PROXY_CONF_ARG, apConf);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fragmentMode = (FragmentMode) getArguments().getSerializable(FRAGMENT_MODE_ARG);
        apConf = (ProxyConfiguration) getArguments().getSerializable(PROXY_CONF_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.standard_list, container, false);

        if (fragmentMode == FragmentMode.DIALOG)
        {
            getDialog().setTitle(R.string.select_proxy);
        }

        progress = (RelativeLayout) v.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        emptyText = (TextView) v.findViewById(android.R.id.empty);
        listView = (ListView) v.findViewById(android.R.id.list);

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
                if (fragmentMode == FragmentMode.FULLSIZE)
                {
                    showDetails(i);
                }
                else if (fragmentMode == FragmentMode.DIALOG)
                {
                    selectProxy(i);
                    dismiss();
                }
            }
        });

        loader = getLoaderManager().initLoader(LOADER_PROXYDB, new Bundle(), this);
        loader.forceLoad();

//        // Reset selected configuration
//        ApplicationGlobals.setSelectedConfiguration(null);

        return v;
    }

    /**
     * LoaderManager Interface methods
     * */

    @Override
    public Loader<List<ProxyEntity>> onCreateLoader(int i, Bundle bundle)
    {
        ProxyDBTaskLoader proxyDBTaskLoader = new ProxyDBTaskLoader(getActivity());
        return proxyDBTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<ProxyEntity>> listLoader, List<ProxyEntity> dbProxies)
    {
        if (dbProxies != null && dbProxies.size() > 0)
        {
            proxiesListAdapter.setData(dbProxies);
            emptyText.setVisibility(View.GONE);

//            if (dbProxies.size() > 10)
//            {
//                listView.setFastScrollEnabled(true);
//                listView.setFastScrollAlwaysVisible(true);
//                listView.setSmoothScrollbarEnabled(true);
//            }
//            else
//            {
//                listView.setFastScrollEnabled(false);
//                listView.setFastScrollAlwaysVisible(false);
//                listView.setSmoothScrollbarEnabled(false);
//            }
        }
        else
        {
            proxiesListAdapter.setData(new ArrayList<ProxyEntity>());
            emptyText.setText(getResources().getString(R.string.proxy_empty_list));
            emptyText.setVisibility(View.VISIBLE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<ProxyEntity>> listLoader)
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

            ProxyEntity selectedProxy = (ProxyEntity) listView.getItemAtPosition(index);
            LogWrapper.d(TAG, "Selected proxy configuration: " + selectedProxy.toString());

            Intent i = new Intent(getActivity(), ProxyDetailActivity.class);
            ApplicationGlobals.getCacheManager().put(selectedProxy.getUUID(), selectedProxy);
            i.putExtra(Constants.SELECTED_PROXY_CONF_ARG, selectedProxy.getUUID());
            startActivity(i);
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(new Exception("Exception during WiFiApListFragment showDetails(" + index + ") " + e.toString()));
        }
    }

    void selectProxy(int index)
    {
        mCurCheckPosition = index;

        try
        {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            listView.setItemChecked(index, true);
            ProxyEntity proxy = (ProxyEntity) listView.getItemAtPosition(index);

            if (apConf.getProxySettings() == ProxySetting.STATIC)
            {
                long proxyId = ApplicationGlobals.getDBManager().findProxy(apConf.getProxyHost(), apConf.getProxyPort());
                if (proxyId != -1)
                {
                    ApplicationGlobals.getDBManager().clearInUseFlag(proxyId);
                }
            }

            apConf.setProxySetting(ProxySetting.STATIC);
            apConf.setProxyHost(proxy.host);
            apConf.setProxyPort(proxy.port);
            apConf.setProxyExclusionList(proxy.exclusion);
            apConf.writeConfigurationToDevice();

            proxy.setInUse(true);
            ApplicationGlobals.getDBManager().upsertProxy(proxy);
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(new Exception("Exception during WiFiApListFragment selectProxy(" + index + ") " + e.toString()));
        }
    }

    public void refreshUI()
    {
        if (proxiesListAdapter != null)
            proxiesListAdapter.notifyDataSetChanged();

        if (loader != null)
            loader.forceLoad();
    }
}
