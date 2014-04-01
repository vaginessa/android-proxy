package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.text.TextUtils;

import com.bugsense.trace.BugSenseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.constants.BaseActions;
import com.lechucksoftware.proxy.proxysettings.constants.EventCategories;
import com.shouldit.proxy.lib.log.IEventReporting;

import java.util.Map;

//import com.google.analytics.tracking.android.Tracker;

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

        key = BuildConfig.BUGSENSE_LICENSE;

        if (key == null || key.length() != 8)
        {
            CharSequence text = "No valid BugSense keyfile found";
//            int duration = Toast.LENGTH_LONG;
//            Toast toast = Toast.makeText(ctx, text, duration);
//            toast.show();
            App.getLogger().e(TAG, text.toString());
        }
        else
        {
            App.getLogger().i(TAG, String.format("BugSense setup [%s]", key));
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
        App.getLogger().e(TAG, "Handled exception message: " + e.getMessage());
        App.getLogger().e(TAG, "Handled exception stack trace: " + TextUtils.join("\n", e.getStackTrace()));

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
            setupBugSense(App.getInstance().getApplicationContext());
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
            App.getLogger().e(TAG, msg);
        }
    }

    public void send(String s)
    {
        send(EventCategories.BASE, BaseActions.BASE, s, null);
    }
}
