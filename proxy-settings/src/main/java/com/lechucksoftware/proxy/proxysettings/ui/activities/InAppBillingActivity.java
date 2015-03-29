package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.lechucksoftware.proxy.proxysettings.IABManager;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.InAppBillingFragment;

import timber.log.Timber;

public class InAppBillingActivity extends ActionBarActivity
{
    private IABManager iabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_in_app_billing);

        iabManager = new IABManager(this);

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, InAppBillingFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (iabManager != null)
        {
            iabManager.close();
        }

        iabManager = null;
    }

    public void launchPurchase()
    {
        iabManager.launchPurchase(this, Constants.IAB_ITEM_SKU_TEST_PURCHASED, Requests.IAB_PURCHASE_PRO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Timber.d("Received activity result. Request: %d, Result: %d", requestCode, resultCode);

        if (iabManager != null && iabManager.handleActivityResult(requestCode, resultCode, data))
        {

        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_app_billing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
