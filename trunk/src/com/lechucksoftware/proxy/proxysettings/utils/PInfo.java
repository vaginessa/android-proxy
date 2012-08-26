package com.lechucksoftware.proxy.proxysettings.utils;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class PInfo 
{
	static String TAG = "PInfo";
	
    public String appname = "";
    public String pname = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable icon;
    
    void prettyPrint() 
    {
        Log.v(TAG,appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
    }
}
