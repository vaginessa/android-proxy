package com.lechucksoftware.proxy.proxysettings.activities;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.lechucksoftware.proxy.proxysettings.R;

public class ProxyPreferencesActivityV11 extends PreferenceActivity
{
	public static ProxyPreferencesActivityV11 instance;

	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;

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
	}

	@Override
	public void onBuildHeaders(List<Header> target)
	{
		loadHeadersFromResource(R.xml.preferences_header, target);
	}
}