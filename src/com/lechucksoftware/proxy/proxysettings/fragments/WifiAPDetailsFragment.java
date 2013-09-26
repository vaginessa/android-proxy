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
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.shouldit.proxy.lib.*;
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
        if (ApplicationGlobals.getSelectedConfiguration() != null && ApplicationGlobals.getSelectedConfiguration().isValidConfiguration())
        {
            proxyEnablePref = (SwitchPreference) findPreference("pref_proxy_enabled");
            proxyEnablePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
            {
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    Boolean isChecked = (Boolean) newValue;

                    if (isChecked)
                    {
                        ApplicationGlobals.getSelectedConfiguration().proxySetting = ProxySetting.STATIC;
                    }
                    else
                    {
                        ApplicationGlobals.getSelectedConfiguration().proxySetting = ProxySetting.NONE;
                    }

                    saveConfiguration();
                    return true;
                }
            });

            proxyHostPref = (EditTextPreference) findPreference("pref_proxy_host");
            proxyHostPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
            {

                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    String proxyHost = (String) newValue;

                    ApplicationGlobals.getSelectedConfiguration().setProxyHost(proxyHost);
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

                    ApplicationGlobals.getSelectedConfiguration().setProxyPort(proxyPort);
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

                    ApplicationGlobals.getSelectedConfiguration().setProxyExclusionList(proxyExclusionList);
                    saveConfiguration();

                    return true;
                }
            });

            authPrefScreen = (PreferenceScreen) findPreference("pref_proxy_authentication");
            if (authPrefScreen != null) getPreferenceScreen().removePreference(authPrefScreen);
        }
    }

    private void saveConfiguration()
    {
        try
        {
            ApplicationGlobals.getSelectedConfiguration().writeConfigurationToDevice();
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
            if (ApplicationGlobals.getSelectedConfiguration() != null
                && ApplicationGlobals.getSelectedConfiguration().isValidConfiguration()
                && proxyEnablePref != null
                && proxyHostPref != null
                && proxyPortPref != null
                && proxyBypassPref != null)
            {
                proxyEnablePref.setEnabled(true);
//                String apdesc = String.format("%s - %s", ProxyUtils.cleanUpSSID(ApplicationGlobals.getSelectedConfiguration().getSSID()), ApplicationGlobals.getSelectedConfiguration().getAPConnectionStatus());
////			apSelectorPref.setSummary(apdesc);

                if (ApplicationGlobals.getSelectedConfiguration().proxySetting == ProxySetting.NONE || ApplicationGlobals.getSelectedConfiguration().proxySetting == ProxySetting.UNASSIGNED)
                {
                    proxyEnablePref.setChecked(false);
                }
                else
                {
                    proxyEnablePref.setChecked(true);
                }

                String proxyHost = ApplicationGlobals.getSelectedConfiguration().getProxyHost();
                if (proxyHost == null || proxyHost.length() == 0)
                {
                    proxyHostPref.setSummary(getText(R.string.not_set));
                }
                else
                {
                    proxyHostPref.setSummary(proxyHost);
                }
                proxyHostPref.setText(proxyHost);

                Integer proxyPort = ApplicationGlobals.getSelectedConfiguration().getProxyPort();
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

                String bypassList = ApplicationGlobals.getSelectedConfiguration().getProxyExclusionList();
                if (bypassList == null || bypassList.equals(""))
                {
                    proxyBypassPref.setSummary(getText(R.string.not_set));
                }
                else
                {
                    proxyBypassPref.setSummary(bypassList);
                }
                proxyBypassPref.setText(bypassList);

                if (ApplicationGlobals.getSelectedConfiguration().isCurrentNetwork())
                {
                    if (ApplicationGlobals.getSelectedConfiguration().status != null)
                    {
                        if (ApplicationGlobals.getSelectedConfiguration().status.getCheckingStatus() == CheckStatusValues.CHECKED)
                        {
                            ActionManager.getInstance().setStatus(Constants.StatusFragmentStates.CONNECTED, ApplicationGlobals.getSelectedConfiguration().getAPConnectionStatus());
                        }
                        else
                        {
                            ActionManager.getInstance().setStatus(Constants.StatusFragmentStates.CHECKING);
                        }
                    }
                    else
                    {
                        ActionManager.getInstance().setStatus(Constants.StatusFragmentStates.CHECKING);
                    }
                }
                else if (ApplicationGlobals.getSelectedConfiguration().ap.getLevel() > -1)
                {
                    ActionManager.getInstance().setStatus(Constants.StatusFragmentStates.CONNECT_TO, getResources().getString(R.string.connect_to_wifi_action, ApplicationGlobals.getSelectedConfiguration().ap.ssid));
                }
                else
                {
                    ActionManager.getInstance().setStatus(Constants.StatusFragmentStates.NOT_AVAILABLE, ApplicationGlobals.getSelectedConfiguration().getAPConnectionStatus());
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
