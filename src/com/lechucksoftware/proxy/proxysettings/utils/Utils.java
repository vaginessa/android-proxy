package com.lechucksoftware.proxy.proxysettings.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import android.content.Context;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;
import com.bugsense.trace.BugSenseHandler;
import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.android.utils.lib.log.LogWrapper;

public class Utils
{
	public static String TAG = "Utils";
    public static String BASE_ASSETS = "file:///android_asset/";

    public static String getAppVersionName(Context ctx)
    {
        PackageInfo pi = Utils.getAppInfo(ctx);
        String appVersionName;

        if (pi != null)
        {
            appVersionName = ctx.getResources().getString(R.string.app_versionname, pi.versionName);
        }
        else
        {
            appVersionName = "";
        }

        return appVersionName;
    }

    public static PackageInfo getAppInfo(Context ctx)
    {
        PackageInfo pInfo = null;
        try
        {
            pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            BugSenseHandler.sendException(e);
        }

        return pInfo;
    }

	public static void setHTTPAuthentication(final String user, final String password)
	{
		Authenticator.setDefault(new Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});
	}

    public static String getFullAsset(Context ctx, String filename)
    {
        String text = null;
        InputStream inputStream = null;
        try
        {
            inputStream = ctx.getAssets().open(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String aux;

            while ((aux = br.readLine()) != null)
            {
                builder.append(aux);
            }

            text = builder.toString();
        }
        catch (Exception e)
        {
            BugSenseHandler.sendException(e);
        }

        return text;
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
			InputStream inputStream = ctx.getAssets().open("proxy_settings_bugsense_license.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			key = br.readLine();
			key = key.trim();
			LogWrapper.d(TAG, "Using bugsense key '" + key + "'");
		}
		catch (IOException e)
		{
			LogWrapper.e("TAG", "No bugsense keyfile found");
            BugSenseHandler.sendException(e);
            return;
		}
        catch (Exception e)
        {
            BugSenseHandler.sendException(e);
            return;
        }

        if (key == null)
        {
            CharSequence text =  "No bugsense keyfile found";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(ctx, text, duration);
            toast.show();
        }
        else
        {
            BugSenseHandler.initAndStartSession(ctx, key);
        }
	}
}
