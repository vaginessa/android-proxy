package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;
import com.bugsense.trace.BugSenseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BugReportingUtils
{
    private static final String TAG = "BugReportingUtils";
    private static Boolean setupDone;

    static
    {
        setupDone = false;
    }

    public static void setupBugSense(Context ctx)
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
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(ctx, text, duration);
            toast.show();
        }
        else
        {
            BugSenseHandler.initAndStartSession(ctx, key);
            setupDone = true;
        }
    }

    public static void sendException(Exception e)
    {
        if (setupDone)
        {
            BugSenseHandler.sendException(e);
        }
        else
        {
            LogWrapper.e(TAG, "sendException: " + e.toString());
        }
    }

    public static void sendEvent(String s)
    {
        if (setupDone)
        {
            BugSenseHandler.sendEvent(s);
        }
        else
        {
            LogWrapper.e(TAG, "sendEvent: " + s);
        }
    }
}
