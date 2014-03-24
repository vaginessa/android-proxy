package io.should.proxy.lib;

import java.util.Comparator;

import io.should.proxy.lib.enums.ProxyStatusProperties;

public class ProxyStatusPropertiesComparator implements Comparator<ProxyStatusProperties>
{
    public int compare(ProxyStatusProperties o1, ProxyStatusProperties o2)
    {
    	int result = o1.getPriority().compareTo(o2.getPriority());
    	return result;
    }
}
