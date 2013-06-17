package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import java.io.Serializable;

import com.shouldit.android.utils.lib.log.LogWrapper;

import android.graphics.drawable.Drawable;

public class PInfo implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8480854041634888786L;

	static String TAG = "PInfo";
	
    public String appname = "";
    public String pname = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable icon;
    
    void prettyPrint() 
    {
        LogWrapper.d(TAG,appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
    }
}
