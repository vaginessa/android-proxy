package com.shouldit.proxy.lib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogWrapper
{
    /**
     * ANDROID LOG LEVELS
     * <p/>
     * VERBOSE	Constant Value: 2 (0x00000002)
     * DEBUG	Constant Value: 3 (0x00000003)
     * INFO	Constant Value: 4 (0x00000004)
     * WARN	Constant Value: 5 (0x00000005)
     * ERROR	Constant Value: 6 (0x00000006)
     * ASSERT	Constant Value: 7 (0x00000007)
     */

//	private static int mLogLevel = Integer.MAX_VALUE;
    private static int mLogLevel = Log.VERBOSE;
    private static Map<String, Date> startTraces;

    public static void d(String tag, String msg)
    {
        if (BuildConfig.DEBUG && mLogLevel <= Log.DEBUG)
            Log.d(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (BuildConfig.DEBUG && mLogLevel <= Log.VERBOSE)
            Log.v(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        if (BuildConfig.DEBUG && mLogLevel <= Log.ERROR)
            Log.e(tag, msg);
    }

    public static void i(String tag, String msg)
    {
        if (BuildConfig.DEBUG && mLogLevel <= Log.INFO)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg)
    {
        if (BuildConfig.DEBUG && mLogLevel <= Log.WARN)
            Log.w(tag, msg);
    }

    public static void a(String tag, String msg)
    {
        if (BuildConfig.DEBUG && mLogLevel <= Log.ASSERT)
            Log.println(Log.ASSERT, tag, msg);
    }

    private static void log(String tag, String msg, int logLevel)
    {
        switch (logLevel)
        {
            case Log.DEBUG:
                d(tag, msg);
                break;
            case Log.ERROR:
                e(tag, msg);
                break;
            case Log.INFO:
                i(tag, msg);
                break;
            case Log.ASSERT:
                a(tag, msg);
                break;
            case Log.WARN:
                w(tag, msg);
                break;
            default:
                v(tag, msg);
                break;
        }
    }

    public static void startTrace(String tag, String msg, int logLevel)
    {
        startTrace(tag, msg, logLevel, false);
    }

    public static void startTrace(String tag, String msg, int logLevel, boolean showStart)
    {
        if (startTraces == null)
        {
            startTraces = new ConcurrentHashMap<String, Date>();
        }

        Date now = new Date();
        DateFormat df = DateFormat.getDateTimeInstance();
        if (showStart)
        {
            log(tag, "START " + msg + " ################## " + df.format(now) + " #####################################################################", logLevel);
        }

        synchronized (startTraces)
        {
            startTraces.put(msg, now);
        }
    }

    public static void stopTrace(String tag, String key, int logLevel)
    {
        stopTrace(tag, key, "", logLevel);
    }

    public static void stopTrace(String tag, String key, String msg, int logLevel)
    {
        synchronized (startTraces)
        {
            if (startTraces != null && startTraces.containsKey(key))
            {
                Date start = startTraces.remove(key);
                Date now = new Date();
                long diff = now.getTime() - start.getTime();
                log(tag, "FINISH " + key + " " + msg + " ################## " + diff + " msec #####################################################################", logLevel);
            }

//        else
//        {
//            DateFormat df = DateFormat.getDateTimeInstance();
//            log(tag, msg + " ################## " +  df.format(new Date()) + " #####################################################################", logLevel);
//        }
        }
    }

    public static void logIntent(String tag, String msg, Intent intent, int logLevel)
    {
        logIntent(tag, msg, intent, logLevel, false);
    }

    public static void logIntent(String tag, Intent intent, int logLevel)
    {
        logIntent(tag, null, intent, logLevel, false);
    }

    public static void logIntent(String tag, Intent intent, int logLevel, boolean logExtras)
    {
        logIntent(tag, null, intent, logLevel, logExtras);
    }

    public static void logIntent(String tag, String msg, Intent intent, int logLevel, boolean logExtras)
    {
        StringBuilder sb = new StringBuilder();

        if (msg != null)
            sb.append(msg + intent.toString());
        else
            sb.append("LOG Intent: " + intent.toString());

        if (intent.getAction() != null)
            sb.append(intent.getAction() + " ");
        if (intent.getDataString() != null)
            sb.append(intent.getDataString() + " ");

        if (logExtras)
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                for (String key : extras.keySet())
                {
                    String extra = String.valueOf(extras.get(key));
                    sb.append("EXTRA [\"" + key + "\"] : " + extra + " ");
                }
            }
        }

        log(tag, sb.toString(), logLevel);
    }


}
