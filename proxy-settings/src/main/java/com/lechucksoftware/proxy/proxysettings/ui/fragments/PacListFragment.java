package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.loaders.PacDBTaskLoader;
import com.lechucksoftware.proxy.proxysettings.ui.activities.PacDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.PacListAdapter;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.ui.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import timber.log.Timber;

/**
 * Created by marco on 17/05/13.
 */
public class PacListFragment extends BaseDialogFragment implements IBaseFragment, LoaderManager.LoaderCallbacks<List<PacEntity>>
{
    private static final String TAG = PacListFragment.class.getSimpleName();
//    private static ProxyListFragment instance;
    int mCurCheckPosition = 0;
    private PacListAdapter pacListAdapter;

    private Loader<List<PacEntity>> loader;

    @InjectView(R.id.progress) RelativeLayout progress;
    @InjectView(R.id.empty_message_section) RelativeLayout emptySection;
    @InjectView(R.id.add_new_pac_proxy) FloatingActionButton addNewPacProxyButton;

    @InjectView(android.R.id.empty) TextView emptyText;
    @InjectView(android.R.id.list) ListView listView;

    @Optional @InjectView(R.id.dialog_cancel) Button cancelDialogButton; // Cancel not displayed into full fragment

    private FragmentMode fragmentMode;

    // Loaders
    private static final int LOADER_PACDB = 1;

    private WiFiApConfig wiFiApConfig;
    private APLNetworkId aplNetworkId;

    public static PacListFragment newInstance()
    {
        return newInstance(FragmentMode.FULLSIZE, null);
    }

    public static PacListFragment newInstance(FragmentMode mode, APLNetworkId aplNetworkId)
    {
        PacListFragment fragment = new PacListFragment();

        Bundle args = new Bundle();
        args.putSerializable(Constants.FRAGMENT_MODE_ARG, mode);
        args.putParcelable(Constants.WIFI_AP_NETWORK_ARG, aplNetworkId);
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
                aplNetworkId = getArguments().getParcelable(Constants.WIFI_AP_NETWORK_ARG);

                if (aplNetworkId != null)
                {
                    wiFiApConfig = App.getWifiNetworksManager().getConfiguration(aplNetworkId);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v;

        setHasOptionsMenu(true);

        v = inflater.inflate(R.layout.pac_list_fragment, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        progress.setVisibility(View.VISIBLE);

        if (pacListAdapter == null)
        {
            pacListAdapter = new PacListAdapter(getActivity());
        }

        listView.setAdapter(pacListAdapter);
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
                    selectPac(i);
                }
            }
        });

        addNewPacProxyButton.setVisibility(UIUtils.booleanToVisibility(fragmentMode == FragmentMode.FULLSIZE));

        loader = getLoaderManager().initLoader(LOADER_PACDB, new Bundle(), this);
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
    public Loader<List<PacEntity>> onCreateLoader(int i, Bundle bundle)
    {
        PacDBTaskLoader pacDBTaskLoader = new PacDBTaskLoader(getActivity());
        return pacDBTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<PacEntity>> listLoader, List<PacEntity> dbPacs)
    {
        refreshLoaderResults(dbPacs);
    }

    private void refreshLoaderResults(List<PacEntity> dbPacs)
    {
        if (dbPacs != null && dbPacs.size() > 0)
        {
            pacListAdapter.setData(dbPacs);

            emptySection.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);

//            footerTextView.setVisibility(View.VISIBLE);
//            footerTextView.setText(getString(R.string.num_proxies_configured, dbPacs.size()));
        }
        else
        {
            pacListAdapter.setData(new ArrayList<PacEntity>());

            emptySection.setVisibility(View.VISIBLE);
            emptyText.setText(getString(R.string.proxy_empty_list));
            emptyText.setVisibility(View.VISIBLE);

//            footerTextView.setVisibility(View.GONE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<PacEntity>> listLoader)
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

            PacEntity selectedPac = (PacEntity) listView.getItemAtPosition(index);
            Timber.d("Selected PAC configuration: " + selectedPac.toString());

            Intent i = new Intent(getActivity(), PacDetailActivity.class);
//            App.getCacheManager().put(selectedProxy.getUUID(), selectedProxy);
            i.putExtra(Constants.SELECTED_PAC_CONF_ARG, selectedPac.getId());
            startActivity(i);
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during WiFiApListFragment showDetails(%d)", index);
        }
    }

    void selectPac(int index)
    {
        mCurCheckPosition = index;

        try
        {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            listView.setItemChecked(index, true);
            PacEntity pacEntity = (PacEntity) listView.getItemAtPosition(index);

            Intent i = new Intent();
            i.putExtra(Constants.SELECTED_PROXY_TYPE_ARG, ProxySetting.PAC);
            i.putExtra(Constants.SELECTED_PAC_CONF_ARG, pacEntity);
            getActivity().setResult(AppCompatActivity.RESULT_OK, i);
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

    @OnClick(R.id.add_new_pac_proxy)
    public void createNewProxy()
    {
        Intent addNewProxyIntent = new Intent(getActivity(), PacDetailActivity.class);
        addNewProxyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        addNewProxyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(addNewProxyIntent);
    }
}
