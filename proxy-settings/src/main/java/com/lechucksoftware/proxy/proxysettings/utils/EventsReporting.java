package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;

import java.util.Map;

import timber.log.Timber;

//import com.bugsense.trace.BugSenseHandler;

public class EventsReporting
{
    private static boolean analyticsSetupDone;
    private static boolean crashLyticsSetupDone;
    private Context context;
    private Tracker defaultTracker;

    public EventsReporting(Context ctx)
    {
        context = ctx;

        analyticsSetupDone = false;
        crashLyticsSetupDone = false;

        setup();
    }

    public void setup()
    {
        analyticsSetupDone = setupAnalytics(context);
        crashLyticsSetupDone = setupCrashLytics(context);
    }

    private boolean setupCrashLytics(Context ctx)
    {
        String key;
        Boolean setupDone;

        Crashlytics.start(ctx);
        setupDone = true;

        return setupDone;
    }

    public boolean setupAnalytics(Context upAnalytics)
    {
        String key;
        Boolean setupDone;

        key = BuildConfig.ANALYTICS_TRACK_ID;

        if (!TextUtils.isEmpty(key))
        {
            defaultTracker = GoogleAnalytics.getInstance(context).newTracker(BuildConfig.ANALYTICS_TRACK_ID);

            defaultTracker.enableExceptionReporting(true);
            defaultTracker.enableAutoActivityTracking(true);
//            defaultTracker.setAppName(ApplicationStatistics.getInstallationDetails(context));
            setupDone = true;
        }
        else
        {
            setupDone = false;
        }

        return setupDone;
    }

//    public void sendException(Exception e)
//    {
//        sendException(e, null);
//    }
//
//    public void sendException(Exception e, Map<String, String> params)
//    {
//        Timber.e("Handled exception message: " + e.getMessage());
//        Timber.e("Handled exception stack trace: " + Log.getStackTraceString(e));
//
//        if (crashLyticsSetupDone)
//        {
//            if (params != null)
//            {
//                for (String key : params.keySet())
//                {
//                    String value = params.get(key);
//                    Crashlytics.log(0, key, value); // Priority = 0
//                    Timber.e(String.format("Added log '%s': '%s'",key,value));
//                }
//            }
//
//            Crashlytics.logException(e);
//            Timber.e("Sent exception to Crashlytics");
//        }
//
//        if (analyticsSetupDone)
//        {
//            if (e != null)
//            {
//                HitBuilders.ExceptionBuilder eb = new HitBuilders.ExceptionBuilder();
//                StandardExceptionParser sep = new StandardExceptionParser(context, null);
//
//                eb.setFatal(false);
//                String title = sep.getDescription(Thread.currentThread().getName(), e);
//                String stackTrace = Log.getStackTraceString(e);
//
//                eb.setDescription(TextUtils.join("             ",new Object[]{title,stackTrace}));
//
//                defaultTracker.send(eb.build());
//            }
//        }
//        else
//        {
//            setupAnalytics(App.getInstance().getApplicationContext());
//        }
//    }

    public void sendEvent(final int categoryId, final int actionId, final int labelId)
    {
        sendEvent(categoryId, actionId, labelId, null);
    }

    public void sendEvent(final int categoryId, final int actionId, final int labelId, final Long eventValue)
    {
        String category = context.getString(categoryId);
        String action = context.getString(actionId);
        String label = context.getString(labelId);

        sendEvent(category, action, label, eventValue);
    }

    public void sendEvent(final String category, final String action, final String label, final Long eventValue)
    {
        if (analyticsSetupDone)
        {
            HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder();

            builder.setCategory(category);   // Event category (required)
            builder.setAction(action);       // Event action (required)
            builder.setLabel(label);         // Event label

            if (eventValue != null)
                builder.setValue(eventValue);

            Map<String, String> map = builder.build();
            defaultTracker.send(map);
        }
        else
        {
            String msg = "";
            if (eventValue != null)
                msg = String.format("Logging event: %s %s %s %d", category, action, label, eventValue);
            else
                msg = String.format("Logging event: %s %s %s", category, action, label);

            Timber.e(msg);
        }
    }

    public void sendEvent(String s)
    {
        sendEvent("", "", s, null);
    }

    public void sendScreenView(String screenName)
    {
        // DO nothing, since enableAutoActivityTracking = true

//        Tracker tracker = getDefaultTracker();
//        if (tracker != null)
//        {
//            tracker.setScreenName(screenName);
//            tracker.sendEvent(new HitBuilders.AppViewBuilder().build());
//        }
    }
}
