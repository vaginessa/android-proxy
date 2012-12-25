package com.lechucksoftware.proxy.proxysettings.activities;

import java.util.List;

import android.app.ActionBar.OnNavigationListener;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.SpinnerAdapter;

import com.lechucksoftware.proxy.proxysettings.R;

public class ProxyPreferencesActivity extends PreferenceActivity implements OnNavigationListener
{
	public static ProxyPreferencesActivity instance;

	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;

	private SpinnerAdapter mSpinnerAdapter;

	private OnNavigationListener mNavigationCallback;

	// static Preference appsFeedbackPref;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		ActionBar actionBar = getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		actionBar.setTitle("");
		
		
	}

	@Override
	public void onBuildHeaders(List<Header> target)
	{
		loadHeadersFromResource(R.xml.preferences_header, target);
	}

	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{
		// TODO Auto-generated method stub
		return false;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu)
	// {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.proxy_prefs_activity, menu);
	// return true;
	// }
}
