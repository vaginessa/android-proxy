package com.shouldit.proxy.lib;

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
}
