package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.res.AssetManager;
import com.bugsense.trace.BugSenseHandler;
//import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.shouldit.proxy.lib.log.IExceptionReport;
import com.shouldit.proxy.lib.log.LogWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class BugReportingUtils implements IExceptionReport
{
    private static final String TAG = "BugReportingUtils";
    private Boolean setupDone;
    private static BugReportingUtils instance;
    private Context context;

//    private static Tracker tracker;

    private BugReportingUtils()
    {
        setupDone = false;
    }

    public static BugReportingUtils getInstance()
    {
        if (instance == null)
        {
            instance = new BugReportingUtils();
        }

        return instance;
    }

    public static void setup(Context ctx)
    {
        getInstance().context = ctx;
        getInstance().setupBugSense(ctx);

        // Initialize a tracker using a Google Analytics property ID.
//        tracker = GoogleAnalytics.getInstance(ctx).getTracker("UA-41504209-1");
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
            BugSenseHandler.sendException(e);

            StandardExceptionParser sep = new StandardExceptionParser(getInstance().context, null);
            String exceptionDescription = sep.getDescription(Thread.currentThread().getName(),e);
            Map<String, String> map = MapBuilder.createException(exceptionDescription, false).build();
            EasyTracker.getInstance(getInstance().context).send(map);
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

    public void send(String s)
    {
        if (setupDone)
        {
            BugSenseHandler.sendEvent(s);

            MapBuilder mapBuilder = MapBuilder.createEvent("ui_action",     // Event category (required)
                                                           "button_press",  // Event action (required)
                                                           "play_button",   // Event label
                                                            null);          // Event value
            Map<String, String> map = mapBuilder.build();
            EasyTracker.getInstance(getInstance().context).send(map);
        }
        else
        {
            LogWrapper.e(TAG, "sendEvent: " + s);
        }
    }

    public static void addCrashExtraData(String s1, String s2)
    {
        getInstance().addCrashExtraData(s1,s2);
    }

    public void addExtraData(String s1, String s2)
    {
        if (setupDone)
        {
            BugSenseHandler.addCrashExtraData(s1,s2);
        }
        else
        {
            setupBugSense(ApplicationGlobals.getInstance().getApplicationContext());
            LogWrapper.e(TAG, "addCrashExtraData: " + s1);
            LogWrapper.e(TAG, "addCrashExtraData: " + s2);
        }
    }


}
