package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.constants.BaseActions;
import com.lechucksoftware.proxy.proxysettings.constants.EventCategories;

import java.util.HashMap;
import java.util.Map;

import be.shouldit.proxy.lib.log.IEventReporting;

public class EventReportingUtils implements IEventReporting
{
    private static final String TAG = "EventReportingUtils";
    private Boolean setupDone;
    private static EventReportingUtils instance;
    private Context context;
    private Tracker defaultTracker;
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
        getInstance().setupAnalytics(ctx);
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
            HashMap<String, String> map = new HashMap<String, String>();
            PackageInfo appInfo = Utils.getAppInfo(context);

            try
            {
                map.put("versionName", String.valueOf(appInfo.versionName));
                map.put("versionCode", String.valueOf(appInfo.versionCode));
            }
            catch (Exception internalEx)
            {
                BugSenseHandler.sendException(internalEx);
            }

            if (map != null)
                BugSenseHandler.sendExceptionMap(map,e);
            else
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

    public static int getTotalCrashes()
    {
        return getInstance().getCrashesCount();
    }

    public static void clearTotalCrashes()
    {
        getInstance().clearTotalCrashesNum();
    }

    public int getCrashesCount()
    {
        // Get the total number of crashes
        int totalCrashes = BugSenseHandler.getTotalCrashesNum();
        return totalCrashes;
    }

    public void clearTotalCrashesNum()
    {
        BugSenseHandler.clearTotalCrashesNum();
    }

    public static void sendEvent(String s)
    {
        getInstance().send(s);
    }

    public static void sendEvent(EventCategories eventCategory, BaseActions eventAction, String eventLabel)
    {
        getInstance().send(eventCategory, eventAction, eventLabel);
    }

    public static void sendEvent(EventCategories eventCategory, BaseActions eventAction, String eventLabel, Long eventValue)
    {
        getInstance().send(eventCategory, eventAction, eventLabel, eventValue);
    }

    public void send(EventCategories eventCategory, BaseActions eventAction, String eventLabel)
    {
        send(eventCategory,eventAction, eventLabel, null);
    }

    public void send(EventCategories eventCategory, BaseActions eventAction, String eventLabel, Long eventValue)
    {
        if (setupDone)
        {
            HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder();

            builder.setCategory(eventCategory.toString());   // Event category (required)
            builder.setAction(eventAction.toString());       // Event action (required)
            builder.setLabel(eventLabel.toString());         // Event label

            if (eventValue != null)
                builder.setValue(eventValue);

            Map<String, String> map = builder.build();
            Tracker tracker = getDefaultTracker();
            if(tracker != null)
            {
                tracker.send(map);
            }
        }
        else
        {
            String msg = String.format("sendEvent: %s %s %s", eventCategory, eventAction, eventLabel);
            App.getLogger().e(TAG, msg);
        }
    }

    public void send(String s)
    {
        send(EventCategories.BASE, BaseActions.BASE, s);
    }

    public void setupAnalytics(Context upAnalytics)
    {
        if (!TextUtils.isEmpty(BuildConfig.ANALYTICS_TRACK_ID))
        {
            defaultTracker = GoogleAnalytics.getInstance(context).newTracker(BuildConfig.ANALYTICS_TRACK_ID);
//            defaultTracker.setAppName(ApplicationStatistics.getInstallationDetails(context));
        }
    }

    public static Tracker getDefaultTracker()
    {
        return getInstance().defaultTracker;
    }

    public static void sendScreenView(String screenName)
    {
        Tracker tracker = getDefaultTracker();
        if (tracker != null)
        {
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.AppViewBuilder().build());
        }
    }
}
