package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxySelectorListActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputExclusionList;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputField;
import com.lechucksoftware.proxy.proxysettings.ui.components.WifiAp;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.NoProxiesDefinedAlertDialog;
import com.lechucksoftware.proxy.proxysettings.utils.FragmentsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;


public class WiFiApDetailFragment extends BaseFragment implements IBaseFragment
{
    public static final String TAG = WiFiApDetailFragment.class.getSimpleName();

    private APLNetworkId wifiNetworkId;
    private WiFiApConfig selectedWiFiAP;

//    private ProxyEntity selectedProxy;

    @InjectView(R.id.wifi_ap_header) WifiAp wifiApHeader;
    @InjectView(R.id.wifi_layout) ViewGroup wifiLayout;
    @InjectView(R.id.wifi_proxy_switch) SwitchCompat proxySwitch;
    @InjectView(R.id.proxy_selector) Button proxySelector;
    @InjectView(R.id.proxy_type) InputField proxyType;
    @InjectView(R.id.proxy_host) InputField proxyHost;
    @InjectView(R.id.proxy_port) InputField proxyPort;
    @InjectView(R.id.proxy_pac_url) InputField proxyPacUrl;
    @InjectView(R.id.proxy_bypass) InputExclusionList proxyBypass;
    @InjectView(R.id.wifi_proxy_input_fields) CardView proxyFields;
    @InjectView(R.id.proxy_static_fields) LinearLayout staticProxyFields;
    @InjectView(R.id.proxy_pac_fields) LinearLayout pacProxyFields;

    private boolean refreshingUI;
//    @InjectView(R.id.progress) RelativeLayout progress;

    /**
     * Create a new instance of WiFiApDetailFragment
     */
    public static WiFiApDetailFragment newInstance(APLNetworkId wifiNetworkId)
    {
        WiFiApDetailFragment instance = new WiFiApDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.SELECTED_AP_CONF_ARG, wifiNetworkId);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        App.getTraceUtils().startTrace(TAG, "onCreateView", Log.DEBUG);

        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.wifi_ap_preferences, container, false);
        ButterKnife.inject(this, v);

        proxySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if (!refreshingUI)
                {
                    proxySwitchClicked();
                }

            }
        });

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
        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        wifiNetworkId = getArguments().getParcelable(Constants.SELECTED_AP_CONF_ARG);
        if (wifiNetworkId != null)
        {
            selectedWiFiAP = App.getWifiNetworksManager().getConfiguration(wifiNetworkId);
        }

        if (selectedWiFiAP == null)
        {
            FragmentsUtils.goToMainActivity(getActivity());
        }

        refreshWifiUI();
        refreshProxyUI();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    public void proxySwitchClicked()
    {
//        progress.setVisibility(View.VISIBLE);

        if (proxySwitch.isChecked())
        {
            Timber.d("Set proxy settings = STATIC");
            selectedWiFiAP.setProxySetting(ProxySetting.STATIC);
        }
        else
        {
            Timber.d("Set proxy settings = NONE");
            selectedWiFiAP.setProxySetting(ProxySetting.NONE);
            selectedWiFiAP.setProxyHost(null);
            selectedWiFiAP.setProxyPort(0);
            selectedWiFiAP.setProxyExclusionString(null);

            App.getWifiNetworksManager().addSavingOperation(selectedWiFiAP);
        }

        refreshProxyUI();
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
            startActivityForResult(i, Requests.SELECT_PROXY_FOR_WIFI_NETWORK);
        }
        else
        {
            NoProxiesDefinedAlertDialog noProxiesDefinedAltertDialog = NoProxiesDefinedAlertDialog.newInstance();
            noProxiesDefinedAltertDialog.setTargetFragment(this, Requests.CREATE_NEW_PROXY);
            noProxiesDefinedAltertDialog.show(getFragmentManager(),"NoProxiesDefinedAlertDialog");
        }
    }

    public void refreshUI()
    {
        App.getTraceUtils().startTrace(TAG, "refreshUI", Log.DEBUG);

        if (selectedWiFiAP != null)
        {
            refreshWifiUI();
        }

        App.getTraceUtils().stopTrace(TAG, "refreshUI", Log.DEBUG);
    }

    private void refreshWifiUI()
    {
        refreshingUI = true;

        if (selectedWiFiAP != null)
        {
            wifiApHeader.setConfiguration(selectedWiFiAP);

            int selectedColor = R.color.grey_600;
            if (selectedWiFiAP.getLevel() != -1)
            {
                if (selectedWiFiAP.isActive())
                {
                    selectedColor = R.color.blue_500;
                }
                else
                {
                    selectedColor = R.color.green_500;
                }
            }

            wifiLayout.setBackgroundResource(selectedColor);

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null)
            {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null)
                {
                    actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(selectedColor)));
                }
            }
        }
        else
        {
            FragmentsUtils.goToMainActivity(getActivity());
        }


        refreshingUI = false;
    }

    private void refreshProxyUI()
    {
        refreshingUI = true;

        if (selectedWiFiAP != null)
        {
            if (selectedWiFiAP.getProxySetting() == ProxySetting.STATIC
                    || selectedWiFiAP.getProxySetting() == ProxySetting.PAC)
            {
                Timber.d("Set proxy switch = ON");
                proxySwitch.setChecked(true);
                proxySwitch.setText(R.string.status_proxy_enabled);

                proxySelector.setVisibility(View.VISIBLE);

                proxyType.setValue(selectedWiFiAP.getProxySetting().toString());

                proxyHost.setValue("");
                proxyPort.setValue("");
                proxyBypass.setExclusionString("");
                proxyPacUrl.setValue("");
                proxyFields.setVisibility(View.GONE);
                staticProxyFields.setVisibility(View.GONE);
                pacProxyFields.setVisibility(View.GONE);

                if (selectedWiFiAP.getProxySetting() == ProxySetting.STATIC)
                {
                    long proxyId = App.getDBManager().findProxy(selectedWiFiAP);
                    if (proxyId != -1)
                    {
                        ProxyEntity selectedProxy = App.getDBManager().getProxy(proxyId);
                        proxyHost.setValue(selectedProxy.getHost());
                        proxyPort.setValue(selectedProxy.getPort());
                        proxyBypass.setExclusionString(selectedProxy.getExclusion());

                        proxyFields.setVisibility(View.VISIBLE);
                        staticProxyFields.setVisibility(View.VISIBLE);
                    }
                }
                else if (selectedWiFiAP.getProxySetting() == ProxySetting.PAC)
                {
                    long pacId = App.getDBManager().findPac(selectedWiFiAP);
                    if (pacId != -1)
                    {
                        PacEntity selectedPac = App.getDBManager().getPac(pacId);
                        proxyPacUrl.setValue(selectedPac.getPacUriFile());

                        proxyFields.setVisibility(View.VISIBLE);
                        pacProxyFields.setVisibility(View.VISIBLE);
                    }
                }
            }
            else
            {
                Timber.d("Set proxy switch = OFF");
                proxySwitch.setChecked(false);
                proxySwitch.setText(R.string.status_proxy_disabled);

                proxySelector.setVisibility(View.GONE);
                proxyFields.setVisibility(View.GONE);
            }
        }
        else
        {
            FragmentsUtils.goToMainActivity(getActivity());
        }

        refreshingUI = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
//        inflater.inflate(R.menu.ap_wifi_details, menu);
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
        Timber.d("Received Activity resultCode: %d for requestCode: %d", resultCode, requestCode);

        switch (requestCode)
        {
            case Requests.CREATE_NEW_PROXY:
                Intent addNewProxyIntent = new Intent(getActivity(), ProxyDetailActivity.class);
                addNewProxyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                addNewProxyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addNewProxyIntent);
                break;

            case Requests.SELECT_PROXY_FOR_WIFI_NETWORK:

                if (selectedWiFiAP != null && data != null && data.hasExtra(Constants.SELECTED_PROXY_TYPE_ARG))
                {
                    Bundle args = data.getExtras();
                    if (resultCode == AppCompatActivity.RESULT_OK && args != null)
                    {
                        ProxySetting setting = (ProxySetting) args.get(Constants.SELECTED_PROXY_TYPE_ARG);

                        if (setting == ProxySetting.STATIC)
                        {
                            ProxyEntity proxyEntity = (ProxyEntity) args.get(Constants.SELECTED_PROXY_CONF_ARG);

                            selectedWiFiAP.setProxySetting(ProxySetting.STATIC);
                            selectedWiFiAP.setProxyHost(proxyEntity.getHost());
                            selectedWiFiAP.setProxyPort(proxyEntity.getPort());
                            selectedWiFiAP.setProxyExclusionString(proxyEntity.getExclusion());
                            selectedWiFiAP.setPacUriFile(Uri.EMPTY);
                        }
                        else if (setting == ProxySetting.PAC)
                        {
                            PacEntity pacEntity = (PacEntity) args.get(Constants.SELECTED_PAC_CONF_ARG);

                            selectedWiFiAP.setProxySetting(ProxySetting.PAC);
                            selectedWiFiAP.setPacUriFile(pacEntity.getPacUriFile());
                            selectedWiFiAP.setProxyHost("");
                            selectedWiFiAP.setProxyPort(0);
                            selectedWiFiAP.setProxyExclusionString("");
                        }

                        App.getWifiNetworksManager().addSavingOperation(selectedWiFiAP);
                    }
                }
                break;
        }
    }
}
