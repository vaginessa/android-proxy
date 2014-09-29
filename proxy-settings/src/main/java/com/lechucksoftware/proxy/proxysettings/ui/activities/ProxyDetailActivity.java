package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncUpdateLinkedWiFiAP;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.UpdateLinkedWifiAPAlertDialog;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.ProxyDetailFragment;

import java.util.UUID;

public class ProxyDetailActivity extends BaseActivity
{
    public static String TAG = ProxyDetailActivity.class.getSimpleName();

    private static ProxyDetailActivity instance;
    private UUID cachedProxyId;
    private boolean saveEnabled;

    public static ProxyDetailActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);   // DO NOT LOAD savedInstanceState since onSaveInstanceState(Bundle) is not overridden

        instance = this;
        saveEnabled = true;

        setContentView(R.layout.main_layout);

        FragmentManager fm = getFragmentManager();

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

                ActionBar actionBar = getActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);

                ProxyEntity pe = (ProxyEntity) App.getCacheManager().get(cachedProxyId);
                actionBar.setTitle(pe.getHost());
                actionBar.setDisplayUseLogoEnabled(false);
            }
            else
            {
                App.getEventsReporter().sendException(new Exception("No selected proxy received into caller intent"));
            }
        }
        else
        {
            App.getEventsReporter().sendException(new Exception("No caller intent received"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.proxy_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem saveMenuItem = menu.findItem(R.id.menu_save);
        if (saveMenuItem != null)
        {
            saveMenuItem.setVisible(saveEnabled);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent mainIntent = new Intent(this, ProxyListActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                return true;

            case R.id.menu_save:
                saveProxy();
                return true;

            case R.id.menu_delete:
                deleteProxy();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void enableSave()
    {
        saveEnabled = true;
        invalidateOptionsMenu();
    }

    public void disableSave()
    {
        saveEnabled = false;
        invalidateOptionsMenu();
    }

    private void saveProxy()
    {
        try
        {
            ProxyEntity proxy = (ProxyEntity) App.getCacheManager().get(cachedProxyId);
            if (proxy.getInUse())
            {
                UpdateLinkedWifiAPAlertDialog updateDialog = UpdateLinkedWifiAPAlertDialog.newInstance();
                updateDialog.show(getFragmentManager(), "UpdateLinkedWifiAPAlertDialog");
            }
            else
            {
                App.getDBManager().upsertProxy(proxy);
                App.getCacheManager().release(cachedProxyId);
                finish();
            }
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }

    private void deleteProxy()
    {
        try
        {
            ProxyEntity proxy = (ProxyEntity) App.getCacheManager().get(cachedProxyId);
            if (proxy.getInUse())
            {
                UpdateLinkedWifiAPAlertDialog updateDialog = UpdateLinkedWifiAPAlertDialog.newInstance();
                updateDialog.show(getFragmentManager(), "UpdateLinkedWifiAPAlertDialog");
            }
            else
            {
                App.getDBManager().deleteProxy(proxy.getId());
                App.getCacheManager().release(cachedProxyId);
                finish();
            }
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments)
    {
        if (requestCode == Requests.UPDATE_LINKED_WIFI_AP)
        {
            ProxyEntity updated = (ProxyEntity) App.getCacheManager().get(cachedProxyId);
            ProxyEntity current = (ProxyEntity) App.getDBManager().getProxy(updated.getId());
            AsyncUpdateLinkedWiFiAP asyncUpdateLinkedWiFiAP = new AsyncUpdateLinkedWiFiAP(this, current, updated);
            asyncUpdateLinkedWiFiAP.execute();

            App.getDBManager().upsertProxy(updated);
            finish();
        }
    }
}
