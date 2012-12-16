package com.lechucksoftware.proxy.proxysettings.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lechucksoftware.proxy.proxysettings.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UrlManager
{
	public static String[] getUsedUrls(Context ctx)
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String cachedUrls = sharedPref.getString(Constants.PREFERENCES_CACHED_URLS, "");
		
		if (cachedUrls == "")
		{
			// Populate cachedurls for the first time
			List<String> defaultUrls = new ArrayList<String>();
			defaultUrls.add("http://");
			defaultUrls.add("http://www.");
			defaultUrls.add("https://");
			defaultUrls.add("https://www.");
			defaultUrls.add("ftp://");
			
			SharedPreferences.Editor keyValuesEditor = sharedPref.edit();
			StringBuilder sb = new StringBuilder();
			for (String s : defaultUrls) 
			{
				sb.append(s);
				sb.append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			keyValuesEditor.putString(Constants.PREFERENCES_CACHED_URLS, sb.toString());
			keyValuesEditor.commit();
			
			String [] results = new String[defaultUrls.size()];  
			results = (String[]) defaultUrls.toArray(results);
			return results;
		}
		else
		{
			return cachedUrls.split(",");
		}
	}

	public static void addUsedUrl(Context ctx, String url)
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String cachedUrls = sharedPref.getString(Constants.PREFERENCES_CACHED_URLS, "");
		SharedPreferences.Editor keyValuesEditor = sharedPref.edit();
		
		if (cachedUrls == "")
		{		
			keyValuesEditor.putString(Constants.PREFERENCES_CACHED_URLS, url);
			keyValuesEditor.commit();
		}
		else
		{
			List<String> urlsList = new ArrayList<String>(Arrays.asList(cachedUrls.split(",")));
			
			if (!urlsList.contains(url))
			{
				cachedUrls = cachedUrls.concat("," + url);
				keyValuesEditor.putString(Constants.PREFERENCES_CACHED_URLS, cachedUrls);
				keyValuesEditor.commit();	
			}
		}
	}
}
