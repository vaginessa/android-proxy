package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.AdvancedPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.MainAPPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyCheckerPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.shouldit.proxy.lib.APLConstants;

public class ProxyPreferencesActivity extends Activity
{
	public static final String TAG = "ProxyPreferencesActivity";

	public static ProxyPreferencesActivity instance;

	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;
	
	private MainAPPrefsFragment mainFragment;
	private HelpPrefsFragment helpFragment;
	private ProxyCheckerPrefsFragment checkFragment;
	private AdvancedPrefsFragment advFragment;
	

	public void showProgressDialog()
	{
		if (mProgressDialog != null)
			mProgressDialog.show();
	}

	public void dismissProgressDialog()
	{
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	public void setProgressDialogMessage(String message)
	{
		if (mProgressDialog != null)
			mProgressDialog.setMessage(message);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); 
		
		mainFragment = new MainAPPrefsFragment();
		checkFragment = new ProxyCheckerPrefsFragment();
		advFragment = new AdvancedPrefsFragment();
		helpFragment = new HelpPrefsFragment();
		
	    
		getFragmentManager().beginTransaction().replace(android.R.id.content, mainFragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.proxy_prefs_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getFragmentManager().beginTransaction().replace(android.R.id.content, mainFragment).commit();
				return true;

			case R.id.menu_proxy_status:
				return true;
			case R.id.menu_proxy_enabled:
			case R.id.menu_proxy_host:
			case R.id.menu_proxy_address:
			case R.id.menu_proxy_web_reach:
				getFragmentManager().beginTransaction().replace(android.R.id.content, checkFragment).commit();
				return true;

			case R.id.menu_about:
				getFragmentManager().beginTransaction().replace(android.R.id.content, helpFragment).commit();
				return true;
			case R.id.menu_advanced:
				getFragmentManager().beginTransaction().replace(android.R.id.content, advFragment).commit();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals(APLConstants.APL_UPDATED_PROXY_CONFIGURATION))
			{
				LogWrapper.d(TAG, "Received broadcast for updated proxy configuration");
				refreshFragments();
			}
			else if (action.equals(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK))
			{
				LogWrapper.d(TAG, "Received broadcast for partial update to proxy configuration");
				refreshFragments();
			}
		}
	};
	
	private void refreshFragments()
	{
		mainFragment.refreshUIComponents();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		// Start register the status receivers
		IntentFilter ifilt = new IntentFilter();
		ifilt.addAction(APLConstants.APL_UPDATED_PROXY_CONFIGURATION);
		ifilt.addAction(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
		registerReceiver(changeStatusReceiver, ifilt);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// Stop the registered status receivers
		unregisterReceiver(changeStatusReceiver);
	}
}
