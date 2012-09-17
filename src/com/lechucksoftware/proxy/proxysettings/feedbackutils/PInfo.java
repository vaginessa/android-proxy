package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class PInfo implements Serializable 
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
