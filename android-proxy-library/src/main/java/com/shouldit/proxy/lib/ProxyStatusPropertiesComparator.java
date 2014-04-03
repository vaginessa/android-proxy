package com.shouldit.proxy.lib;

import com.shouldit.proxy.lib.enums.ProxyStatusProperties;

import java.io.Serializable;
import java.util.Comparator;

public class ProxyStatusPropertiesComparator implements Comparator<ProxyStatusProperties>, Serializable
{
    public int compare(ProxyStatusProperties o1, ProxyStatusProperties o2)
    {
    	int result = o1.getPriority().compareTo(o2.getPriority());
    	return result;
    }
}
