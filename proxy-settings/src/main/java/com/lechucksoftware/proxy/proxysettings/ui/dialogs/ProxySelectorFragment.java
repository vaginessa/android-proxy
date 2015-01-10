package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.FragmentMode;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyListFragment;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiAPConfig;

public class ProxySelectorFragment extends BaseFragment
{
    private static final String SELECTED_WIFI_NETWORK = "SELECTED_WIFI_NETWORK";

    FragmentTabHost tabHost;
    private APLNetworkId wifiAplNetworkId;

    public static ProxySelectorFragment newInstance(APLNetworkId wifiAplNetworkId)
    {
        ProxySelectorFragment fragment = new ProxySelectorFragment();

        Bundle args = new Bundle();
        
        args.putSerializable(SELECTED_WIFI_NETWORK, wifiAplNetworkId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        wifiAplNetworkId = (APLNetworkId) getArguments().getSerializable(SELECTED_WIFI_NETWORK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v;

        v = inflater.inflate(R.layout.proxy_selector_dialog, container, false);

        tabHost = (FragmentTabHost) v.findViewById(android.R.id.tabhost);

        tabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        Bundle args = new Bundle();
        args.putSerializable(Constants.FRAGMENT_MODE_ARG, FragmentMode.DIALOG);
        args.putSerializable(Constants.WIFI_AP_NETWORK_ARG, wifiAplNetworkId);

        tabHost.addTab(tabHost.newTabSpec("static_proxies").setIndicator("STATIC"), ProxyListFragment.class, args);
        tabHost.addTab(tabHost.newTabSpec("pac_proxies").setIndicator("PAC"), ProxyListFragment.class, args);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}
