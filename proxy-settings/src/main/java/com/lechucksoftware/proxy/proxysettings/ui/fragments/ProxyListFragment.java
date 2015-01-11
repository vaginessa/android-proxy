package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.FragmentMode;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.loaders.ProxyDBTaskLoader;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncSaveWiFiApConfig;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.ProxiesListAdapter;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.ui.base.IBaseFragment;

import java.util.ArrayList;
import java.util.List;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

/**
 * Created by marco on 17/05/13.
 */
public class ProxyListFragment extends BaseDialogFragment implements IBaseFragment, LoaderManager.LoaderCallbacks<List<ProxyEntity>>
{
    private static final String TAG = ProxyListFragment.class.getSimpleName();
//    private static ProxyListFragment instance;
    int mCurCheckPosition = 0;
    private ProxiesListAdapter proxiesListAdapter;

    private Loader<List<ProxyEntity>> loader;

    @InjectView(R.id.progress) RelativeLayout progress;
    @InjectView(R.id.empty_message_section) RelativeLayout emptySection;

    @InjectView(android.R.id.empty) TextView emptyText;
    @InjectView(android.R.id.list) ListView listView;

    @Optional @InjectView(R.id.proxy_footer_textview) TextView footerTextView; // Footer not displayed into dialog
    @Optional @InjectView(R.id.dialog_cancel) Button cancelDialogButton; // Cancel not displayed into full fragment

    private FragmentMode fragmentMode;

    // Loaders
    private static final int LOADER_PROXYDB = 1;

    private WiFiAPConfig wiFiAPConfig;
    private APLNetworkId aplNetworkId;

    public static ProxyListFragment newInstance(int sectionNumber)
    {
        return newInstance(sectionNumber, FragmentMode.FULLSIZE, null);
    }

    public static ProxyListFragment newInstance(int sectionNumber, FragmentMode mode, APLNetworkId aplNetworkId)
    {
        ProxyListFragment fragment = new ProxyListFragment();

        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(Constants.FRAGMENT_MODE_ARG, mode);
        args.putSerializable(Constants.WIFI_AP_NETWORK_ARG, aplNetworkId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null)
        {
            if (args.containsKey(Constants.FRAGMENT_MODE_ARG))
            {
                fragmentMode = (FragmentMode) getArguments().getSerializable(Constants.FRAGMENT_MODE_ARG);
            }

            if (args.containsKey(Constants.WIFI_AP_NETWORK_ARG))
            {
                aplNetworkId = (APLNetworkId) getArguments().getSerializable(Constants.WIFI_AP_NETWORK_ARG);

                if (aplNetworkId != null)
                {
                    wiFiAPConfig = App.getWifiNetworksManager().getConfiguration(aplNetworkId);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v;

        setHasOptionsMenu(true);

//        if (fragmentMode == FragmentMode.DIALOG)
//        {
////            getDialog().setTitle(R.string.select_proxy);
//            v = inflater.inflate(R.layout.proxy_list_fragment, container, false);
//
//            ButterKnife.inject(this, v);
//        }
//        else
//        {
            v = inflater.inflate(R.layout.proxy_list_fragment, container, false);

            ButterKnife.inject(this, v);
//        }

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        progress.setVisibility(View.VISIBLE);

        if (proxiesListAdapter == null)
        {
            proxiesListAdapter = new ProxiesListAdapter(getActivity());
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
                }

            }
        });

        footerTextView.setVisibility(View.GONE);

        loader = getLoaderManager().initLoader(LOADER_PROXYDB, new Bundle(), this);
        loader.forceLoad();
    }

    public void refreshUI()
    {
        if (loader != null)
        {
            loader.forceLoad();
        }
    }

    /**
     * LoaderManager Interface methods
     * */

    @Override
    public Loader<List<ProxyEntity>> onCreateLoader(int i, Bundle bundle)
    {
        App.getTraceUtils().startTrace(TAG, "onCreateLoader", Log.DEBUG);

        ProxyDBTaskLoader proxyDBTaskLoader = new ProxyDBTaskLoader(getActivity());

        App.getTraceUtils().stopTrace(TAG, "onCreateLoader", Log.DEBUG);

        return proxyDBTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<ProxyEntity>> listLoader, List<ProxyEntity> dbProxies)
    {
        App.getTraceUtils().startTrace(TAG, "onLoadFinished", Log.DEBUG);

        refreshLoaderResults(dbProxies);

        App.getTraceUtils().stopTrace(TAG, "onLoadFinished", Log.DEBUG);
        App.getTraceUtils().stopTrace(TAG, "STARTUP", Log.ERROR);
    }

    private void refreshLoaderResults(List<ProxyEntity> dbProxies)
    {
        if (dbProxies != null && dbProxies.size() > 0)
        {
            proxiesListAdapter.setData(dbProxies);

            emptySection.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);

            footerTextView.setVisibility(View.VISIBLE);
            footerTextView.setText(getString(R.string.num_proxies_configured, dbProxies.size()));
        }
        else
        {
            proxiesListAdapter.setData(new ArrayList<ProxyEntity>());

            emptySection.setVisibility(View.VISIBLE);
            emptyText.setText(getResources().getString(R.string.proxy_empty_list));
            emptyText.setVisibility(View.VISIBLE);

            footerTextView.setVisibility(View.GONE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<ProxyEntity>> listLoader)
    {
        Timber.d("onLoaderReset");
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
            Timber.d("Selected proxy configuration: " + selectedProxy.toString());

            Intent i = new Intent(getActivity(), ProxyDetailActivity.class);
//            App.getCacheManager().put(selectedProxy.getUUID(), selectedProxy);
            i.putExtra(Constants.SELECTED_PROXY_CONF_ARG, selectedProxy.getId());
            startActivity(i);
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during WiFiApListFragment showDetails(%d)", index);
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

            Intent i = new Intent();
            i.putExtra(Constants.SELECTED_PROXY_TYPE_ARG, ProxySetting.STATIC);
            i.putExtra(Constants.SELECTED_PROXY_CONF_ARG, proxy);
            getActivity().setResult(FragmentActivity.RESULT_OK, i);

//            wiFiAPConfig.setProxySetting(ProxySetting.STATIC);
//            wiFiAPConfig.setProxyHost(proxy.getHost());
//            wiFiAPConfig.setProxyPort(proxy.getPort());
//            wiFiAPConfig.setProxyExclusionString(proxy.getExclusion());
//            wiFiAPConfig.writeConfigurationToDevice();
//
//            AsyncSaveWiFiApConfig asyncSaveWiFiApConfig = new AsyncSaveWiFiApConfig(this, wiFiAPConfig);
//            asyncSaveWiFiApConfig.execute();

            getActivity().finish();
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during WiFiApListFragment selectPac(%d)",index);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (activity instanceof MasterActivity)
        {
            ((MasterActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        if (getActivity() instanceof MasterActivity)
        {
            MasterActivity master = (MasterActivity) getActivity();

            if (master != null && !master.isDrawerOpen())
            {
                // Only show items in the action bar relevant to this screen
                // if the drawer is not showing. Otherwise, let the drawer
                // decide what to show in the action bar.
                inflater.inflate(R.menu.proxy_list, menu);
                master.restoreActionBar();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_add_new_proxy:
                Intent addNewProxyIntent = new Intent(getActivity(), ProxyDetailFragment.class);
                addNewProxyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                addNewProxyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addNewProxyIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
