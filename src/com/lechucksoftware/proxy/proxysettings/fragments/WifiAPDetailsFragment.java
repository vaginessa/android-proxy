package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.InputField;
import com.lechucksoftware.proxy.proxysettings.components.InputTags;
import com.lechucksoftware.proxy.proxysettings.components.WifiSignal;
import com.lechucksoftware.proxy.proxysettings.constants.FragmentMode;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.enums.CheckStatusValues;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;
import com.shouldit.proxy.lib.utils.ProxyUtils;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.UUID;


public class WifiAPDetailsFragment extends BaseFragment implements IBaseFragment
{
    public static final String TAG = "WifiAPDetailsFragment";

//    private PreferenceScreen authPrefScreen;
//    private SwitchPreference proxyEnablePref;
//
//    private EditTextPreference proxyHostPref;
//    private EditTextPreference proxyPortPref;
//    private EditTextPreference proxyBypassPref;
//    private TagsPreference proxyTags;
//    private Preference proxySelectionPref;
    private ProxyEntity selectedProxy;

    // Arguments
    private static final String SELECTED_AP_CONF_ARG = "SELECTED_AP_CONF_ARG";
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
     * Create a new instance of WifiAPDetailsFragment
     */
    public static WifiAPDetailsFragment newInstance(ProxyConfiguration selectedConf)
    {
        WifiAPDetailsFragment instance = new WifiAPDetailsFragment();

        Bundle args = new Bundle();
        args.putSerializable(SELECTED_AP_CONF_ARG, selectedConf.id);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        confId = (UUID) getArguments().getSerializable(SELECTED_AP_CONF_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.wifi_ap_preferences, container, false);

        wifiLayout = (ViewGroup) v.findViewById(R.id.wifi_layout);

        wifiLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ProxyConfiguration selectedAPConf = ApplicationGlobals.getProxyManager().getConfiguration(confId);
                ActionsListFragment actionsListFragment = ActionsListFragment.newInstance(FragmentMode.DIALOG, selectedAPConf);
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
                    ProxyConfiguration selectedAPConf = ApplicationGlobals.getProxyManager().getConfiguration(confId);
                    selectedAPConf.setProxySetting(ProxySetting.STATIC);
                    saveConfiguration();
                }
                else
                {
                    ProxyConfiguration selectedAPConf = ApplicationGlobals.getProxyManager().getConfiguration(confId);
                    selectedAPConf.setProxySetting(ProxySetting.NONE);
                    saveConfiguration();
                }

                refreshUI();
            }
        });

        proxySelector = (TextView) v.findViewById(R.id.proxy_selector);
        proxySelector.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ProxyConfiguration selectedAPConf = ApplicationGlobals.getProxyManager().getConfiguration(confId);
                ProxiesListFragment proxiesListFragment = ProxiesListFragment.newInstance(FragmentMode.DIALOG, selectedAPConf);
                proxiesListFragment.show(getFragmentManager(), TAG);
            }
        });

        proxyHost = (InputField) v.findViewById(R.id.proxy_host);
        proxyHost.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {
                LogWrapper.d(TAG,"beforeTextChanged " + charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {
                LogWrapper.d(TAG,"onTextChanged " + charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                LogWrapper.d(TAG,"afterTextChanged " + editable.toString());
            }
        });

        proxyPort = (InputField) v.findViewById(R.id.proxy_port);
        proxyBypass = (InputField) v.findViewById(R.id.proxy_bypass);
        proxyTags = (InputTags) v.findViewById(R.id.proxy_tags);

        refreshUI();

        return v;
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState)
//    {
//        super.onViewCreated(view, savedInstanceState);
//        refreshUI();
//    }

    private void getUIComponents()
    {


//        if (selectedAPConf != null && selectedAPConf.isValidConfiguration())
//        {
//            proxyEnablePref = (SwitchPreference) findPreference("pref_proxy_enabled");
//            proxyEnablePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
//            {
//                public boolean onPreferenceChange(Preference preference, Object newValue)
//                {
//                    Boolean isChecked = (Boolean) newValue;
//
//                    if (isChecked)
//                    {
//                        selectedAPConf.proxySetting = ProxySetting.STATIC;
//                    }
//                    else
//                    {
//                        selectedAPConf.proxySetting = ProxySetting.NONE;
//                    }
//
//                    refreshVisibility(isChecked);
//
//                    saveConfiguration();
//                    return true;
//                }
//            });
//
//            proxySelectionPref = (Preference) findPreference("pref_proxy_selected");
//            proxySelectionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
//            {
//                @Override
//                public boolean onPreferenceClick(Preference preference)
//                {
//
//                    ProxiesListFragment proxiesListFragment = ProxiesListFragment.newInstance(FragmentMode.DIALOG, selectedAPConf);
//                    proxiesListFragment.show(getFragmentManager(), TAG);
//                    return true;
//                }
//            });
//
//
//            proxyHostPref = (EditTextPreference) findPreference("pref_proxy_host");
//            proxyHostPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
//            {
//
//                public boolean onPreferenceChange(Preference preference, Object newValue)
//                {
//                    String proxyHost = (String) newValue;
//
//                    selectedAPConf.setProxyHost(proxyHost);
//                    saveConfiguration();
//
//                    return true;
//                }
//            });
//
//            proxyPortPref = (EditTextPreference) findPreference("pref_proxy_port");
//            proxyPortPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
//            {
//                public boolean onPreferenceChange(Preference preference, Object newValue)
//                {
//                    String portString = (String) newValue;
//                    Integer proxyPort;
//
//                    try
//                    {
//                        proxyPort = Integer.parseInt(portString);
//                    }
//                    catch (NumberFormatException ex)
//                    {
//                        proxyPort = 0; // Equivalent to NOT SET
//                        showError(R.string.proxy_error_invalid_port);
//                        return false;
//                    }
//
//                    if (proxyPort <= 0 || proxyPort > 0xFFFF)
//                    {
//                        showError(R.string.proxy_error_invalid_port);
//                        return false;
//                    }
//
//                    selectedAPConf.setProxyPort(proxyPort);
//                    saveConfiguration();
//
//                    return true;
//                }
//            });
//
//            proxyBypassPref = (EditTextPreference) findPreference("pref_proxy_bypass");
//            proxyBypassPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
//            {
//
//                public boolean onPreferenceChange(Preference preference, Object newValue)
//                {
//                    String proxyExclusionList = (String) newValue;
//
//                    selectedAPConf.setProxyExclusionList(proxyExclusionList);
//                    saveConfiguration();
//
//                    return true;
//                }
//            });
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
//                        BugReportingUtils.sendException(new Exception("Found null selectedProxy"));
//                    }
//
//                    return true;
//                }
//            });
//
//            authPrefScreen = (PreferenceScreen) findPreference("pref_proxy_authentication");
//            if (authPrefScreen != null) getPreferenceScreen().removePreference(authPrefScreen);
//        }
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
            ProxyConfiguration selectedAPConf = ApplicationGlobals.getProxyManager().getConfiguration(confId);
            if (selectedAPConf != null && selectedAPConf.isValidConfiguration())
            {
                selectedAPConf.writeConfigurationToDevice();
            }
        }
        catch (Exception e)
        {
            BugReportingUtils.sendException(e);
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

    public void refreshUI()
    {
        ProxyConfiguration selectedAPConf = ApplicationGlobals.getProxyManager().getConfiguration(confId);

        if (selectedAPConf.proxySetting == ProxySetting.STATIC)
        {
            proxySwitch.setChecked(true);

            long proxyId = ApplicationGlobals.getDBManager().findProxy(selectedAPConf.getProxyHost(), selectedAPConf.getProxyPort());
            if (proxyId != -1)
            {
                selectedProxy = ApplicationGlobals.getDBManager().getProxy(proxyId);
                proxyHost.setValue(selectedProxy.host);
                proxyPort.setValue(selectedProxy.port);
                proxyBypass.setValue(selectedProxy.exclusion);
            }
            else
            {
                selectedProxy = null;
                proxyHost.setValue("");
                proxyPort.setValue("");
                proxyBypass.setValue("");
            }
        }
        else
        {
            proxySwitch.setChecked(false);
        }

        if (selectedAPConf.ap.getLevel() == -1)
        {
            wifiLayout.setBackgroundResource(R.color.DarkGrey);
        }
        else
        {
            if (selectedAPConf.isCurrentNetwork())
            {
                wifiLayout.setBackgroundResource(R.color.Holo_Blue_Dark);
            }
            else
            {
                wifiLayout.setBackgroundResource(R.color.Holo_Green_Dark);
            }
        }

        wifiName.setText(ProxyUtils.cleanUpSSID(selectedAPConf.getSSID()));
        wifiStatus.setText(selectedAPConf.toStatusString());
        wifiSignal.setConfiguration(selectedAPConf);

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
