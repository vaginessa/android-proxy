package com.lechucksoftware.proxy.proxysettings.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lechucksoftware.proxy.proxysettings.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.lechucksoftware.proxy.proxysettings.dialogs.AboutDialog;
import com.lechucksoftware.proxy.proxysettings.utils.ChangeLogDialog;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

public class HelpPrefsFragment extends PreferenceFragment
{
    public static HelpPrefsFragment instance;
    private Preference whatsNewPref;
    private Preference changeLogPref;
    private AboutDialog aboutPref;
    private Preference sendFeedbackPref;
//    private Preference aboutPref;

    public static HelpPrefsFragment getInstance()
    {
        if (instance == null)
            instance = new HelpPrefsFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.help_preferences);

        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        changeLogPref = findPreference("pref_full_changelog");
        changeLogPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                ChangeLogDialog cld = new ChangeLogDialog(getActivity());
                cld.show();
                return true;
            }
        });

        aboutPref = (AboutDialog) findPreference("pref_about");

        PackageInfo pi = Utils.getAppInfo(getActivity());
        final String appVersionName;
        if (pi != null)
        {
            appVersionName = getResources().getString(R.string.app_versionname, pi.versionName);
            aboutPref.setSummary(appVersionName);
        }
        else
        {
            appVersionName = "";
        }

        sendFeedbackPref = findPreference("pref_send_feedback");
        sendFeedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Intent i = new Intent(Intent.ACTION_SEND);
//i.setType("text/plain"); //use this line for testing in the emulator
                i.setType("message/rfc822"); // use from live device
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@shouldit.net"});
                i.putExtra(Intent.EXTRA_SUBJECT, "User feedback for Proxy Settings" + appVersionName);
                startActivity(i);
                return true;
            }
        });


        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

}
