package be.shouldit.proxy.lib.log;

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

//	private int mLogLevel = Integer.MAX_VALUE;
    private int mLogLevel = Integer.MIN_VALUE;

    public int getLogLevel()
    {
        return mLogLevel;
    }

    public LogWrapper(int logLevel)
    {
        mLogLevel = logLevel;
    }

    private Map<String, TraceDate> startTraces;

    public void d(String tag, String msg)
    {
        if (mLogLevel <= Log.DEBUG)
            Log.d(tag, msg);
    }

    public void v(String tag, String msg)
    {
        if (mLogLevel <= Log.VERBOSE)
            Log.v(tag, msg);
    }

    public void e(String tag, String msg)
    {
        if (mLogLevel <= Log.ERROR)
            Log.e(tag, msg);
    }

    public void i(String tag, String msg)
    {
        if (mLogLevel <= Log.INFO)
            Log.i(tag, msg);
    }

    public void w(String tag, String msg)
    {
        if (mLogLevel <= Log.WARN)
            Log.w(tag, msg);
    }

    public void a(String tag, String msg)
    {
        if (mLogLevel <= Log.ASSERT)
            Log.println(Log.ASSERT, tag, msg);
    }

    private void log(String tag, String msg, int logLevel)
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

    public void startTrace(String tag, String msg, int logLevel)
    {
        startTrace(tag, msg, logLevel, false);
    }

    public void startTrace(String tag, String msg, int logLevel, boolean showStart)
    {
        if (startTraces == null)
        {
            startTraces = new ConcurrentHashMap<String, TraceDate>();
        }

        TraceDate traceDate = new TraceDate();
        DateFormat df = DateFormat.getDateTimeInstance();
        if (showStart)
        {
            log(tag, "START " + msg + " ################## " + df.format(traceDate.getStartTime()) + " #####################################################################", logLevel);
        }

        synchronized (startTraces)
        {
            startTraces.put(msg, traceDate);
        }
    }

    public void partialTrace(String tag, String key, int logLevel)
    {
        partialTrace(tag, key, "", logLevel);
    }

    public void partialTrace(String tag, String key, String partialMsg, int logLevel)
    {
        synchronized (startTraces)
        {
            if (startTraces != null && startTraces.containsKey(key))
            {
                TraceDate start = startTraces.get(key);
                Date now = new Date();
                long diffFromLast = now.getTime() - start.getLastTime().getTime();
                long diffFromStart = now.getTime() - start.getStartTime().getTime();
                start.updateLast(now);
                log(tag, "PARTIAL " + key + " " + partialMsg + " %%%%%%%%%%%%% " + diffFromLast + " ms (Tot: " + diffFromStart  + " ms) %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%", logLevel);
            }
        }
    }

    public void stopTrace(String tag, String key, int logLevel)
    {
        stopTrace(tag, key, "", logLevel);
    }

    public void stopTrace(String tag, String key, String msg, int logLevel)
    {
        synchronized (startTraces)
        {
            if (startTraces != null && startTraces.containsKey(key))
            {
                TraceDate start = startTraces.get(key);
                Date now = new Date();
                long diffFromLast = now.getTime() - start.getLastTime().getTime();
                long diffFromStart = now.getTime() - start.getStartTime().getTime();
                start.updateLast(now);
                log(tag, "FINISH " + key + " " + msg + " %%%%%%%%%%%%% " + diffFromLast + " ms (Tot: " + diffFromStart  + " ms) %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%", logLevel);
            }
        }
    }

    public void logIntent(String tag, String msg, Intent intent, int logLevel)
    {
        logIntent(tag, msg, intent, logLevel, false);
    }

    public void logIntent(String tag, Intent intent, int logLevel)
    {
        logIntent(tag, null, intent, logLevel, false);
    }

    public void logIntent(String tag, Intent intent, int logLevel, boolean logExtras)
    {
        logIntent(tag, null, intent, logLevel, logExtras);
    }

    public void logIntent(String tag, String msg, Intent intent, int logLevel, boolean logExtras)
    {
        StringBuilder sb = new StringBuilder();

        if (msg != null)
        {
            sb.append(msg);
            sb.append(intent.toString());
        }
        else
        {
            sb.append("LOG Intent: ");
            sb.append(intent.toString());
        }

        if (intent.getAction() != null)
        {
            sb.append(intent.getAction());
            sb.append(" ");
        }

        if (intent.getDataString() != null)
        {
            sb.append(intent.getDataString());
            sb.append(" ");
        }

        if (logExtras)
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                for (String key : extras.keySet())
                {
                    String extra = String.valueOf(extras.get(key));
                    sb.append(String.format("EXTRA [\"%s\"]: %s ",key,extra));
                }
            }
        }

        log(tag, sb.toString(), logLevel);
    }
}
