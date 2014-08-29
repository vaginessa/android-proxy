package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.Resources;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.ChangeLogDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.HtmlDialog;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

public class HelpPrefsFragment extends PreferenceFragment
{
    public static HelpPrefsFragment instance;
    private Preference whatsNewPref;
    private Preference changeLogPref;
    private Preference aboutPref;
    private Preference sendFeedbackPref;
    private Preference betaTestPref;
    private Preference appRatePref;
    private Preference shareApp;
    private Preference contactPref;
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

                ChangeLogDialog changeLogDialog = new ChangeLogDialog();
                changeLogDialog.show(getActivity().getFragmentManager(), "ChangelogHTMLDialog");
                return true;
            }
        });

        contactPref = findPreference("pref_contact");
        contactPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                // TODO:  Create ContacsDialog in order to let the user decide how to contact us
//                ContactsDialog contactDialog = ContactsDialog.newInstance();
                Utils.sendFeedbackMail(getActivity());
                return true;
            }
        });

        final String appVersionName = Utils.getAppVersionName(getActivity());
        aboutPref = findPreference("pref_about");
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                HtmlDialog aboutDialog = HtmlDialog.newInstance(getString(R.string.about),Resources.ABOUT);
                aboutDialog.setCancelable(true);
                aboutDialog.show(getFragmentManager(), "AboutDialog");
                return true;

            }
        });
        aboutPref.setSummary(appVersionName);

        appRatePref = findPreference("pref_issues");
        appRatePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shouldit/proxy-settings/issues/new")));
                return true;
            }
        });

        betaTestPref = findPreference("pref_betatest");
        betaTestPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                showBetaTestDialog();
                return true;
            }
        });

        if (App.getInstance().activeMarket != AndroidMarket.PLAY)
        {
            getPreferenceScreen().removePreference(betaTestPref);
        }

        appRatePref = findPreference("pref_rate_app");
        appRatePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                Utils.startMarketActivity(getActivity());

                return true;
            }
        });

//        shareApp = findPreference("pref_share_app");
//        shareApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
//        {
//            @Override
//            public boolean onPreferenceClick(Preference preference)
//            {
//
//
//            }
//        });


//        sendFeedbackPref = findPreference("pref_send_feedback");
//        sendFeedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
//        {
//            @Override
//            public boolean onPreferenceClick(Preference preference)
//            {
//                Intent i = new Intent(Intent.ACTION_SEND);
////i.setType("text/plain"); //use this line for testing in the emulator
//                i.setType("message/rfc822"); // use from live device
//                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@shouldit.net"});
//                i.putExtra(Intent.EXTRA_SUBJECT, "User feedback for Proxy Settings" + appVersionName);
//                startActivity(i);
//                return true;
//            }
//        });


        return v;
    }

    private void showBetaTestDialog()
    {
        AlertDialog dialog = UIUtils.getBetaTestDialog(getActivity());
        dialog.show();
    }
}
