package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.ProxySelectionDialogAdapter;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

import be.shouldit.proxy.lib.WiFiAPConfig;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProxySelectorDialog extends BaseDialogFragment
{
    private static final String SELECTED_WIFI_NETWORK = "SELECTED_WIFI_NETWORK";
    private WiFiAPConfig selectedWifiNetwork;

    @InjectView(R.id.dialog_viewpager) ViewPager viewPager;

    private ProxySelectionDialogAdapter selectionAdapter;

    public static ProxySelectorDialog newInstance(WiFiAPConfig wifiNetwork)
    {
        ProxySelectorDialog fragment = new ProxySelectorDialog();

        Bundle args = new Bundle();
        
        args.putSerializable(SELECTED_WIFI_NETWORK, wifiNetwork);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        selectedWifiNetwork = (WiFiAPConfig) getArguments().getSerializable(SELECTED_WIFI_NETWORK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v;

        getDialog().setTitle(R.string.select_proxy);
        v = inflater.inflate(R.layout.proxy_list_dialog, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.

        selectionAdapter = new ProxySelectionDialogAdapter(getFragmentManager());
        viewPager.setAdapter(selectionAdapter);
    }
}
