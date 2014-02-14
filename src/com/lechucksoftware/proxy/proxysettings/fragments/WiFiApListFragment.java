package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.lechucksoftware.proxy.proxysettings.ActionManager;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.WiFiApDetailActivity;
import com.lechucksoftware.proxy.proxysettings.adapters.WifiAPSelectorListAdapter;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.StatusFragmentStates;
import com.lechucksoftware.proxy.proxysettings.fragments.base.BaseListFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.loaders.ProxyConfigurationTaskLoader;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.enums.SecurityType;
import com.shouldit.proxy.lib.log.LogWrapper;

import java.util.List;

/**
 * Created by marco on 17/05/13.
 */
public class WiFiApListFragment extends BaseListFragment implements IBaseFragment, LoaderManager.LoaderCallbacks<List<ProxyConfiguration>>
{
    private static final String TAG = "WiFiApListFragment";
    private static final int LOADER_PROXYCONFIGURATIONS = 1;
    private static WiFiApListFragment instance;
    int mCurCheckPosition = 0;
    private WifiAPSelectorListAdapter apListAdapter;
    private TextView emptyText;
    private Loader<List<ProxyConfiguration>> loader;
    private RelativeLayout progress;

    public static WiFiApListFragment getInstance()
    {
        if (instance == null)
            instance = new WiFiApListFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuVisibility(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LogWrapper.startTrace(TAG,"onCreateView",Log.DEBUG);

        View v = inflater.inflate(R.layout.ap_list, container, false);

        progress = (RelativeLayout) v.findViewById(R.id.progress);
        emptyText = (TextView) v.findViewById(android.R.id.empty);

        LogWrapper.stopTrace(TAG, "onCreateView", Log.DEBUG);
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE,
                                    ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE |    // ENABLE
                                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);  // DISABLE

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

        ActionManager.getInstance().hide();

        progress.setVisibility(View.VISIBLE);
        if (apListAdapter == null)
        {
            apListAdapter = new WifiAPSelectorListAdapter(getActivity());
            setListAdapter(apListAdapter);
        }

        loader = getLoaderManager().initLoader(LOADER_PROXYCONFIGURATIONS, new Bundle(), this);
        loader.forceLoad();

        refreshUI();
    }

    public void initUI()
    {}

    public void refreshUI()
    {
        if (apListAdapter != null)
            apListAdapter.notifyDataSetChanged();

        if (APL.getWifiManager().isWifiEnabled())
        {
            getListView().setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            loader.forceLoad();
        }
        else
        {
            // Do not display results when Wi-Fi is not enabled
//            apListAdapter.setData(new ArrayList<ProxyConfiguration>());
            getListView().setVisibility(View.GONE);

            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText(getResources().getString(R.string.wifi_empty_list_wifi_off));
            ActionManager.getInstance().setStatus(StatusFragmentStates.ENABLE_WIFI);
        }

//        Toast.makeText(getActivity(), TAG + " REFRESHUI ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<List<ProxyConfiguration>> onCreateLoader(int i, Bundle bundle)
    {
        LogWrapper.startTrace(TAG,"onCreateLoader",Log.DEBUG);

        ProxyConfigurationTaskLoader proxyConfigurationTaskLoader = new ProxyConfigurationTaskLoader(getActivity());
        LogWrapper.stopTrace(TAG, "onCreateLoader", Log.DEBUG);

        return proxyConfigurationTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<ProxyConfiguration>> listLoader, List<ProxyConfiguration> proxyConfigurations)
    {
        LogWrapper.startTrace(TAG,"onLoadFinished",Log.DEBUG);
        if (APL.getWifiManager().isWifiEnabled())
        {
            if (proxyConfigurations != null && proxyConfigurations.size() > 0)
            {
                apListAdapter.setData(proxyConfigurations);
                ActionManager.getInstance().hide();
            }
            else
            {
                emptyText.setText(getResources().getString(R.string.wifi_empty_list_no_ap));
                ActionManager.getInstance().setStatus(StatusFragmentStates.CONNECT_TO);
            }
        }

        progress.setVisibility(View.GONE);

        LogWrapper.stopTrace(TAG,"onLoadFinished",Log.DEBUG);
        LogWrapper.stopTrace(TAG,"STARTUP", Log.DEBUG);
    }

    @Override
    public void onLoaderReset(Loader<List<ProxyConfiguration>> listLoader)
    {
//        Toast.makeText(getActivity(), TAG + " LOADRESET", Toast.LENGTH_SHORT).show();
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

            ProxyConfiguration selectedConfiguration = (ProxyConfiguration) getListView().getItemAtPosition(index);

            if (selectedConfiguration.ap.security == SecurityType.SECURITY_EAP)
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.oops)
                        .setMessage(getResources().getString(R.string.not_supported_network_8021x_error_message))
                        .setPositiveButton(R.string.proxy_error_dismiss, null)
                        .show();

                EventReportingUtils.sendException(new Exception("Not supported Wi-Fi security 802.1x!!"));
            }
            else
            {
                LogWrapper.d(TAG,"Selected proxy configuration: " + selectedConfiguration.toShortString());

                Intent i = new Intent(getActivity(), WiFiApDetailActivity.class);
                i.putExtra(Constants.SELECTED_AP_CONF_ARG, selectedConfiguration.id);
                startActivity(i);
            }
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(new Exception("Exception during WiFiApListFragment showDetails(" + index + ") " + e.toString()));
        }
    }
}
