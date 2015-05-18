package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.ui.activities.AboutActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ChangeLogActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.IabActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BasePreferenceFragment;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest.BetaTestCommunityDialog;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.betatest.BetaTestDismissedDialog;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

public class HelpPrefsFragment extends BasePreferenceFragment
{
    private Preference whatsNewPref;
    private Preference changeLogPref;
    private Preference aboutPref;
    private Preference sendFeedbackPref;
    private Preference betaTestPref;
    private Preference appRatePref;
    private Preference shareApp;
    private Preference contactPref;
    private Preference donatePref;
//    private Preference aboutPref;


    public static HelpPrefsFragment newInstance(int sectionNumber)
    {
        HelpPrefsFragment fragment = new HelpPrefsFragment();

        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.help_preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        changeLogPref = findPreference("pref_full_changelog");
        changeLogPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                Intent i = new Intent(getActivity(), ChangeLogActivity.class);
                startActivity(i);
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

                Intent i = new Intent(getActivity(), AboutActivity.class);
                startActivity(i);
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

        donatePref = findPreference("pref_donate");
        donatePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                MaterialDialog.Builder donateBuilder = new MaterialDialog.Builder(getActivity());
                donateBuilder.positiveText(R.string.donate);
                donateBuilder.negativeText(R.string.cancel);
                donateBuilder.cancelable(true);
                donateBuilder.items(new String[]{"AAAA", "BBBB", "CCCCC"});
                donateBuilder.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                    {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        return true;
                    }
                });
                donateBuilder.callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        BaseActivity activity = (BaseActivity) getActivity();

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog)
                    {

                    }
                });

                MaterialDialog donateDialog = donateBuilder.build();
                donateDialog.show();
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

        if (App.getInstance().activeMarket != AndroidMarket.PLAY)
        {
            getPreferenceScreen().removePreference(betaTestPref);
            getPreferenceScreen().removePreference(donatePref);

        }

        return v;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (activity instanceof MasterActivity)
        {
            ((MasterActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private void showBetaTestDialog()
    {
        MaterialDialog dialog = UIUtils.getBetaTestDialog(getActivity());
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        MasterActivity master = (MasterActivity) getActivity();

        if (master != null && !master.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
//            inflater.inflate(R.menu.main, menu);
            master.restoreActionBar();
        }
    }
}
