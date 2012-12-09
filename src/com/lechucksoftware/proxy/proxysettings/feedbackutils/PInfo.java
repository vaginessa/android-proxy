package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import java.io.Serializable;

import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

import android.graphics.drawable.Drawable;
import android.util.Log;

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
        LogWrapper.v(TAG,appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
    }
}
