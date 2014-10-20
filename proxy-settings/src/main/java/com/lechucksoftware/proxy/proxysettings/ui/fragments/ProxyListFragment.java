package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncSaveProxyConfiguration;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.ProxiesSelectorListAdapter;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.ui.base.IBaseFragment;

import java.util.ArrayList;
import java.util.List;

import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by marco on 17/05/13.
 */
public class ProxyListFragment extends BaseDialogFragment implements IBaseFragment, LoaderManager.LoaderCallbacks<List<ProxyEntity>>
{
    private static final String TAG = ProxyListFragment.class.getSimpleName();
//    private static ProxyListFragment instance;
    int mCurCheckPosition = 0;
    private ProxiesSelectorListAdapter proxiesListAdapter;

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

    // Arguments
    private static final String FRAGMENT_MODE_ARG = "FRAGMENT_MODE_ARG";
    private static final String PROXY_CONF_ARG = "PROXY_CONF_ARG";
    private WiFiAPConfig apConf;

    public static ProxyListFragment newInstance(int sectionNumber)
    {
        return newInstance(sectionNumber, FragmentMode.FULLSIZE, null);
    }

    public static ProxyListFragment newInstance(int sectionNumber, FragmentMode mode, WiFiAPConfig apConf)
    {
        ProxyListFragment fragment = new ProxyListFragment();

        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(FRAGMENT_MODE_ARG, mode);
        args.putSerializable(PROXY_CONF_ARG, apConf);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fragmentMode = (FragmentMode) getArguments().getSerializable(FRAGMENT_MODE_ARG);
        apConf = (WiFiAPConfig) getArguments().getSerializable(PROXY_CONF_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v;

        setHasOptionsMenu(true);

        if (fragmentMode == FragmentMode.DIALOG)
        {
            getDialog().setTitle(R.string.select_proxy);
            v = inflater.inflate(R.layout.proxy_list_dialog, container, false);

            ButterKnife.inject(this, v);
        }
        else
        {
            v = inflater.inflate(R.layout.proxy_list_fragment, container, false);

            ButterKnife.inject(this, v);
        }

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

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

        if (fragmentMode == FragmentMode.DIALOG)
        {
            cancelDialogButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dismiss();
                }
            });
        }
        else
        {
            footerTextView.setVisibility(View.GONE);
        }

        loader = getLoaderManager().initLoader(LOADER_PROXYDB, new Bundle(), this);
        loader.forceLoad();
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

            emptySection.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);

            if (fragmentMode == FragmentMode.FULLSIZE)
            {
                footerTextView.setVisibility(View.VISIBLE);
                footerTextView.setText(getString(R.string.num_proxies_configured, dbProxies.size()));
            }
        }
        else
        {
            proxiesListAdapter.setData(new ArrayList<ProxyEntity>());

            emptySection.setVisibility(View.VISIBLE);
            emptyText.setText(getResources().getString(R.string.proxy_empty_list));
            emptyText.setVisibility(View.VISIBLE);

            if (fragmentMode == FragmentMode.FULLSIZE)
            {
                footerTextView.setVisibility(View.GONE);
            }
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
            App.getLogger().d(TAG, "Selected proxy configuration: " + selectedProxy.toString());

            Intent i = new Intent(getActivity(), ProxyDetailActivity.class);
            App.getCacheManager().put(selectedProxy.getUUID(), selectedProxy);
            i.putExtra(Constants.SELECTED_PROXY_CONF_ARG, selectedProxy.getUUID());
            startActivity(i);
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(new Exception("Exception during WiFiApListFragment showDetails(" + index + ") " + e.toString()));
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

            apConf.setProxySetting(ProxySetting.STATIC);
            apConf.setProxyHost(proxy.getHost());
            apConf.setProxyPort(proxy.getPort());
            apConf.setProxyExclusionString(proxy.getExclusion());
            apConf.writeConfigurationToDevice();

            AsyncSaveProxyConfiguration asyncSaveProxyConfiguration = new AsyncSaveProxyConfiguration(this, apConf);
            asyncSaveProxyConfiguration.execute();
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(new Exception("Exception during WiFiApListFragment selectProxy(" + index + ") " + e.toString()));
        }
    }

    public void refreshUI()
    {
        if (loader != null)
            loader.forceLoad();
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
        ((MasterActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

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
