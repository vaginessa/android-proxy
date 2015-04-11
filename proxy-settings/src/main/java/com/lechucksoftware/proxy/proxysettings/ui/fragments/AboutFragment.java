package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Resources;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutFragment extends BaseDialogFragment
{
    public static AboutFragment instance;
    @InjectView(R.id.about_webview) WebView aboutWebView;

    public static AboutFragment newInstance()
    {
        AboutFragment instance = new AboutFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.about, container, false);
        ButterKnife.inject(this, v);

        aboutWebView.loadUrl(Resources.ABOUT);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
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
