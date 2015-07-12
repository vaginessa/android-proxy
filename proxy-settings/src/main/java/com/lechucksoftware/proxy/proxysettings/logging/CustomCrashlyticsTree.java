package com.lechucksoftware.proxy.proxysettings.logging;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;

import timber.log.Timber;

public class CustomCrashlyticsTree extends Timber.DebugTree
{
    private static int MIN_LOG_LEVEL = Log.DEBUG;

    @Override
    public void e(Throwable t, String message, Object... args)
    {
        e(message, args);
        Crashlytics.logException(t);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t)
    {
        if (priority >= MIN_LOG_LEVEL)
        {
            if (message.length() < 4000)
            {
                logMessage(priority, tag, message);
            }
            else
            {
                // It's rare that the message will be this large, so we're ok with the perf hit of splitting
                // and calling Log.println N times.  It's possible but unlikely that a single line will be
                // longer than 4000 characters: we're explicitly ignoring this case here.
                String[] lines = message.split("\n");
                for (String line : lines)
                {
                    logMessage(priority, tag, line);
                }
            }
        }
    }

    private void logMessage(int priority, String tag, String message)
    {
        if (priority >= MIN_LOG_LEVEL)
        {
            if (BuildConfig.DEBUG)
            {
                Crashlytics.log(priority, tag, message);
            }
            else
            {
                Crashlytics.log(String.format("%s: %s", tag, message));
            }
        }
    }
}
