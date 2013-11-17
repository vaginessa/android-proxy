package com.shouldit.proxy.lib;

import com.shouldit.proxy.lib.enums.ProxyStatusProperties;

import java.util.Comparator;

public class ProxyStatusPropertiesComparator implements Comparator<ProxyStatusProperties>
{
    public int compare(ProxyStatusProperties o1, ProxyStatusProperties o2)
    {
    	int result = o1.getPriority().compareTo(o2.getPriority());
    	return result;
    }
}
