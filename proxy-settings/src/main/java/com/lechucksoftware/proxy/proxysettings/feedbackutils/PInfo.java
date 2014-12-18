package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

import timber.log.Timber;

public class PInfo implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8480854041634888786L;

	static String TAG = PInfo.class.getSimpleName();
	
    public String appname = "";
    public String pname = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable icon;
    public ApplicationInfo applicationInfo;
    
    void prettyPrint() 
    {
        Timber.d("%s\t%s\t%s\t%s",appname, pname, versionName, versionCode);
    }
}
