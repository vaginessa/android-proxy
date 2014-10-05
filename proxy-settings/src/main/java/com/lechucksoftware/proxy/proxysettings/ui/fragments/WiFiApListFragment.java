package com.lechucksoftware.proxy.proxysettings.ui.fragments;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.loaders.ProxyConfigurationTaskLoader;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WiFiApDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.WifiAPListAdapter;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.components.ActionsView;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import java.util.List;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by marco on 17/05/13.
 */
public class WiFiApListFragment extends BaseFragment implements IBaseFragment, LoaderManager.LoaderCallbacks<List<WiFiAPConfig>>
{
    private static final String TAG = WiFiApListFragment.class.getSimpleName();
    private static final int LOADER_PROXYCONFIGURATIONS = 1;
    private static WiFiApListFragment instance;

    private WifiAPListAdapter apListAdapter;
    private Loader<List<WiFiAPConfig>> loader;

    @InjectView(R.id.progress) RelativeLayout progress;
    @InjectView(R.id.actions_view) ActionsView actionsView;
    @InjectView(R.id.empty_message_section) RelativeLayout emptySection;
    @InjectView(R.id.wifi_ap_footer_textview) TextView footerTextView;
    @InjectView(R.id.wifi_ap_footer_progress) ProgressBar footerProgress;

    @InjectView(android.R.id.empty) TextView emptyText;
    @InjectView(android.R.id.list) ListView listView;

    public static WiFiApListFragment getInstance()
    {
        if (instance == null)
            instance = new WiFiApListFragment();

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        App.getLogger().startTrace(TAG, "onCreateView", Log.DEBUG);

        View v = inflater.inflate(R.layout.wifi_ap_list_fragment, container, false);

        ButterKnife.inject(this, v);

        App.getLogger().stopTrace(TAG, "onCreateView", Log.DEBUG);
        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

//    public void onActivityCreated(Bundle savedInstanceState)
//    {
//        super.onActivityCreated(savedInstanceState);
//
//        ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setTitle(getResources().getString(R.string.app_name));
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE,
//                                    ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE |    // ENABLE
//                                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);  // DISABLE
//    }

    @Override
    public void onResume()
    {
        super.onResume();

        progress.setVisibility(View.VISIBLE);

        actionsView.wifiOnOffEnable(false);
        actionsView.wifiConfigureEnable(false);

        footerTextView.setVisibility(View.GONE);
        footerProgress.setVisibility(View.GONE);
//        footerTextView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                ProxyUtils.startAndroidWifiSettings(getActivity());
//            }
//        });

        if (apListAdapter == null)
        {
            apListAdapter = new WifiAPListAdapter(getActivity());
        }

        listView.setAdapter(apListAdapter);

        loader = getLoaderManager().initLoader(LOADER_PROXYCONFIGURATIONS, new Bundle(), this);
        loader.forceLoad();
    }

    public void refreshUI()
    {
        if (loader != null)
        {
            loader.forceLoad();
            footerProgress.setVisibility(View.VISIBLE);
        }
    }

    public void refreshLoaderResults(List<WiFiAPConfig> wiFiApConfigs)
    {
        App.getLogger().startTrace(TAG, "refreshLoaderResults", Log.DEBUG);

        if (Utils.isAirplaneModeOn(getActivity()))
        {
            emptySection.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText(getActivity().getString(R.string.airplane_mode_message));

            actionsView.wifiConfigureEnable(false);
            actionsView.wifiOnOffEnable(false);
            footerTextView.setVisibility(View.GONE);
        }
        else
        {
            if (APL.getWifiManager().isWifiEnabled())
            {
                actionsView.wifiOnOffEnable(false);

                if (wiFiApConfigs != null && wiFiApConfigs.size() > 0)
                {
                    apListAdapter.setData(wiFiApConfigs);

                    listView.setVisibility(View.VISIBLE);
                    emptySection.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);

                    // TODO: Add WifiConfigureEnable if Wi-Fi is enabled, some Wi-Fi are available but no Wi-Fi is active
                    boolean atLeastOneActive = false;
                    for (WiFiAPConfig config : wiFiApConfigs)
                    {
                        if (config.isActive())
                        {
                            atLeastOneActive = true;
                            break;
                        }
                    }

                    if (atLeastOneActive)
                        actionsView.wifiConfigureEnable(false);
                    else
                        actionsView.wifiConfigureEnable(true);

                    footerTextView.setVisibility(View.VISIBLE);
                    footerTextView.setText(getString(R.string.num_wifi_access_points_configured, wiFiApConfigs.size()));
                }
                else
                {
                    listView.setVisibility(View.GONE);
                    emptySection.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText(getResources().getString(R.string.wifi_empty_list_no_ap));

                    actionsView.wifiConfigureEnable(true);
                    footerTextView.setVisibility(View.GONE);
                }
            }
            else
            {
                // Do not display results when Wi-Fi is not enabled
//            apListAdapter.setData(new ArrayList<WiFiAPConfig>());
                listView.setVisibility(View.GONE);
                emptySection.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText(getResources().getString(R.string.wifi_empty_list_wifi_off));

                actionsView.wifiOnOffEnable(true);
                actionsView.wifiConfigureEnable(false);
                footerTextView.setVisibility(View.GONE);
            }
        }

        App.getLogger().stopTrace(TAG, "refreshLoaderResults", Log.DEBUG);
    }

    @Override
    public Loader<List<WiFiAPConfig>> onCreateLoader(int i, Bundle bundle)
    {
        App.getLogger().startTrace(TAG, "onCreateLoader", Log.DEBUG);

        ProxyConfigurationTaskLoader proxyConfigurationTaskLoader = new ProxyConfigurationTaskLoader(getActivity());

        App.getLogger().stopTrace(TAG, "onCreateLoader", Log.DEBUG);

        return proxyConfigurationTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<WiFiAPConfig>> listLoader, List<WiFiAPConfig> aps)
    {
        App.getLogger().startTrace(TAG, "onLoadFinished", Log.DEBUG);

        progress.setVisibility(View.GONE);
        footerProgress.setVisibility(View.GONE);

        refreshLoaderResults(aps);

        App.getLogger().stopTrace(TAG, "onLoadFinished", Log.DEBUG);
        App.getLogger().stopTrace(TAG, "STARTUP", Log.ERROR);
    }

    @Override
    public void onLoaderReset(Loader<List<WiFiAPConfig>> listLoader)
    {
        App.getLogger().d(TAG, "onLoaderReset");
    }

    @OnItemClick(android.R.id.list)
    void showDetails(int index)
    {
        try
        {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            listView.setItemChecked(index, true);

            WiFiAPConfig selectedConfiguration = (WiFiAPConfig) listView.getItemAtPosition(index);

            if (selectedConfiguration.getSecurityType() == SecurityType.SECURITY_EAP)
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.oops)
                        .setMessage(getResources().getString(R.string.not_supported_network_8021x_error_message))
                        .setPositiveButton(R.string.proxy_error_dismiss, null)
                        .show();

                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                        R.string.analytics_act_button_click,
                        R.string.analytics_lab_8021x_security_not_supported);
            }
            else
            {
                App.getLogger().d(TAG, "Selected proxy configuration: " + selectedConfiguration.toShortString());

                Intent i = new Intent(getActivity(), WiFiApDetailActivity.class);
                i.putExtra(Constants.SELECTED_AP_CONF_ARG, selectedConfiguration.getAPLNetworkId());
                startActivity(i);
            }
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(new Exception("Exception during WiFiApListFragment showDetails(" + index + ") " + e.toString()));
        }
    }
}
