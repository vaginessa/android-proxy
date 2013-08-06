package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.*;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import com.lechucksoftware.proxy.proxysettings.ActionManager;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.MainActivity;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.shouldit.android.utils.lib.log.LogWrapper;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyUtils;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;


public class ProxyDetailsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
    public static ProxyDetailsFragment instance;
    public static final String TAG = "ProxyDetailsFragment";

    private PreferenceScreen authPrefScreen;
    private SwitchPreference proxyEnablePref;

    private EditTextPreference proxyHostPref;
    private EditTextPreference proxyPortPref;
    private EditTextPreference proxyBypassPref;

    /**
     * Create a new instance of ProxyDetailsFragment
     */
    public static ProxyDetailsFragment getInstance()
    {
        if (instance == null)
            instance = new ProxyDetailsFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preferences);

        instance = this;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getUIComponents();
        refreshUI();
//        selectAP();
    }

    private void getUIComponents()
    {
//		apSelectorPref = (ApSelectorDialogPreference) findPreference("pref_ap_selector_dialog");

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

                ApplicationGlobals.getSelectedConfiguration().writeConfigurationToDevice();
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
                ApplicationGlobals.getSelectedConfiguration().writeConfigurationToDevice();

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
                ApplicationGlobals.getSelectedConfiguration().writeConfigurationToDevice();

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
                ApplicationGlobals.getSelectedConfiguration().writeConfigurationToDevice();

                return true;
            }
        });

        authPrefScreen = (PreferenceScreen) findPreference("pref_proxy_authentication");
        if (authPrefScreen != null) getPreferenceScreen().removePreference(authPrefScreen);
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
            if (ApplicationGlobals.getSelectedConfiguration() != null && ApplicationGlobals.getSelectedConfiguration().isValidConfiguration())
            {
                proxyEnablePref.setEnabled(true);
                String apdesc = String.format("%s - %s", ProxyUtils.cleanUpSSID(ApplicationGlobals.getSelectedConfiguration().getSSID()), ApplicationGlobals.getSelectedConfiguration().getAPConnectionStatus());
//			apSelectorPref.setSummary(apdesc);

                if (ApplicationGlobals.getSelectedConfiguration().proxySetting == ProxySetting.NONE || ApplicationGlobals.getSelectedConfiguration().proxySetting == ProxySetting.UNASSIGNED)
                {
                    proxyEnablePref.setChecked(false);
                }
                else
                {
                    proxyEnablePref.setChecked(true);
                }

                String proxyHost = ApplicationGlobals.getSelectedConfiguration().getProxyHost();
                proxyHostPref.setText(proxyHost);
                if (proxyHost == null || proxyHost.length() == 0)
                {
                    proxyHostPref.setSummary(getText(R.string.not_set));
                }
                else
                {
                    proxyHostPref.setSummary(proxyHost);
                }

                Integer proxyPort = ApplicationGlobals.getSelectedConfiguration().getProxyPort();
                String proxyPortString;
                if (proxyPort == null || proxyPort == 0)
                {
                    proxyPortString = getText(R.string.not_set).toString();
                    proxyPortPref.setText(null);
                }
                else
                {
                    proxyPortString = proxyPort.toString();
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

                proxyPortPref.setSummary(proxyPortString);
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

            ActionManager.getInstance().refreshUI();
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
