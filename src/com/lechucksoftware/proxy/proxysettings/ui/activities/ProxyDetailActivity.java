package com.lechucksoftware.proxy.proxysettings.ui.activities;

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
import com.lechucksoftware.proxy.proxysettings.constants.BaseActions;
import com.lechucksoftware.proxy.proxysettings.constants.EventCategories;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncUpdateLinkedWiFiAP;
import com.lechucksoftware.proxy.proxysettings.ui.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.UpdateLinkedWifiAPAlertDialog;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyDetailFragment;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.UUID;

public class ProxyDetailActivity extends BaseActivity
{
    public static String TAG = ProxyDetailActivity.class.getSimpleName();

    private static ProxyDetailActivity instance;
    private UUID cachedProxyId;
    private View saveButton;
    private View cancelButton;

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
        saveButton = customActionBarView.findViewById(R.id.actionbar_done);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                EventReportingUtils.sendEvent(EventCategories.UI, BaseActions.BUTTON_PRESS, "save_proxy", null);
                saveConfiguration();
            }

        });

        cancelButton = customActionBarView.findViewById(R.id.actionbar_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {

                EventReportingUtils.sendEvent(EventCategories.UI, BaseActions.BUTTON_PRESS, "cancel_save_proxy", null);
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

    public void enableSave()
    {
        if (saveButton != null)
            saveButton.setEnabled(true);
    }

    public void disableSave()
    {
        if (saveButton != null)
            saveButton.setEnabled(false);
    }

    private void saveConfiguration()
    {
        try
        {
            ProxyEntity proxy = (ProxyEntity) ApplicationGlobals.getCacheManager().get(cachedProxyId);
            if (proxy.getInUse())
            {
                UpdateLinkedWifiAPAlertDialog updateDialog = UpdateLinkedWifiAPAlertDialog.newInstance();
                updateDialog.show(getFragmentManager(), "UpdateLinkedWifiAPAlertDialog");
            }
            else
            {
                ApplicationGlobals.getDBManager().upsertProxy(proxy);
                ApplicationGlobals.getCacheManager().release(cachedProxyId);
                finish();
            }
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
        }
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments)
    {
        if (requestCode == Requests.UPDATE_LINKED_WIFI_AP)
        {
            ProxyEntity updated = (ProxyEntity) ApplicationGlobals.getCacheManager().get(cachedProxyId);
            ProxyEntity current = (ProxyEntity) ApplicationGlobals.getDBManager().getProxy(updated.getId());
            AsyncUpdateLinkedWiFiAP asyncUpdateLinkedWiFiAP = new AsyncUpdateLinkedWiFiAP(this, current, updated);
            asyncUpdateLinkedWiFiAP.execute();

            ApplicationGlobals.getDBManager().upsertProxy(updated);
            finish();
        }
    }
}
