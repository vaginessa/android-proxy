package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import java.util.Comparator;

public class PackagesComparator implements Comparator<PInfo>
{
	public int compare(PInfo lhs, PInfo rhs)
	{
		return lhs.appname.compareToIgnoreCase(rhs.appname);
	}
}