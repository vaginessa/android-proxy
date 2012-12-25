package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LogWrapper
{
//	private static int mLogLevel = Integer.MAX_VALUE; 
	private static int mLogLevel = Log.VERBOSE;
	
	public static void d(String tag, String msg)
	{
		if (mLogLevel <= Log.DEBUG)
			Log.d(tag, msg);
	}
	
	public static void v(String tag, String msg)
	{
		if (mLogLevel <= Log.VERBOSE)
			Log.v(tag, msg);
	}
	
	public static void e(String tag, String msg)
	{
		if (mLogLevel <= Log.ERROR)
			Log.e(tag, msg);
	}

	public static void i(String tag, String msg)
	{
		if (mLogLevel <= Log.INFO)
			Log.e(tag, msg);
	}
	
	public static void log(String tag, String msg, int logLevel)
	{
		switch(logLevel)
		{
			case Log.DEBUG:
				d(tag,msg);
				break;
			case Log.ERROR:
				e(tag,msg);
				break;
			case Log.VERBOSE:
				v(tag,msg);
				break;
			case Log.INFO:
				i(tag,msg);
				break;
		}
	}
	
	public static void logIntent(String tag, Intent intent, int logLevel)
	{
		log(tag, intent.toString(), logLevel);
    	if (intent.getAction() != null) log(tag, intent.getAction(), logLevel);
    	if (intent.getDataString() != null) log(tag, intent.getDataString(), logLevel);
    	
    	Bundle extras = intent.getExtras();
    	if (extras != null)	
    	{
        	for(String key: extras.keySet())
        	{
        		String extra = String.valueOf(extras.get(key));
        		log(tag, "Key: " + key + " ---- " + extra, logLevel);
        	}
    	}
	}
}
