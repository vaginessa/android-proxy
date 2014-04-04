package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by marco on 18/09/13.
 */
public class InstallationStatistics
{
    private static final String TAG = InstallationStatistics.class.getSimpleName();
    public long launchCount;
    public Date launhcFirstDate;

    public static void UpdateInstallationDetails(Context applicationContext)
    {
        SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        long launch_count = prefs.getLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, 0) + 1;
        editor.putLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, launch_count);

        long date_firstLaunch = prefs.getLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, 0);
        if (date_firstLaunch == 0)
        {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, date_firstLaunch);
        }

        editor.commit();
    }

    public static InstallationStatistics GetInstallationDetails(Context applicationContext)
    {
        InstallationStatistics details = new InstallationStatistics();
        SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);

        // Increment launch counter
        details.launchCount = prefs.getLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, 0);

        // Get date of first launch
        details.launhcFirstDate = new Date(prefs.getLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, 0));

        App.getLogger().a(TAG,details.toString());

        return details;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        DateFormat df = DateFormat.getDateTimeInstance();
        App.getLogger().a(TAG, String.format("App launched #%d times since %s", launchCount, df.format(launhcFirstDate)));
        return sb.toString();
    }
}
