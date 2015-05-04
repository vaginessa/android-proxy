package com.lechucksoftware.proxy.proxysettings.logging;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class CustomCrashlyticsTree implements Timber.TaggedTree
{
    private static final ThreadLocal<String> NEXT_TAG = new ThreadLocal<String>();
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$");
    private static int MIN_LOG_LEVEL = Log.DEBUG;

    @Override
    public void tag(String tag)
    {
        NEXT_TAG.set(tag);
    }

    private static String createTag()
    {
        String tag = NEXT_TAG.get();
        if (tag != null)
        {
            NEXT_TAG.remove();
            return tag;
        }

        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length < 6)
        {
            throw new IllegalStateException("Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }

        tag = stackTrace[5].getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);

        if (m.find())
        {
            tag = m.replaceAll("");
        }

        return tag.substring(tag.lastIndexOf('.') + 1);
    }

    static String formatString(String message, Object... args)
    {
        // If no varargs are supplied, treat it as a request to log the string without formatting.
        return args.length == 0 ? message : String.format(message, args);
    }

    @Override
    public void v(String message, Object... args)
    {
        throwShade(Log.VERBOSE, formatString(message, args), null);
    }

    @Override
    public void v(Throwable t, String message, Object... args)
    {
        throwShade(Log.VERBOSE, formatString(message, args), t);
    }

    @Override
    public void d(String message, Object... args)
    {
        throwShade(Log.DEBUG, formatString(message, args), null);
    }

    @Override
    public void d(Throwable t, String message, Object... args)
    {
        throwShade(Log.DEBUG, formatString(message, args), t);
    }

    @Override
    public void i(String message, Object... args)
    {
        throwShade(Log.INFO, formatString(message, args), null);
    }

    @Override
    public void i(Throwable t, String message, Object... args)
    {
        throwShade(Log.INFO, formatString(message, args), t);
    }

    @Override
    public void w(String message, Object... args)
    {
        throwShade(Log.WARN, formatString(message, args), null);
    }

    @Override
    public void w(Throwable t, String message, Object... args)
    {
        throwShade(Log.WARN, formatString(message, args), t);
    }

    @Override
    public void e(String message, Object... args)
    {
        throwShade(Log.ERROR, formatString(message, args), null);
    }

    @Override
    public void e(Throwable t, String message, Object... args)
    {
        throwShade(Log.ERROR, formatString(message, args), t);
        Crashlytics.logException(t);
    }

    private void logMessage(int priority, String tag, String message)
    {
        if (priority >= MIN_LOG_LEVEL)
        {
            if (BuildConfig.DEBUG)
            {
                message = message.replaceAll(System.getProperty("line.separator"),". ");
                Crashlytics.log(priority, tag, message);
            }
            else
            {
                Crashlytics.log(String.format("%s: %s", tag, message));
            }
        }
    }

    private void throwShade(int priority, String message, Throwable t)
    {
        if (message == null || message.length() == 0)
        {
            if (t != null)
            {
                message = Log.getStackTraceString(t);
            }
            else
            {
                // Swallow message if it's null and there's no throwable.
                return;
            }
        }
        else if (t != null)
        {
            message += "\n" + Log.getStackTraceString(t);
        }

        String tag = createTag();
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
