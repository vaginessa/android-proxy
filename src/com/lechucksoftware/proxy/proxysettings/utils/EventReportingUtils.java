package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.res.AssetManager;
import com.bugsense.trace.BugSenseHandler;
//import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.constants.BaseActions;
import com.lechucksoftware.proxy.proxysettings.constants.EventCategories;
import com.lechucksoftware.proxy.proxysettings.exception.DetailedExceptionParser;
import com.shouldit.proxy.lib.log.IEventReporting;
import com.shouldit.proxy.lib.log.LogWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class EventReportingUtils implements IEventReporting
{
    private static final String TAG = "EventReportingUtils";
    private Boolean setupDone;
    private static EventReportingUtils instance;
    private Context context;

//    private static Tracker tracker;

    private EventReportingUtils()
    {
        setupDone = false;
    }

    public static EventReportingUtils getInstance()
    {
        if (instance == null)
        {
            instance = new EventReportingUtils();
        }

        return instance;
    }

    public static void setup(Context ctx)
    {
        getInstance().context = ctx;
        getInstance().setupBugSense(ctx);
    }

    private void setupBugSense(Context ctx)
    {
        String key = null;

        // If you want to use BugSense for your fork, register with
        // them and place your API key in /assets/bugsense.txt
        // (This prevents me receiving reports of crashes from forked
        // versions which is somewhat confusing!)
        try
        {
            AssetManager am = ctx.getAssets();
            if (am != null)
            {
                InputStream inputStream = am.open("proxy_settings_bugsense_license.txt");
                if (inputStream != null)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    key = br.readLine();
                    key = key.trim();
                    LogWrapper.d(Utils.TAG, "Using bugsense key '" + key + "'");
                }
            }
        }
        catch (IOException e)
        {
            LogWrapper.e(TAG, "No bugsense keyfile found");
            return;
        }
        catch (Exception e)
        {
            LogWrapper.e(TAG, "Generic exception on setupBugSense: " + e.toString());
            return;
        }

        if (key == null)
        {
            CharSequence text = "No bugsense keyfile found";
//            int duration = Toast.LENGTH_LONG;
//            Toast toast = Toast.makeText(ctx, text, duration);
//            toast.show();
            LogWrapper.e(TAG, text.toString());
        }
        else
        {
            BugSenseHandler.initAndStartSession(ctx, key);
            setupDone = true;
        }
    }

    public static void sendException(Exception e)
    {
        getInstance().send(e);
    }

    public void send(Exception e)
    {
        if (setupDone)
        {
            // Bugsense
            BugSenseHandler.sendException(e);

            // Google Analytics
//            DetailedExceptionParser sep = new DetailedExceptionParser();
//            String exceptionDescription = sep.getDescription(Thread.currentThread().getName(),e);
//            Map<String, String> map = MapBuilder.createException(exceptionDescription, false).build();
//            EasyTracker.getInstance(getInstance().context).send(map);
        }
        else
        {
            setupBugSense(ApplicationGlobals.getInstance().getApplicationContext());
            LogWrapper.e(TAG, "sendException: " + e.toString());
        }
    }

    public static void sendEvent(String s)
    {
        getInstance().send(s);
    }

    public static void sendEvent(EventCategories eventCategory, BaseActions eventAction, String eventLabel, Long eventValue)
    {
        getInstance().send(eventCategory, eventAction, eventLabel, eventValue);
    }

    public void send(EventCategories eventCategory, BaseActions eventAction, String eventLabel, Long eventValue)
    {
        if (setupDone)
        {
            MapBuilder mapBuilder = MapBuilder.createEvent(
                                        eventCategory.toString(),   // Event category (required)
                                        eventAction.toString(),     // Event action (required)
                                        eventLabel,                 // Event label
                                        eventValue);                // Event value

            Map<String, String> map = mapBuilder.build();
            EasyTracker.getInstance(getInstance().context).send(map);
        }
        else
        {
            String msg = String.format("sendEvent: %s %s %s %d", eventCategory, eventAction, eventLabel, eventValue);
            LogWrapper.e(TAG, msg);
        }
    }

    public void send(String s)
    {
        send(EventCategories.BASE, BaseActions.BASE, s, null);
    }
}
