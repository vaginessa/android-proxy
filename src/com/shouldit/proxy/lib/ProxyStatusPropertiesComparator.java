package com.shouldit.proxy.lib;

import java.util.Comparator;

import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;

public class ProxyStatusPropertiesComparator implements Comparator<ProxyStatusProperties>
{
    public int compare(ProxyStatusProperties o1, ProxyStatusProperties o2)
    {
    	int result = o1.getPriority().compareTo(o2.getPriority());
    	return result;
    }
}
