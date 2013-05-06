package com.lechucksoftware.proxy.proxysettings.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import android.content.Context;

import com.bugsense.trace.BugSenseHandler;

public class Utils
{
	public static String TAG = "Utils";

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
		// If you want to use BugSense for your fork, register with
		// them and place your API key in /assets/bugsense.txt
		// (This prevents me receiving reports of crashes from forked
		// versions which is somewhat confusing!)
		try
		{
			InputStream inputStream = ctx.getAssets().open("proxy_settings_bugsense_license.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String key = br.readLine();
					
			key = key.trim();
			LogWrapper.d(TAG, "Using bugsense key '" + key + "'");
			BugSenseHandler.initAndStartSession(ctx, key);
		}
		catch (IOException e)
		{
			LogWrapper.e("TAG", "No bugsense keyfile found");
		}
	}
}
