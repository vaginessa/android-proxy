package com.lechucksoftware.proxy.proxysettings.activities;

import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.VersionWarningAlertDialog;
import com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivityV11.MainPrefsFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class ProxySettingsCallerActivity extends FragmentActivity
{
	public static String TAG = "ProxySettingsCallerActivity";

	static final int DIALOG_ID_WARNING = 0;
	static final int DIALOG_ID_APP_RATE = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Log.d(TAG, "SDK Version");
		Log.d(TAG, "SDK Version: " + Build.VERSION.SDK_INT);

		if (AppLaunched())
		{
			showDialog(DIALOG_ID_APP_RATE);
		}
		else
		{
			GoToProxy();
		}
	}

	public void GoToProxy()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) // 12 =
																		// Honeycomb
																		// 3.1
		{
			VersionWarningAlertDialog newFragment = VersionWarningAlertDialog.newInstance();
			newFragment.show(getSupportFragmentManager(), TAG);
		}
		else
		{
			Log.d(TAG, "Starting ProxyPreferencesActivity activity");
			startActivity(new Intent(this, ProxyPreferencesActivity.class));
		}
	}

	public void DontDisplayAgain()
	{
		SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
		SharedPreferences.Editor editor = prefs.edit();

		if (editor != null)
		{
			editor.putBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, true);
			editor.commit();
		}
	}

	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog;
		switch (id)
		{
			case DIALOG_ID_APP_RATE:
				dialog = getRateDialog();
				break;

			default:
				dialog = null;
				Log.e(TAG, "onCreateDialog - Dialog ID not found");
				break;
		}

		return dialog;
	}

	public boolean AppLaunched()
	{
		SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
		if (prefs.getBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, false))
		{
			return false;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong(Constants.PREFERENCES_APPRATE_LAUNCH_COUNT, 0) + 1;
		editor.putLong(Constants.PREFERENCES_APPRATE_LAUNCH_COUNT, launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong(Constants.PREFERENCES_APPRATE_DATE_FIRST_LAUNCH, 0);
		if (date_firstLaunch == 0)
		{
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong(Constants.PREFERENCES_APPRATE_DATE_FIRST_LAUNCH, date_firstLaunch);
		}

		editor.commit();

		// Wait at least N days before opening
		if (launch_count >= Constants.APPRATE_LAUNCHES_UNTIL_PROMPT)
		{
			if (System.currentTimeMillis() >= date_firstLaunch + (Constants.APPRATE_DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))
			{
				return true;
			}
		}

		return false;
	}


	public Dialog getRateDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.app_rater_dialog_title)).setMessage(getResources().getString(R.string.app_rater_dialog_text)).setCancelable(false).setPositiveButton(getResources().getText(R.string.app_rater_dialog_button_rate), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				DontDisplayAgain();
				Log.d(TAG, "Starting Market activity");
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.lechucksoftware.proxy.proxysettings")));
				finish();
			}
		}).setNeutralButton(getResources().getText(R.string.app_rater_dialog_button_remind), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				GoToProxy();
			}
		}).setNegativeButton(getResources().getText(R.string.app_rater_dialog_button_nothanks), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				DontDisplayAgain();
				GoToProxy();
			}
		});

		AlertDialog alert = builder.create();
		return alert;

	}

}