package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

import butterknife.ButterKnife;
import butterknife.Bind;

public class AboutFragment extends BaseDialogFragment
{
    public static AboutFragment instance;

    @Bind(R.id.about_app_version) TextView aboutAppVersionTxt;
    @Bind(R.id.about_find_source_code) TextView findSourceTxt;
    @Bind(R.id.about_documentation) TextView docsTxt;
    @Bind(R.id.about_open_source_licenses) TextView licensesTxt;


    public static AboutFragment newInstance()
    {
        AboutFragment instance = new AboutFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.about, container, false);
        ButterKnife.bind(this, v);

        aboutAppVersionTxt.setText(BuildConfig.VERSION_NAME);
        findSourceTxt.setMovementMethod(LinkMovementMethod.getInstance());
        docsTxt.setMovementMethod(LinkMovementMethod.getInstance());
        licensesTxt.setMovementMethod(LinkMovementMethod.getInstance());

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.empty, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent mainIntent = new Intent(getActivity(), MasterActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
