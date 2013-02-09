package com.lechucksoftware.proxy.proxysettings.activities;

import java.util.List;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.R;

public class ProxyPreferencesActivity extends PreferenceActivity
{
	public static final String TAG = "ProxyPreferencesActivity";

	public static ProxyPreferencesActivity instance;

	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;

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
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
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
	        	switchToHeader("com.lechucksoftware.proxy.proxysettings.fragments.MainAPPrefsFragment",null);
	            return true;
	        case R.id.menu_proxy_status:
	        	return true;
	        case R.id.menu_proxy_enabled:
	        case R.id.menu_proxy_host:
	        case R.id.menu_proxy_address:
	        case R.id.menu_proxy_web_reach:
	        	switchToHeader("com.lechucksoftware.proxy.proxysettings.fragments.ProxyCheckerPrefsFragment", null);
	        	return true;
	        	
	        case R.id.menu_about:
	        	switchToHeader("com.lechucksoftware.proxy.proxysettings.fragments.AboutPrefsFragment",null);
	        	return true;
	        default:
	        	switchToHeader("com.lechucksoftware.proxy.proxysettings.fragments.SettingsPrefsFragment",null);
	            return true;
	    }
	}

	@Override
	public void onBuildHeaders(List<Header> target)
	{
		loadHeadersFromResource(R.xml.preferences_header, target);
	}
}
