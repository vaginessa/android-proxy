package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.AdvancedPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.MainAPPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyCheckerPrefsFragment;

public class ProxyPreferencesActivity extends Activity
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
	    
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MainAPPrefsFragment()).commit();
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
				getFragmentManager().beginTransaction().replace(android.R.id.content, new MainAPPrefsFragment()).commit();
				return true;

			case R.id.menu_proxy_status:
				return true;
			case R.id.menu_proxy_enabled:
			case R.id.menu_proxy_host:
			case R.id.menu_proxy_address:
			case R.id.menu_proxy_web_reach:
				getFragmentManager().beginTransaction().replace(android.R.id.content, new ProxyCheckerPrefsFragment()).commit();
				return true;

			case R.id.menu_about:
				getFragmentManager().beginTransaction().replace(android.R.id.content, new HelpPrefsFragment()).commit();
				return true;
			case R.id.menu_advanced:
				getFragmentManager().beginTransaction().replace(android.R.id.content, new AdvancedPrefsFragment()).commit();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	//	@Override
	//	public void onBuildHeaders(List<Header> target)
	//	{
	//		loadHeadersFromResource(R.xml.preferences_header, target);
	//	}
}
