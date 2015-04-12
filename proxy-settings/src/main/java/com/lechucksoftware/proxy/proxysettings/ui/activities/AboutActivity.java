package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.AboutFragment;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ChangeLogFragment;

import java.util.AbstractCollection;

public class AboutActivity extends BaseActivity
{
    public static String TAG = AboutActivity.class.getSimpleName();

    private static AboutActivity instance;

    public static AboutActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden

        instance = this;

        setContentView(R.layout.main_layout);

        FragmentManager fm = getSupportFragmentManager();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setTitle(getString(R.string.about));

        AboutFragment aboutFragment = AboutFragment.newInstance();

        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_container, aboutFragment).commit();
    }
}
