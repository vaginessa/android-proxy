package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.ProxyConfiguration;

import java.util.UUID;


public class WifiAPDetailsFragment extends BaseFragment implements IBaseFragment
{
    public static WifiAPDetailsFragment instance;
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
    private ProxyConfiguration selectedAPConf;

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

        UUID confId = (UUID) getArguments().getSerializable(SELECTED_AP_CONF_ARG);
        selectedAPConf = ApplicationGlobals.getProxyManager().getConfiguration(confId);

//        addPreferencesFromResource(R.xml.proxy_enabled_preference);
//        addPreferencesFromResource(R.xml.proxy_settings_preferences);

        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.wifi_ap_preferences, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getUIComponents();
        refreshUI();
    }

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
//                    refreshDependencies(isChecked);
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

    private void refreshDependencies(Boolean isProxyEnabled)
    {
//        if (proxyHostPref != null) getPreferenceScreen().removePreference(proxyHostPref);
//
//
//        proxyHostPref.setEnabled(false);
//        proxyPortPref.setEnabled(false);
//        proxyBypassPref.setEnabled(false);
//        proxyTags.setEnabled(false);
    }

    private void saveConfiguration()
    {
        try
        {
            if (selectedAPConf != null && selectedAPConf.isValidConfiguration())
            {
                selectedAPConf.writeConfigurationToDevice();
            }
            //TODO: check here if the configuration has been saved
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
//                refreshDependencies(proxyEnablePref.isChecked());
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

        if (APL.getWifiManager().isWifiEnabled()
                && selectedAPConf != null
                && selectedAPConf.ap != null)
        {
            actionBar.setTitle(selectedAPConf.ap.ssid);

//            ActionManager.getInstance().refreshUI();
        }
        else
        {
            NavigationUtils.GoToAccessPointListFragment(getFragmentManager());
        }
    }
}
