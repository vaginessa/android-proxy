package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.preferences.TagsPreference;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.shouldit.proxy.lib.log.LogWrapper;


public class ProxyDataDetailsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
    public static ProxyDataDetailsFragment instance;
    public static final String TAG = ProxyDataDetailsFragment.class.getSimpleName();

    private PreferenceScreen authPrefScreen;
    private SwitchPreference proxyEnablePref;

    private EditTextPreference proxyHostPref;
    private EditTextPreference proxyPortPref;
    private EditTextPreference proxyBypassPref;
    private TagsPreference proxyTags;

    /**
     * Create a new instance of WifiAPDetailsFragment
     */
    public static ProxyDataDetailsFragment getInstance()
    {
        if (instance == null)
            instance = new ProxyDataDetailsFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.proxy_description_preference);
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
//		apSelectorPref = (ApSelectorDialogPreference) findPreference("pref_ap_selector_dialog");

        proxyHostPref = (EditTextPreference) findPreference("pref_proxy_host");
        proxyHostPref.setDependency(null);
        proxyHostPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {

            public boolean onPreferenceChange(Preference preference, Object newValue)
            {

                return true;
            }
        });

        proxyPortPref = (EditTextPreference) findPreference("pref_proxy_port");
        proxyPortPref.setDependency(null);
        proxyPortPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                return true;
            }
        });

        proxyBypassPref = (EditTextPreference) findPreference("pref_proxy_bypass");
        proxyBypassPref.setDependency(null);
        proxyBypassPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {

            public boolean onPreferenceChange(Preference preference, Object newValue)
            {


                return true;
            }
        });

        proxyTags = (TagsPreference) findPreference("pref_proxy_tags");
        proxyTags.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                TagsListFragment tagsListSelectorFragment = TagsListFragment.newInstance(ApplicationGlobals.getSelectedProxy());
                tagsListSelectorFragment.show(getFragmentManager(), TAG);
                return true;
            }
        });

        authPrefScreen = (PreferenceScreen) findPreference("pref_proxy_authentication");
        if (authPrefScreen != null) getPreferenceScreen().removePreference(authPrefScreen);
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
            if (ApplicationGlobals.getSelectedProxy() != null)
            {
                DBProxy proxy = ApplicationGlobals.getSelectedProxy();

                String proxyHost = proxy.host;
                proxyHostPref.setText(proxyHost);
                if (proxyHost == null || proxyHost.length() == 0)
                {
                    proxyHostPref.setSummary(getText(R.string.not_set));
                }
                else
                {
                    proxyHostPref.setSummary(proxyHost);
                }

                Integer proxyPort = proxy.port;
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

                proxyPortPref.setSummary(proxyPortString);

                String bypassList = proxy.exclusion;
                if (bypassList == null || bypassList.equals(""))
                {
                    proxyBypassPref.setSummary(getText(R.string.not_set));
                }
                else
                {
                    proxyBypassPref.setSummary(bypassList);
                }

                proxyTags.setTags(proxy);
            }
            else
            {
                LogWrapper.e(TAG, "NOT VISIBLE");
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

        DBProxy selconf = ApplicationGlobals.getSelectedProxy();

        if (selconf != null)
        {
//            actionBar.setTitle(selconf.description);
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
