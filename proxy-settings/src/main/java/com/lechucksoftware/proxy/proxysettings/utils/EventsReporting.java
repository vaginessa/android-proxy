package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;

import java.util.Map;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

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
        Boolean setupDone;

        if (!Fabric.isInitialized()) {

            final Fabric fabric = new Fabric.Builder(ctx)
                    .kits(new Crashlytics())
                    .debuggable(true)
                    .build();
            Fabric.with(fabric);
        }

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

            defaultTracker.setAppVersion(String.valueOf(BuildConfig.VERSION_CODE));
            defaultTracker.enableExceptionReporting(true);
            defaultTracker.enableAutoActivityTracking(true);
//            defaultTracker.setAppName(ApplicationStatistics.getInstallationDetails(context));
            setupDone = true;

            Timber.d("Setup of Analytics completed");
        }
        else
        {
            setupDone = false;
            Timber.e("Cannot perform setup of Analytics");
        }

        return setupDone;
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
        String msg = String.format("Event Cat:'%s', Act:'%s' Lab:'%s' Ev:'%d'", category, action, label, eventValue);

        if (analyticsSetupDone)
        {
            HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder();

            builder.setCategory(category);   // Event category (required)
            builder.setAction(action);       // Event action (required)
            builder.setLabel(label);         // Event label

            if (eventValue != null)
                builder.setValue(eventValue);

            Map<String, String> map = builder.build();

            Timber.d(msg);
            defaultTracker.send(map);
        }
        else
        {
            Timber.e(msg);
        }
    }

    public void sendEvent(String s)
    {
        sendEvent("", "", s, null);
    }
}
