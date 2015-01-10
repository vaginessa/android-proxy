package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncSaveWiFiApConfig;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxySelectorListActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputExclusionList;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputField;
import com.lechucksoftware.proxy.proxysettings.ui.components.WifiSignal;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.NoProxiesDefinedAlertDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.ProxySelectorFragment;
import com.lechucksoftware.proxy.proxysettings.utils.FragmentsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;


public class WiFiApDetailFragment extends BaseFragment implements IBaseFragment
{
    public static final String TAG = WiFiApDetailFragment.class.getSimpleName();

    private APLNetworkId wifiNetworkId;
    private WiFiAPConfig selectedWiFiAP;
    private ProxyEntity selectedProxy;

    @InjectView(R.id.wifi_signal) WifiSignal wifiSignal;
    @InjectView(R.id.wifi_name) TextView wifiName;
    @InjectView(R.id.wifi_layout) ViewGroup wifiLayout;
    @InjectView(R.id.wifi_proxy_switch) Switch proxySwitch;
    @InjectView(R.id.proxy_selector) TextView proxySelector;
    @InjectView(R.id.proxy_host) InputField proxyHost;
    @InjectView(R.id.proxy_port) InputField proxyPort;
    @InjectView(R.id.proxy_bypass) InputExclusionList proxyBypass;
    @InjectView(R.id.wifi_proxy_input_fields) RelativeLayout proxyFieldsLayout;
//    @InjectView(R.id.progress) RelativeLayout progress;

    /**
     * Create a new instance of WiFiApDetailFragment
     */
    public static WiFiApDetailFragment newInstance(APLNetworkId wifiNetworkId)
    {
        WiFiApDetailFragment instance = new WiFiApDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(Constants.SELECTED_AP_CONF_ARG, wifiNetworkId);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        wifiNetworkId = (APLNetworkId) getArguments().getSerializable(Constants.SELECTED_AP_CONF_ARG);
        selectedWiFiAP = App.getWifiNetworksManager().getConfiguration(wifiNetworkId);

        if (selectedWiFiAP == null)
        {
            FragmentsUtils.goToMainActivity(getActivity());
        }

        refreshUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        App.getTraceUtils().startTrace(TAG, "onCreateView", Log.DEBUG);

        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.wifi_ap_preferences, container, false);
        ButterKnife.inject(this, v);

//        proxyTags = (InputTags) v.findViewById(R.id.proxy_tags);

//        proxyEditButton = (ImageButton) v.findViewById(R.id.edit_proxy_button);
//        proxyEditButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view)
//            {
//                Intent i = new Intent(getActivity(), ProxyDetailActivity.class);
//                App.getCacheManager().put(selectedProxy.getUUID(), selectedProxy);
//                i.putExtra(Constants.SELECTED_PROXY_CONF_ARG, selectedProxy.getUUID());
//                startActivity(i);
//            }
//        });
        refreshUI();

        App.getTraceUtils().stopTrace(TAG, "onCreateView", Log.DEBUG);
        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    @OnClick(R.id.wifi_proxy_switch)
    public void proxySwitchClicked()
    {
//        progress.setVisibility(View.VISIBLE);

        if (proxySwitch.isChecked())
        {
            Timber.d(TAG, "Set proxy settings = STATIC");
            selectedWiFiAP.setProxySetting(ProxySetting.STATIC);
        }
        else
        {
            Timber.d(TAG, "Set proxy settings = NONE");
            selectedWiFiAP.setProxySetting(ProxySetting.NONE);
            selectedWiFiAP.setProxyHost(null);
            selectedWiFiAP.setProxyPort(0);
            selectedWiFiAP.setProxyExclusionString(null);
        }

        saveConfiguration();
        refreshUI();
    }

    @OnClick(R.id.proxy_selector)
    public void openProxySelectorDialog()
    {
        Map<Long, ProxyEntity> savedProxies = App.getDBManager().getAllProxiesWithTAGs();
        List<ProxyEntity> availableProxies = new ArrayList<ProxyEntity>(savedProxies.values());

        if (availableProxies != null && availableProxies.size() > 0)
        {
            Intent i = new Intent(getActivity(), ProxySelectorListActivity.class);
            i.putExtra(Constants.WIFI_AP_NETWORK_ARG, selectedWiFiAP.getAPLNetworkId());
            startActivity(i);

//            ProxyListFragment proxiesListFragment = ProxyListFragment.newInstance(0, FragmentMode.DIALOG, selectedWiFiAP);
//            proxiesListFragment.show(getFragmentManager(), TAG);
        }
        else
        {
            NoProxiesDefinedAlertDialog noProxiesDefinedAltertDialog = NoProxiesDefinedAlertDialog.newInstance();
            noProxiesDefinedAltertDialog.setTargetFragment(this, Requests.CREATE_NEW_PROXY);
            noProxiesDefinedAltertDialog.show(getFragmentManager(),"NoProxiesDefinedAlertDialog");
        }
    }

//    private void openProxyEditorDialog()
//    {
//        ProxyDetailFragment dialog = ProxyDetailFragment.newInstance(selectedProxy);
//        dialog.show(getFragmentManager(),TAG);
//    }

    private void refreshVisibility()
    {
        if (proxySwitch.isChecked())
        {
            proxySelector.setVisibility(View.VISIBLE);

            if (selectedProxy == null)
            {
                proxyFieldsLayout.setVisibility(View.GONE);
//                proxySelector.setError("SELECT A PROXY");
            }
            else
            {
                proxyFieldsLayout.setVisibility(View.VISIBLE);
//                proxySelector.setError(null);
            }
        }
        else
        {
            proxySelector.setVisibility(View.GONE);
            proxyFieldsLayout.setVisibility(View.GONE);
        }

//        progress.setVisibility(View.GONE);
    }

    private void saveConfiguration()
    {
        // TODO: handle into async task ProgressVisibility
//        progress.setVisibility(View.VISIBLE);

        AsyncSaveWiFiApConfig asyncSaveWiFiApConfig = new AsyncSaveWiFiApConfig(this, selectedWiFiAP);
        asyncSaveWiFiApConfig.execute();
    }

    public void refreshUI()
    {
//        LogWrapper.startTrace(TAG, "refreshUI", Log.DEBUG);

        if (!APL.getWifiManager().isWifiEnabled())
        {
            FragmentsUtils.goToMainActivity(getActivity());
        }

        if (selectedWiFiAP != null)
        {
            if (selectedWiFiAP.getProxySetting() == ProxySetting.STATIC)
            {
                Timber.d("Set proxy switch = ON");
                proxySwitch.setChecked(true);
                proxySwitch.setText(R.string.status_proxy_enabled);
                refreshFieldsValues();
            }
            else
            {
                Timber.d("Set proxy switch = OFF");
                proxySwitch.setChecked(false);
                proxySwitch.setText(R.string.status_proxy_disabled);
            }

            if (selectedWiFiAP.getLevel() == -1)
            {
                wifiLayout.setBackgroundResource(R.color.DarkGrey);
            }
            else
            {
                if (selectedWiFiAP.isActive())
                {
                    wifiLayout.setBackgroundResource(R.color.Holo_Blue_Dark);
                }
                else
                {
                    wifiLayout.setBackgroundResource(R.color.Holo_Green_Dark);
                }
            }

            wifiName.setText(ProxyUtils.cleanUpSSID(selectedWiFiAP.getSSID()));
    //        wifiStatus.setText(selectedWiFiAP.getProxyStatusString());
            wifiSignal.setConfiguration(selectedWiFiAP);

            refreshVisibility();
        }
//        else
//        {
//            LogWrapper.d(TAG,"selectedWiFiAP is NULL: " + String.valueOf(confId));
////            NavigationUtils.goToMainActivity(getActivity());
//        }

//        LogWrapper.stopTrace(TAG, "refreshUI", Log.DEBUG);
    }

    private void refreshFieldsValues()
    {
        long proxyId = App.getDBManager().findProxy(selectedWiFiAP);
        if (proxyId != -1)
        {
            selectedProxy = App.getDBManager().getProxy(proxyId);
            proxyHost.setValue(selectedProxy.getHost());
            proxyPort.setValue(selectedProxy.getPort());
            proxyBypass.setExclusionString(selectedProxy.getExclusion());
//            proxyTags.setTags(selectedProxy.getTags());
        }
        else
        {
            selectedProxy = null;
            proxyHost.setValue("");
            proxyPort.setValue("");
            proxyBypass.setExclusionString("");
//            proxyTags.setTags(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.ap_wifi_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent mainIntent = new Intent(getActivity(), MasterActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Requests.CREATE_NEW_PROXY)
        {
            Intent addNewProxyIntent = new Intent(getActivity(), ProxyDetailActivity.class);
            addNewProxyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            addNewProxyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(addNewProxyIntent);
        }
    }
}
