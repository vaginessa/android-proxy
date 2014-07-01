package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.bugsense.trace.BugSenseHandler;
import com.crittercism.app.Crittercism;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;

import java.util.HashMap;
import java.util.Map;

import be.shouldit.proxy.lib.log.IEventReporting;

public class EventReportingUtils implements IEventReporting
{
    private static final String TAG = "EventReportingUtils";
//    private Boolean setupDone;
    private static EventReportingUtils instance;
//    private static boolean crittercismSetupDone;
    private static boolean bugSenseSetupDone;
    private static boolean analyticsSetupDone;
    private Context context;
    private Tracker defaultTracker;
//    private static Tracker tracker;

    private EventReportingUtils()
    {
//        crittercismSetupDone = false;
        bugSenseSetupDone = false;
        analyticsSetupDone = false;
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

        analyticsSetupDone = getInstance().setupAnalytics(ctx);
        bugSenseSetupDone = getInstance().setupBugSense(ctx);
//        crittercismSetupDone = getInstance().setupCrittercism(ctx);
    }

    private boolean setupBugSense(Context ctx)
    {
        String key;
        Boolean setupDone;

        key = BuildConfig.BUGSENSE_LICENSE;

        if (key == null || key.length() != 8)
        {
            CharSequence text = "No valid BugSense keyfile found";
//            int duration = Toast.LENGTH_LONG;
//            Toast toast = Toast.makeText(ctx, text, duration);
//            toast.show();
            App.getLogger().e(TAG, text.toString());
            setupDone = false;
        }
        else
        {
            App.getLogger().i(TAG, String.format("BugSense setup [%s]", key));
            BugSenseHandler.initAndStartSession(ctx, key);
            setupDone = true;
        }

        return setupDone;
    }

    public boolean setupCrittercism(Context context)
    {
        String key;
        Boolean setupDone;

        key = BuildConfig.CRITTERCISM_LICENSE;

        if (key == null || key.length() != 24)
        {
            CharSequence text = "No valid Crittercism keyfile found";
            App.getLogger().e(TAG, text.toString());
            setupDone = false;
        }
        else
        {
            App.getLogger().i(TAG, String.format("Crittercism setup [%s]", key));
            Crittercism.initialize(context, "");
            setupDone = true;
        }

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
//            defaultTracker.setAppName(ApplicationStatistics.getInstallationDetails(context));
            setupDone = true;
        }
        else
        {
            setupDone = false;
        }

        return setupDone;
    }

    public static Tracker getDefaultTracker()
    {
        return getInstance().defaultTracker;
    }

    public static void sendException(Exception e)
    {
        getInstance().send(e);
    }

    public void send(Exception e)
    {
        App.getLogger().e(TAG, "Handled exception message: " + e.getMessage());
        App.getLogger().e(TAG, "Handled exception stack trace: " + TextUtils.join("\n", e.getStackTrace()));

        if (bugSenseSetupDone)
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
            {
                BugSenseHandler.sendExceptionMap(map, e);
            }
            else
            {
                BugSenseHandler.sendException(e);
            }
        }
        else
        {
            setupBugSense(App.getInstance().getApplicationContext());
        }

//        if (crittercismSetupDone)
//        {
//            Crittercism.logHandledException(e);
//        }
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

    public static void sendEvent(final int categoryId, final int actionId, final int labelId)
    {
        getInstance().send(categoryId, actionId, labelId);
    }

    public static void sendEvent(final int categoryId, final int actionId, final int labelId, final Long eventValue)
    {
        getInstance().send(categoryId, actionId, labelId, eventValue);
    }

    public static void sendEvent(final String category, final String action, final String label, final Long eventValue)
    {
        getInstance().send(category, action, label, eventValue);
    }

    public void send(final int categoryId, final int actionId, final int labelId)
    {
        send(categoryId, actionId, labelId, null);
    }

    public void send(final int categoryId, final int actionId, final int labelId, final Long eventValue)
    {
        String category = context.getString(categoryId);
        String action = context.getString(actionId);
        String label = context.getString(labelId);

        send(category, action, label, eventValue);
    }

    public void send(final String category, final String action, final String label, final Long eventValue)
    {
        if (bugSenseSetupDone)
        {
            HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder();

            builder.setCategory(category);   // Event category (required)
            builder.setAction(action);       // Event action (required)
            builder.setLabel(label);         // Event label

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
            String msg = "";
            if (eventValue != null)
                msg = String.format("Logging event: %s %s %s %d", category, action, label, eventValue);
            else
                msg = String.format("Logging event: %s %s %s", category, action, label);

            App.getLogger().e(TAG, msg);
        }
    }

    public void send(String s)
    {
        send("", "", s, null);
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
