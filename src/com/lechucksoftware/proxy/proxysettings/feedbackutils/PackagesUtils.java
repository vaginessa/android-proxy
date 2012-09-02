package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;

import com.lechucksoftware.proxy.proxysettings.R;

public class PackagesUtils
{
	public static ArrayList<PInfo> getPackages(Context callerContext)
	{
		ArrayList<PInfo> apps = getInstalledApps(callerContext, false); /*
																		 * false
																		 * = no
																		 * system
																		 * packages
																		 */
		final int max = apps.size();

		for (int i = 0; i < max; i++)
		{
			apps.get(i).prettyPrint();
		}

		return apps;
	}

	private static ArrayList<PInfo> getInstalledApps(Context callerContext, boolean getSysPackages)
	{
		ArrayList<PInfo> res = new ArrayList<PInfo>();

		// Only get applications that can be launched.. 
		
//		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//		final List pkgAppsList = callerContext.getPackageManager().queryIntentActivities(mainIntent, 0);
		
		List<PackageInfo> packs = callerContext.getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++)
		{
			PackageInfo p = packs.get(i);
			
			if ((!getSysPackages) && (p.versionName == null))
			{
				continue;
			}
			
			if (isDisabledPackage(callerContext, p.packageName))
			{
				continue;
			}
			
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel(callerContext.getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			newInfo.icon = p.applicationInfo.loadIcon(callerContext.getPackageManager());
			res.add(newInfo);
		}
		
		Collections.sort(res, new PackagesComparator());
		
		return res;
	}

	private static boolean isDisabledPackage(Context callerContext, String packageName)
	{
		for (String s : callerContext.getResources().getStringArray(R.array.feedback_ignored_packages))
		{
			if (s.equals(packageName))
			{
				return true;
			}
		}
		
		return false;
	}
}
