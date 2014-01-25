package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.InputExclusionList;
import com.lechucksoftware.proxy.proxysettings.components.InputField;
import com.lechucksoftware.proxy.proxysettings.components.InputTags;
import com.lechucksoftware.proxy.proxysettings.components.WifiSignal;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.FragmentMode;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;
import com.shouldit.proxy.lib.utils.ProxyUtils;

import java.util.UUID;


public class WiFiApDetailFragment extends BaseFragment implements IBaseFragment
{
    public static final String TAG = "WiFiApDetailFragment";

    private ProxyConfiguration selectedWiFiAP;
    private ProxyEntity selectedProxy;

    private TextView wifiName;
    private TextView wifiStatus;
    private WifiSignal wifiSignal;
    private Switch proxySwitch;
    private TextView proxySelector;
    private ViewGroup wifiLayout;
    private InputField proxyHost;
    private InputField proxyPort;
    private InputField proxyBypass;
    private InputTags proxyTags;
    private UUID confId;

    /**
     * Create a new instance of WiFiApDetailFragment
     */
    public static WiFiApDetailFragment newInstance(UUID selectedId)
    {
        WiFiApDetailFragment instance = new WiFiApDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(Constants.SELECTED_AP_CONF_ARG, selectedId);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        confId = (UUID) getArguments().getSerializable(Constants.SELECTED_AP_CONF_ARG);
        selectedWiFiAP = ApplicationGlobals.getProxyManager().getConfiguration(confId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
//        LogWrapper.startTrace(TAG, "onCreateView", Log.DEBUG);

        View v = inflater.inflate(R.layout.wifi_ap_preferences, container, false);

//        LogWrapper.getPartial(TAG, "onCreateView", Log.DEBUG);

        wifiLayout = (ViewGroup) v.findViewById(R.id.wifi_layout);

        wifiLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActionsListFragment actionsListFragment = ActionsListFragment.newInstance(FragmentMode.DIALOG, selectedWiFiAP);
                actionsListFragment.show(getFragmentManager(), TAG);
            }
        });

        wifiName = (TextView) v.findViewById(R.id.wifi_name);
        wifiStatus = (TextView) v.findViewById(R.id.wifi_status);
        wifiSignal = (WifiSignal) v.findViewById(R.id.wifi_signal);
        proxySwitch = (Switch) v.findViewById(R.id.wifi_proxy_switch);
        proxySwitch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (proxySwitch.isChecked())
                {
                    selectedWiFiAP.setProxySetting(ProxySetting.STATIC);
                }
                else
                {
                    selectedWiFiAP.setProxySetting(ProxySetting.NONE);
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

        proxyHost = (InputField) v.findViewById(R.id.proxy_host);
        proxyHost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openProxyEditorDialog();
            }
        });


        proxyPort = (InputField) v.findViewById(R.id.proxy_port);
        proxyPort.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openProxyEditorDialog();
            }
        });

        proxyBypass = (InputField) v.findViewById(R.id.proxy_bypass);
        proxyBypass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openProxyEditorDialog();
            }
        });

        proxyTags = (InputTags) v.findViewById(R.id.proxy_tags);
        proxyTags.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openProxyEditorDialog();
            }
        });

//        LogWrapper.getPartial(TAG, "onCreateView", Log.DEBUG);

        refreshUI();

//        LogWrapper.stopTrace(TAG, "onCreateView", Log.DEBUG);
        return v;
    }

    private void openProxyEditorDialog()
    {
        ProxyDetailFragment dialog = ProxyDetailFragment.newInstance(selectedProxy);
        dialog.show(getFragmentManager(),TAG);
    }

    private void openProxySelectorDialog()
    {
        ProxyListFragment proxiesListFragment = ProxyListFragment.newInstance(FragmentMode.DIALOG, selectedWiFiAP);
        proxiesListFragment.show(getFragmentManager(), TAG);
    }

    private void getUIComponents()
    {
//
//            proxyTags = (TagsPreference) findPreference("pref_proxy_tags");
//            proxyTags.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
//            {
//                @Override
//                public boolean onPreferenceClick(Preference preference)
//                {
//                    if (selectedProxy != null)
//                    {
//                        TagsListFragment tagsListSelectorFragment = TagsListFragment.newInstance(selectedProxy);
//                        tagsListSelectorFragment.show(getFragmentManager(), TAG);
//                    }
//                    else
//                    {
//                        EventReportingUtils.sendException(new Exception("Found null selectedProxy"));
//                    }
//
//                    return true;
//                }
//            });
//
    }

    private void refreshVisibility()
    {
        int visibility;
        if (proxySwitch.isChecked())
        {
            proxySelector.setVisibility(View.VISIBLE);

            if (selectedProxy == null)
            {
                proxyHost.setVisibility(View.GONE);
                proxyPort.setVisibility(View.GONE);
                proxyBypass.setVisibility(View.GONE);
                proxyTags.setVisibility(View.GONE);
            }
            else
            {
                proxyHost.setVisibility(View.VISIBLE);
                proxyPort.setVisibility(View.VISIBLE);
                proxyBypass.setVisibility(View.VISIBLE);
                proxyTags.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            proxySelector.setVisibility(View.GONE);
            proxyHost.setVisibility(View.GONE);
            proxyPort.setVisibility(View.GONE);
            proxyBypass.setVisibility(View.GONE);
            proxyTags.setVisibility(View.GONE);
        }
    }

    private void saveConfiguration()
    {
        try
        {
            if (selectedWiFiAP != null && selectedWiFiAP.isValidConfiguration())
            {
                selectedWiFiAP.writeConfigurationToDevice();
            }
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
            showError(R.string.exception_apl_writeconfig_error_message);
        }
    }

    protected void showError(int error)
    {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.proxy_error)
                .setMessage(error)
                .setPositiveButton(R.string.proxy_error_dismiss, null)
                .show();
    }

    public void initUI()
    {

    }

    public void refreshUI()
    {
//        LogWrapper.startTrace(TAG, "refreshUI", Log.DEBUG);

        if (selectedWiFiAP.proxySetting == ProxySetting.STATIC)
        {
            proxySwitch.setChecked(true);
            refreshFieldsValues();
        }
        else
        {
            proxySwitch.setChecked(false);
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

        wifiName.setText(ProxyUtils.cleanUpSSID(selectedWiFiAP.getSSID()));
        wifiStatus.setText(selectedWiFiAP.toStatusString());
        wifiSignal.setConfiguration(selectedWiFiAP);

        refreshVisibility();

//        if (isVisible())
//        {
//            if (selectedAPConf != null
//                    && selectedAPConf.isValidConfiguration()
//                    && proxyEnablePref != null
//                    && proxyHostPref != null
//                    && proxyPortPref != null
//                    && proxyBypassPref != null)
//            {
//                long proxyId = ApplicationGlobals.getDBManager().findProxy(selectedAPConf.getProxyHost(), selectedAPConf.getProxyPort());
//                if (proxyId != -1)
//                {
//                    selectedProxy = ApplicationGlobals.getDBManager().getProxy(proxyId);
//                }
//                else
//                {
//                    selectedProxy = null;
//                }
//
//                proxyEnablePref.setEnabled(true);
//
//                if (selectedAPConf.proxySetting == ProxySetting.NONE || selectedAPConf.proxySetting == ProxySetting.UNASSIGNED)
//                {
//                    proxyEnablePref.setChecked(false);
//                }
//                else
//                {
//                    proxyEnablePref.setChecked(true);
//                }
//
//                String proxyHost = selectedAPConf.getProxyHost();
//                if (proxyHost == null || proxyHost.length() == 0)
//                {
//                    proxyHostPref.setSummary(getText(R.string.not_set));
//                }
//                else
//                {
//                    proxyHostPref.setSummary(proxyHost);
//                }
//                proxyHostPref.setText(proxyHost);
//
//                Integer proxyPort = selectedAPConf.getProxyPort();
//                if (proxyPort == null || proxyPort == 0)
//                {
//                    proxyPortPref.setSummary(R.string.not_set);
//                    proxyPortPref.setText(null);
//                }
//                else
//                {
//                    String proxyPortString = proxyPort.toString();
//                    proxyPortPref.setSummary(proxyPortString);
//                    proxyPortPref.setText(proxyPortString);
//                }
//
//                String bypassList = selectedAPConf.getProxyExclusionList();
//                if (bypassList == null || bypassList.equals(""))
//                {
//                    proxyBypassPref.setSummary(getText(R.string.not_set));
//                }
//                else
//                {
//                    proxyBypassPref.setSummary(bypassList);
//                }
//                proxyBypassPref.setText(bypassList);
//
//                proxyTags.setTags(selectedProxy);
//
//                // Refresh all the dependencies
//                refreshVisibility(proxyEnablePref.isChecked());
//
//                if (selectedAPConf.isCurrentNetwork())
//                {
//                    if (selectedAPConf.status != null)
//                    {
//                        if (selectedAPConf.status.getCheckingStatus() == CheckStatusValues.CHECKED)
//                        {
//                            ActionManager.getInstance().setStatus(StatusFragmentStates.CONNECTED, selectedAPConf.getAPConnectionStatus());
//                        }
//                        else
//                        {
//                            ActionManager.getInstance().setStatus(StatusFragmentStates.CHECKING);
//                        }
//                    }
//                    else
//                    {
//                        ActionManager.getInstance().setStatus(StatusFragmentStates.CHECKING);
//                    }
//                }
//                else if (selectedAPConf.ap.getLevel() > -1)
//                {
//                    ActionManager.getInstance().setStatus(StatusFragmentStates.CONNECT_TO, getResources().getString(R.string.connect_to_wifi_action, selectedAPConf.ap.ssid));
//                }
//                else
//                {
//                    ActionManager.getInstance().setStatus(StatusFragmentStates.NOT_AVAILABLE, selectedAPConf.getAPConnectionStatus());
//                }
//
//            }
//            else
//            {
//                if (APL.getWifiManager().isWifiEnabled())
//                {
////				apSelectorPref.setSummary(getResources().getString(R.string.no_ap_active));
//                }
//                else
//                {
////				apSelectorPref.setTitle(getResources().getString(R.string.wifi_disabled));
//                }
//
//                proxyEnablePref.setEnabled(false);
//            }
//        }

//        LogWrapper.stopTrace(TAG, "refreshUI", Log.DEBUG);
    }

    private void refreshFieldsValues()
    {
        long proxyId = ApplicationGlobals.getDBManager().findProxy(selectedWiFiAP.getProxyHost(), selectedWiFiAP.getProxyPort());
        if (proxyId != -1)
        {
            selectedProxy = ApplicationGlobals.getDBManager().getProxy(proxyId);
            proxyHost.setValue(selectedProxy.host);
            proxyPort.setValue(selectedProxy.port);

            String[] exclusionList = ProxyUtils.parseExclusionList(selectedProxy.exclusion);
            proxyBypass.setValue(TextUtils.join("\n",exclusionList));

            proxyTags.setTags(selectedProxy.getTags());
        }
        else
        {
            selectedProxy = null;
            proxyHost.setValue("");
            proxyPort.setValue("");
            proxyBypass.setValue("");
            proxyTags.setTags(null);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

//        if (APL.getWifiManager().isWifiEnabled()
//                && selectedAPConf != null
//                && selectedAPConf.ap != null)
//        {
////            actionBar.setTitle(selectedAPConf.ap.ssid);
////            ActionManager.getInstance().refreshUI();
//        }
//        else
//        {
//            NavigationUtils.GoToAccessPointListFragment(getFragmentManager());
//        }
    }
}
