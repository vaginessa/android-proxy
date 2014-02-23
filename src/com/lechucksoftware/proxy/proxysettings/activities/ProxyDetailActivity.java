package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyDetailFragment;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.UUID;

public class ProxyDetailActivity extends BaseActivity
{
    public static String TAG = ProxyDetailActivity.class.getSimpleName();

    private static ProxyDetailActivity instance;
    private UUID cachedProxyId;

    public static ProxyDetailActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden

        instance = this;
        setContentView(R.layout.main_layout);

        FragmentManager fm = getFragmentManager();

        createCancelSaveActionBar();

        Intent callerIntent = getIntent();
        if (callerIntent != null)
        {
            Bundle extras = callerIntent.getExtras();
            ProxyDetailFragment detail = null;
            if (extras != null && extras.containsKey(Constants.SELECTED_PROXY_CONF_ARG))
            {
                cachedProxyId = (UUID) extras.getSerializable(Constants.SELECTED_PROXY_CONF_ARG);

                detail = ProxyDetailFragment.newInstance(cachedProxyId);

                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.fragment_container, detail).commit();
            }
            else
            {
                // TODO : only for DEBUG
                UIUtils.showError(getApplicationContext(), "DEBUG - No extras");
            }
        }
        else
        {
            // TODO : only for DEBUG
            UIUtils.showError(getApplicationContext(), "DEBUG - No caller intent");
        }
    }

    private void createCancelSaveActionBar()
    {
        final ActionBar actionBar = getActionBar();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View customActionBarView = inflater.inflate(R.layout.save_cancel, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // "Done"
                        saveConfiguration();
                        ApplicationGlobals.getCacheManager().release(cachedProxyId);
                        finish();
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // "Done"
                        ApplicationGlobals.getCacheManager().release(cachedProxyId);
                        finish();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                                    ActionBar.DISPLAY_SHOW_CUSTOM |
                                    ActionBar.DISPLAY_HOME_AS_UP |
                                    ActionBar.DISPLAY_SHOW_HOME |
                                    ActionBar.DISPLAY_SHOW_TITLE);


//        actionBar.setCustomView(customActionBarView);

        // Full size CANCEL-SAVE
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void saveConfiguration()
    {
        try
        {
            ProxyEntity proxy = (ProxyEntity) ApplicationGlobals.getCacheManager().get(cachedProxyId);
            ApplicationGlobals.getDBManager().upsertProxy(proxy);
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
        }
    }
}
