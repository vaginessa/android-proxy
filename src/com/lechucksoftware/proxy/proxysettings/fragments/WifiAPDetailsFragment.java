package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import com.lechucksoftware.proxy.proxysettings.ActionManager;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.StatusFragmentStates;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.dialogs.ProxySelectDialog;
import com.lechucksoftware.proxy.proxysettings.preferences.TagsPreference;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.enums.CheckStatusValues;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;


public class WifiAPDetailsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
    public static WifiAPDetailsFragment instance;
    public static final String TAG = "WifiAPDetailsFragment";

    private PreferenceScreen authPrefScreen;
    private SwitchPreference proxyEnablePref;

    private EditTextPreference proxyHostPref;
    private EditTextPreference proxyPortPref;
    private EditTextPreference proxyBypassPref;
    private TagsPreference proxyTags;
    private Preference proxySelectionPref;

    /**
     * Create a new instance of WifiAPDetailsFragment
     */
    public static WifiAPDetailsFragment getInstance()
    {
        if (instance == null)
            instance = new WifiAPDetailsFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.proxy_enabled_preference);
        addPreferencesFromResource(R.xml.proxy_settings_preferences);

        instance = this;
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
        final ProxyConfiguration selectedConfiguration = ApplicationGlobals.getSelectedConfiguration();

        if (selectedConfiguration != null && selectedConfiguration.isValidConfiguration())
        {
            proxyEnablePref = (SwitchPreference) findPreference("pref_proxy_enabled");
            proxyEnablePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
            {
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    Boolean isChecked = (Boolean) newValue;

                    if (isChecked)
                    {
                        selectedConfiguration.proxySetting = ProxySetting.STATIC;
                    }
                    else
                    {
                        selectedConfiguration.proxySetting = ProxySetting.NONE;
                    }

                    refreshDependencies(isChecked);

                    saveConfiguration();
                    return true;
                }
            });

            proxySelectionPref = (Preference) findPreference("pref_proxy_selected");
            proxySelectionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    ProxySelectDialog dialog = ProxySelectDialog.newInstance();
                    dialog.show(getFragmentManager(), "ProxySelectDialog");
                    return true;
                }
            });


            proxyHostPref = (EditTextPreference) findPreference("pref_proxy_host");
            proxyHostPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
            {

                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    String proxyHost = (String) newValue;

                    selectedConfiguration.setProxyHost(proxyHost);
                    saveConfiguration();

                    return true;
                }
            });

            proxyPortPref = (EditTextPreference) findPreference("pref_proxy_port");
            proxyPortPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
            {
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    String portString = (String) newValue;
                    Integer proxyPort;

                    try
                    {
                        proxyPort = Integer.parseInt(portString);
                    }
                    catch (NumberFormatException ex)
                    {
                        proxyPort = 0; // Equivalent to NOT SET
                        showError(R.string.proxy_error_invalid_port);
                        return false;
                    }

                    if (proxyPort <= 0 || proxyPort > 0xFFFF)
                    {
                        showError(R.string.proxy_error_invalid_port);
                        return false;
                    }

                    selectedConfiguration.setProxyPort(proxyPort);
                    saveConfiguration();

                    return true;
                }
            });

            proxyBypassPref = (EditTextPreference) findPreference("pref_proxy_bypass");
            proxyBypassPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
            {

                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    String proxyExclusionList = (String) newValue;

                    selectedConfiguration.setProxyExclusionList(proxyExclusionList);
                    saveConfiguration();

                    return true;
                }
            });

            proxyTags = (TagsPreference) findPreference("pref_proxy_tags");
            proxyTags.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    TagsListSelectorFragment tagsListSelectorFragment = new TagsListSelectorFragment();
                    tagsListSelectorFragment.show(getFragmentManager(), TAG);
                    return true;
                }
            });

            authPrefScreen = (PreferenceScreen) findPreference("pref_proxy_authentication");
            if (authPrefScreen != null) getPreferenceScreen().removePreference(authPrefScreen);
        }
    }

    private void refreshDependencies(Boolean isChecked)
    {
        proxyHostPref.setEnabled(isChecked);
        proxyPortPref.setEnabled(isChecked);
        proxyBypassPref.setEnabled(isChecked);
        proxyTags.setEnabled(isChecked);
    }

    private void saveConfiguration()
    {
        try
        {
            final ProxyConfiguration selectedConfiguration = ApplicationGlobals.getSelectedConfiguration();
            if (selectedConfiguration != null && selectedConfiguration.isValidConfiguration())
            {
                selectedConfiguration.writeConfigurationToDevice();
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
        if (isVisible())
        {
            ProxyConfiguration selectedConf = ApplicationGlobals.getSelectedConfiguration();

            if (selectedConf != null
                    && selectedConf.isValidConfiguration()
                    && proxyEnablePref != null
                    && proxyHostPref != null
                    && proxyPortPref != null
                    && proxyBypassPref != null)
            {
                proxyEnablePref.setEnabled(true);

                if (selectedConf.proxySetting == ProxySetting.NONE || selectedConf.proxySetting == ProxySetting.UNASSIGNED)
                {
                    proxyEnablePref.setChecked(false);
                }
                else
                {
                    proxyEnablePref.setChecked(true);
                }

                String proxyHost = selectedConf.getProxyHost();
                if (proxyHost == null || proxyHost.length() == 0)
                {
                    proxyHostPref.setSummary(getText(R.string.not_set));
                }
                else
                {
                    proxyHostPref.setSummary(proxyHost);
                }
                proxyHostPref.setText(proxyHost);

                Integer proxyPort = selectedConf.getProxyPort();
                if (proxyPort == null || proxyPort == 0)
                {
                    proxyPortPref.setSummary(R.string.not_set);
                    proxyPortPref.setText(null);
                }
                else
                {
                    String proxyPortString = proxyPort.toString();
                    proxyPortPref.setSummary(proxyPortString);
                    proxyPortPref.setText(proxyPortString);
                }

                String bypassList = selectedConf.getProxyExclusionList();
                if (bypassList == null || bypassList.equals(""))
                {
                    proxyBypassPref.setSummary(getText(R.string.not_set));
                }
                else
                {
                    proxyBypassPref.setSummary(bypassList);
                }
                proxyBypassPref.setText(bypassList);


                long proxyId = ApplicationGlobals.getDBManager().findProxy(selectedConf.getProxyHost(), selectedConf.getProxyPort());
                if (proxyId != -1)
                {
                    DBProxy selectedProxy = ApplicationGlobals.getDBManager().getProxy(proxyId);
                    proxyTags.setTags(selectedProxy.getTags());
                }

                refreshDependencies(proxyEnablePref.isChecked());

                if (selectedConf.isCurrentNetwork())
                {
                    if (selectedConf.status != null)
                    {
                        if (selectedConf.status.getCheckingStatus() == CheckStatusValues.CHECKED)
                        {
                            ActionManager.getInstance().setStatus(StatusFragmentStates.CONNECTED, selectedConf.getAPConnectionStatus());
                        }
                        else
                        {
                            ActionManager.getInstance().setStatus(StatusFragmentStates.CHECKING);
                        }
                    }
                    else
                    {
                        ActionManager.getInstance().setStatus(StatusFragmentStates.CHECKING);
                    }
                }
                else if (selectedConf.ap.getLevel() > -1)
                {
                    ActionManager.getInstance().setStatus(StatusFragmentStates.CONNECT_TO, getResources().getString(R.string.connect_to_wifi_action, selectedConf.ap.ssid));
                }
                else
                {
                    ActionManager.getInstance().setStatus(StatusFragmentStates.NOT_AVAILABLE, selectedConf.getAPConnectionStatus());
                }

            }
            else
            {
                if (APL.getWifiManager().isWifiEnabled())
                {
//				apSelectorPref.setSummary(getResources().getString(R.string.no_ap_active));
                }
                else
                {
//				apSelectorPref.setTitle(getResources().getString(R.string.wifi_disabled));
                }

                proxyEnablePref.setEnabled(false);
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // Only persistant preferences
        LogWrapper.d(TAG, "Changed preference: " + key);

//		if (key == "pref name bla bla")
//		{}
    }


    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        ProxyConfiguration selconf = ApplicationGlobals.getSelectedConfiguration();

        if (APL.getWifiManager().isWifiEnabled()
                && selconf != null
                && selconf.ap != null)
        {
            actionBar.setTitle(ApplicationGlobals.getSelectedConfiguration().ap.ssid);

//            ActionManager.getInstance().refreshUI();
        }
        else
        {
            NavigationUtils.GoToAccessPointListFragment(getFragmentManager());
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
