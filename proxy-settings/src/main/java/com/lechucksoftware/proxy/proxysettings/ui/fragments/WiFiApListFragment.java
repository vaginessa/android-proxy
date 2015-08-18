package com.lechucksoftware.proxy.proxysettings.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.loaders.ProxyConfigurationTaskLoader;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WiFiApDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.WifiAPListAdapter;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.nispok.snackbar.SnackbarManager;

import java.util.List;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.enums.SecurityType;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import timber.log.Timber;

/**
 * Created by marco on 17/05/13.
 */
public class WiFiApListFragment extends BaseFragment implements IBaseFragment, LoaderManager.LoaderCallbacks<List<WiFiApConfig>>
{
    private static final String TAG = WiFiApListFragment.class.getSimpleName();
    private static final int LOADER_PROXYCONFIGURATIONS = 1;
    private static WiFiApListFragment instance;

    private WifiAPListAdapter apListAdapter;
    private Loader<List<WiFiApConfig>> loader;

    @InjectView(R.id.progress) RelativeLayout progress;
    //    @InjectView(R.id.actions_view) ActionsView actionsView;
    @InjectView(R.id.empty_message_section) RelativeLayout emptySection;
//    @InjectView(R.id.wifi_ap_footer_textview) TextView footerTextView;
//    @InjectView(R.id.wifi_ap_footer_progress) ProgressBar footerProgress;

    @InjectView(android.R.id.empty) TextView emptyText;
    @InjectView(android.R.id.list) ListView listView;

    public static WiFiApListFragment newInstance()
    {
        return new WiFiApListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        App.getTraceUtils().startTrace(TAG, "onCreateView", Log.DEBUG);

        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.wifi_ap_list_fragment, container, false);

        ButterKnife.inject(this, v);

        App.getTraceUtils().stopTrace(TAG, "onCreateView", Log.DEBUG);
        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        ButterKnife.reset(this);
        SnackbarManager.dismiss();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        progress.setVisibility(View.VISIBLE);

//        actionsView.wifiOnOffEnable(false);
//        actionsView.wifiConfigureEnable(false);

//        footerTextView.setVisibility(View.GONE);
//        footerProgress.setVisibility(View.GONE);
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
//            footerProgress.setVisibility(View.VISIBLE);
        }
    }

    public void refreshLoaderResults(List<WiFiApConfig> wiFiApConfigs)
    {
        progress.setVisibility(View.GONE);

        if (APL.getWifiManager().isWifiEnabled())
        {
            SnackbarManager.dismiss();

            if (wiFiApConfigs != null && wiFiApConfigs.size() > 0)
            {
                apListAdapter.setData(wiFiApConfigs);

                listView.setVisibility(View.VISIBLE);
                emptySection.setVisibility(View.GONE);
                emptyText.setVisibility(View.GONE);
            }
            else
            {
                listView.setVisibility(View.GONE);
                emptySection.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText(getString(R.string.wifi_empty_list_no_ap));
            }
        }
        else
        {
            // Do not display results when Wi-Fi is not enabled
            listView.setVisibility(View.GONE);
            emptySection.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText(getString(R.string.wifi_empty_list_wifi_off));

            if (!Utils.isAirplaneModeOn(getActivity()))
            {
                // Show enable Wi-fi action only if not in airplane mode
                UIUtils.showEnableWifiSnackbar(getActivity());
            }
        }
    }

    @Override
    public Loader<List<WiFiApConfig>> onCreateLoader(int i, Bundle bundle)
    {
        ProxyConfigurationTaskLoader proxyConfigurationTaskLoader = new ProxyConfigurationTaskLoader(getActivity());
        return proxyConfigurationTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<WiFiApConfig>> listLoader, List<WiFiApConfig> aps)
    {
        refreshLoaderResults(aps);
    }

    @Override
    public void onLoaderReset(Loader<List<WiFiApConfig>> listLoader)
    {
        Timber.d("onLoaderReset");
    }

    @OnItemClick(android.R.id.list)
    void showDetails(int index)
    {
        try
        {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            listView.setItemChecked(index, true);

            WiFiApConfig selectedConfiguration = (WiFiApConfig) listView.getItemAtPosition(index);

            if (selectedConfiguration.getSecurityType() == SecurityType.SECURITY_EAP)
            {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.oops)
                        .content(getString(R.string.not_supported_network_8021x_error_message))
                        .positiveText(R.string.proxy_error_dismiss)
                        .show();

                App.getEventsReporter().sendEvent(R.string.analytics_cat_user_action,
                                                    R.string.analytics_act_button_click,
                                                    R.string.analytics_lab_8021x_security_not_supported, 0L);
            }
            else
            {
                Timber.d("Selected proxy configuration: " + selectedConfiguration.toShortString());

                Intent i = new Intent(getActivity(), WiFiApDetailActivity.class);
                i.putExtra(Constants.SELECTED_AP_CONF_ARG, selectedConfiguration.getAPLNetworkId());
                startActivity(i);
            }
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception during WiFiApListFragment showDetails(%d)", index);
        }
    }
}
