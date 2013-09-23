package com.lechucksoftware.proxy.proxysettings.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.dialogs.BetaTestApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.dialogs.RateApplicationAlertDialog;
import com.lechucksoftware.proxy.proxysettings.utils.InstallationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

import java.util.Calendar;

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

		LogWrapper.d(TAG, "SDK Version");
		LogWrapper.d(TAG, "SDK Version: " + Build.VERSION.SDK_INT);

        if (showAppRate())
		{
			RateApplicationAlertDialog dialog = RateApplicationAlertDialog.newInstance();
            dialog.show(getSupportFragmentManager(), TAG);
		}
		else if (showAppBetaTest())
        {
            BetaTestApplicationAlertDialog dialog = BetaTestApplicationAlertDialog.newInstance();
            dialog.show(getSupportFragmentManager(), TAG);
        }
        else
		{
			GoToProxy();
		}
		
//		LogWrapper.d(TAG, "Finish onCreate");
	}

    public void onBackPressed() 
    {
    	LogWrapper.d(TAG, "Back Pressed");
    	return;
    }
    
    @Override
    protected void onStart() {
        super.onStart();
//        LogWrapper.d(TAG, "Start");
    }
    @Override
    protected void onResume() {
        super.onResume();
//        LogWrapper.d(TAG, "Resume");
    }
    @Override
    protected void onPause() {
        super.onPause();
//        LogWrapper.d(TAG, "Pause");
    }
    @Override
    protected void onStop() {
//    	LogWrapper.d(TAG, "Stop");
        super.onStop();
    }
    @Override
    protected void onDestroy() {
//    	LogWrapper.d(TAG, "Destroy");
        super.onDestroy();
    }

	public void GoToProxy()
	{
//		LogWrapper.d(TAG, "Starting MainActivity activity");
		
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
		finish();
	}

	public void dontDisplayAgainAppRate()
	{
		SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
		SharedPreferences.Editor editor = prefs.edit();

		if (editor != null)
		{
			editor.putBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, true);
			editor.commit();
		}
	}

    public boolean showAppRate()
    {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        if (prefs.getBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, false))
        {
            return false;
        }

        InstallationStatistics statistics = InstallationStatistics.GetInstallationDetails(getApplicationContext());

        // Wait at least N days before opening
        if (statistics.launchCount >= Constants.APPRATE_LAUNCHES_UNTIL_PROMPT)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(statistics.launhcFirstDate);
            c.add(Calendar.DATE, Constants.APPRATE_DAYS_UNTIL_PROMPT);

            if (System.currentTimeMillis() >= c.getTime().getTime())
            {
                return true;
            }
        }

        return false;
    }

    public void dontDisplayAgainBetaTest()
    {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        if (editor != null)
        {
            editor.putBoolean(Constants.PREFERENCES_APPRATE_DONT_SHOW_AGAIN, true);
            editor.commit();
        }
    }

    public boolean showAppBetaTest()
    {
        return true;

//        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
//        if (prefs.getBoolean(Constants.PREFERENCES_BETATEST_DONT_SHOW_AGAIN, false))
//        {
//            return false;
//        }
//
//        InstallationStatistics statistics = InstallationStatistics.GetInstallationDetails(getApplicationContext());
//
//        // Wait at least N days before opening
//        if (statistics.launchCount >= Constants.BETATEST_LAUNCHES_UNTIL_PROMPT)
//        {
//            Calendar c = Calendar.getInstance();
//            c.setTime(statistics.launhcFirstDate);
//            c.add(Calendar.DATE, Constants.BETATEST_DAYS_UNTIL_PROMPT);
//
//            if (System.currentTimeMillis() >= c.getTime().getTime())
//            {
//                return true;
//            }
//        }

//        return false;
    }
}