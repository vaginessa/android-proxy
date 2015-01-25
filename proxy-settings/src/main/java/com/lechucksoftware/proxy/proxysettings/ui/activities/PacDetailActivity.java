package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.PacDetailFragment;

import timber.log.Timber;

public class PacDetailActivity extends BaseActivity
{
    public static String TAG = PacDetailActivity.class.getSimpleName();

    private static PacDetailActivity instance;
    private Long pacId;

    public static PacDetailActivity getInstance()
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

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        Intent callerIntent = getIntent();
        if (callerIntent != null)
        {
            Bundle extras = callerIntent.getExtras();
            PacDetailFragment detail = null;

            if (extras != null && extras.containsKey(Constants.SELECTED_PAC_CONF_ARG))
            {
                pacId = (Long) extras.getSerializable(Constants.SELECTED_PAC_CONF_ARG);
                detail = PacDetailFragment.newInstance(pacId);
                actionBar.setTitle(getString(R.string.edit_pac));
            }
            else
            {
                detail = PacDetailFragment.newInstance();
                actionBar.setTitle(getString(R.string.create_new_pac));
            }

            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment_container, detail).commit();
        }
        else
        {
            Timber.e(new Exception(),"No caller intent received");
        }
    }
}
