package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import com.lechucksoftware.proxy.proxysettings.App;

import java.io.Serializable;

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
    public ApplicationInfo applicationInfo;
    
    void prettyPrint() 
    {
        App.getLogger().d(TAG, appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
    }
}
