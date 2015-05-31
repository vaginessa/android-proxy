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
    private final Context context;
    public long launchCount;
    public Date launhcFirstDate;
    public int majorVersion = BuildConfig.VERSION_CODE / 100;

    public ApplicationStatistics(Context context)
    {
        this.context = context;
        updateInstallationDetails();
    }

    public void updateInstallationDetails()
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
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

        getInstallationDetails();

        Timber.i(toString());
    }

    private void getInstallationDetails()
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);

        // Increment launch counter
        launchCount = prefs.getLong(Constants.PREFERENCES_APP_LAUNCH_COUNT, 0);

        // Get date of first launch
        launhcFirstDate = new Date(prefs.getLong(Constants.PREFERENCES_APP_DATE_FIRST_LAUNCH, 0));
    }

    @Override
    public String toString()
    {
        DateFormat df = DateFormat.getDateTimeInstance();
        String msg = String.format("App launched #%d times since %s", launchCount, df.format(launhcFirstDate));
        return msg;
    }
}
