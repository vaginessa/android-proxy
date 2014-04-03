package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.FragmentMode;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncSaveProxyConfiguration;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputExclusionList;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputField;
import com.lechucksoftware.proxy.proxysettings.ui.components.WifiSignal;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.NoProxiesDefinedAlertDialog;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;

import java.util.List;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.ProxyConfiguration;
import be.shouldit.proxy.lib.WifiNetworkId;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import be.shouldit.proxy.lib.utils.ProxyUtils;


public class WiFiApDetailFragment extends BaseFragment implements IBaseFragment
{
    public static final String TAG = "WiFiApDetailFragment";

    private ProxyConfiguration selectedWiFiAP;
    private ProxyEntity selectedProxy;

    private TextView wifiName;
//    private TextView wifiStatus;
    private WifiSignal wifiSignal;
    private Switch proxySwitch;
    private TextView proxySelector;
    private ViewGroup wifiLayout;
    private InputField proxyHost;
    private InputField proxyPort;
    private InputExclusionList proxyBypass;
//    private InputTags proxyTags;

    private WifiNetworkId wifiNetworkId;
    private RelativeLayout proxyFieldsLayout;
    private ImageButton proxyEditButton;


    /**
     * Create a new instance of WiFiApDetailFragment
     */
    public static WiFiApDetailFragment newInstance(WifiNetworkId wifiNetworkId)
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

        wifiNetworkId = (WifiNetworkId) getArguments().getSerializable(Constants.SELECTED_AP_CONF_ARG);
//        LogWrapper.d(TAG,"confId: " + String.valueOf(confId));
        selectedWiFiAP = App.getProxyManager().getConfiguration(wifiNetworkId);

        if (selectedWiFiAP == null)
        {
            NavigationUtils.GoToMainActivity(getActivity());
        }

        refreshUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        App.getLogger().startTrace(TAG, "onCreateView", Log.DEBUG);

        View v = inflater.inflate(R.layout.wifi_ap_preferences, container, false);

//        LogWrapper.getPartial(TAG, "onCreateView", Log.DEBUG);

        progress = v.findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        content =  v.findViewById(R.id.content);
        content.setVisibility(View.VISIBLE);


        wifiLayout = (ViewGroup) v.findViewById(R.id.wifi_layout);
//        wifiLayout.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                ActionsListFragment actionsListFragment = ActionsListFragment.newInstance(FragmentMode.DIALOG, selectedWiFiAP);
//                actionsListFragment.show(getFragmentManager(), TAG);
//            }
//        });

        wifiName = (TextView) v.findViewById(R.id.wifi_name);
//        wifiStatus = (TextView) v.findViewById(R.id.wifi_status);
        wifiSignal = (WifiSignal) v.findViewById(R.id.wifi_signal);
        proxySwitch = (Switch) v.findViewById(R.id.wifi_proxy_switch);
        proxySwitch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (proxySwitch.isChecked())
                {
                    App.getLogger().d(TAG, "Set proxy settings = STATIC");
                    selectedWiFiAP.setProxySetting(ProxySetting.STATIC);
                }
                else
                {
                    App.getLogger().d(TAG, "Set proxy settings = NONE");
                    selectedWiFiAP.setProxySetting(ProxySetting.NONE);
                    selectedWiFiAP.setProxyHost(null);
                    selectedWiFiAP.setProxyPort(0);
                    selectedWiFiAP.setProxyExclusionList(null);
                }

                saveConfiguration();
                refreshUI();
            }
        });

        proxySelector = (TextView) v.findViewById(R.id.proxy_selector);
        proxySelector.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openProxySelectorDialog();
            }
        });

        proxyFieldsLayout = (RelativeLayout) v.findViewById(R.id.wifi_proxy_input_fields);

        proxyHost = (InputField) v.findViewById(R.id.proxy_host);
//        proxyHost.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                openProxyEditorDialog();
//            }
//        });


        proxyPort = (InputField) v.findViewById(R.id.proxy_port);
        proxyBypass = (InputExclusionList) v.findViewById(R.id.proxy_bypass);
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

        App.getLogger().stopTrace(TAG, "onCreateView", Log.DEBUG);
        return v;
    }

//    private void openProxyEditorDialog()
//    {
//        ProxyDetailFragment dialog = ProxyDetailFragment.newInstance(selectedProxy);
//        dialog.show(getFragmentManager(),TAG);
//    }

    private void openProxySelectorDialog()
    {
        List<ProxyEntity> availableProxies = App.getCacheManager().getAllProxiesList();
        if (availableProxies != null && availableProxies.size() > 0)
        {
            ProxyListFragment proxiesListFragment = ProxyListFragment.newInstance(FragmentMode.DIALOG, selectedWiFiAP);
            proxiesListFragment.show(getFragmentManager(), TAG);
        }
        else
        {
            NoProxiesDefinedAlertDialog noProxiesDefinedAltertDialog = NoProxiesDefinedAlertDialog.newInstance();
            noProxiesDefinedAltertDialog.show(getFragmentManager(),"NoProxiesDefinedAlertDialog");
        }
    }

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
    }

    private void saveConfiguration()
    {
        // TODO: handle into async task ProgressVisibility
//        progress.setVisibility(View.VISIBLE);

        AsyncSaveProxyConfiguration asyncSaveProxyConfiguration = new AsyncSaveProxyConfiguration(this, selectedWiFiAP);
        asyncSaveProxyConfiguration.execute();
    }

    public void refreshUI()
    {
//        LogWrapper.startTrace(TAG, "refreshUI", Log.DEBUG);

        if (!APL.getWifiManager().isWifiEnabled())
        {
            NavigationUtils.GoToMainActivity(getActivity());
        }

        if (selectedWiFiAP != null)
        {
            if (selectedWiFiAP.getProxySettings() == ProxySetting.STATIC)
            {
                App.getLogger().d(TAG, "Set proxy switch = ON");
                proxySwitch.setChecked(true);
                proxySwitch.setText(R.string.status_proxy_enabled);
                refreshFieldsValues();
            }
            else
            {
                App.getLogger().d(TAG, "Set proxy switch = OFF");
                proxySwitch.setChecked(false);
                proxySwitch.setText(R.string.status_proxy_disabled);
            }

            if (selectedWiFiAP.ap.getLevel() == -1)
            {
                wifiLayout.setBackgroundResource(R.color.DarkGrey);
            }
            else
            {
                if (selectedWiFiAP.isCurrentNetwork())
                {
                    wifiLayout.setBackgroundResource(R.color.Holo_Blue_Dark);
                }
                else
                {
                    wifiLayout.setBackgroundResource(R.color.Holo_Green_Dark);
                }
            }

            wifiName.setText(ProxyUtils.cleanUpSSID(selectedWiFiAP.getAPDescription()));
    //        wifiStatus.setText(selectedWiFiAP.toStatusString());
            wifiSignal.setConfiguration(selectedWiFiAP);

            refreshVisibility();
        }
//        else
//        {
//            LogWrapper.d(TAG,"selectedWiFiAP is NULL: " + String.valueOf(confId));
////            NavigationUtils.GoToMainActivity(getActivity());
//            EventReportingUtils.sendException(new Exception("NO WIFI AP SELECTED"));
//        }

//        LogWrapper.stopTrace(TAG, "refreshUI", Log.DEBUG);
    }

    private void refreshFieldsValues()
    {
        long proxyId = App.getDBManager().findProxy(selectedWiFiAP);
        if (proxyId != -1)
        {
            selectedProxy = App.getDBManager().getProxy(proxyId);
            proxyHost.setValue(selectedProxy.host);
            proxyPort.setValue(selectedProxy.port);
            proxyBypass.setExclusionString(selectedProxy.exclusion);
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
}
