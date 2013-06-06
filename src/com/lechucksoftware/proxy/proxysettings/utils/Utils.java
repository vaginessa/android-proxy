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

public class Utils
{
	public static String TAG = "Utils";

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

	public static void SetHTTPAuthentication(final String user, final String password)
	{
		Authenticator.setDefault(new Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});
	}

	public static void SetupBugSense(Context ctx)
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
