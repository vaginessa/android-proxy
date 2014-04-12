package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;

/**
 * Created by Marco on 12/04/14.
 */
public class StartupAction
{
    private static String keyPrefix = "STARTUP_ACTION_";
    private Context context;

    public String preferenceKey;
    public StartupActionType actionType;
    public StartupActionStatus actionStatus;

    public Integer launchCondition;
    public Integer daysCondition;

    public StartupAction(Context ctx, StartupActionType type, StartupActionStatus status, Integer launch, Integer days)
    {
        context = ctx;
        actionType = type;
        actionStatus = status;
        preferenceKey = keyPrefix + actionType;

        launchCondition = launch;
        daysCondition = days;
    }

    public boolean canExecute(ApplicationStatistics statistics)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        StartupActionStatus status = StartupActionStatus.ParseInt(prefs.getInt(preferenceKey, StartupActionStatus.NOT_AVAILABLE.getValue()));

        Boolean result;

        switch (status)
        {
            case NOT_AVAILABLE:
            case POSTPONED:
                result = checkInstallationConditions(statistics,launchCondition, daysCondition);
                break;

            case REJECTED:
            case DONE:
            default:
                result = false;
        }

        return result;
    }

    public static Boolean checkInstallationConditions(ApplicationStatistics statistics, Integer launchCount, Integer daysCount)
    {
        Boolean result = false;

        if (checkLaunchCount(statistics, launchCount) &&
                checkElapsedDays(statistics, daysCount))
        {
            result = true;
        }

        return result;
    }

    public static Boolean checkLaunchCount(ApplicationStatistics statistics, Integer launchCount)
    {
        Boolean result = false;

        if (launchCount == null || statistics.LaunchCount >= launchCount)
        {
            result = true;
        }

        return result;
    }

    public static Boolean checkElapsedDays(ApplicationStatistics statistics, Integer daysCount)
    {
        Boolean result = false;

        if (daysCount == null || Utils.ElapsedNDays(statistics.LaunhcFirstDate, daysCount))
        {
            result = true;
        }

        return result;
    }
}
