package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;

import java.text.DateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by marco on 18/09/13.
 */
public class ApplicationStatistics
{
    private static final String TAG = ApplicationStatistics.class.getSimpleName();
    public long LaunchCount;
    public Date LaunhcFirstDate;
//    public int CrashesCount;

    public static void updateInstallationDetails(Context applicationContext)
    {
        SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = prefs.edit();

        long launch_count = prefs.getLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, 0) + 1;
        editor.putLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, launch_count);

        long date_firstLaunch = prefs.getLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, 0);
        if (date_firstLaunch == 0)
        {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, date_firstLaunch);
        }

        if (BuildConfig.DEBUG)
        {
            // During debug there is no need to mantain the total number of crashes
//            App.getEventsReporter().clearTotalCrashesNum();
        }

        editor.commit();
    }

    public static ApplicationStatistics getInstallationDetails(Context applicationContext)
    {
        ApplicationStatistics details = new ApplicationStatistics();
        SharedPreferences prefs = applicationContext.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);

        // Increment launch counter
        details.LaunchCount = prefs.getLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, 0);

        // Get date of first launch
        details.LaunhcFirstDate = new Date(prefs.getLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, 0));

//        details.CrashesCount = App.getEventsReporter().getCrashesCount();

        Timber.i(details.toString());

        return details;
    }

    @Override
    public String toString()
    {
        DateFormat df = DateFormat.getDateTimeInstance();
        String msg = String.format("App launched #%d times since %s", LaunchCount, df.format(LaunhcFirstDate));
        return msg;
    }
}
