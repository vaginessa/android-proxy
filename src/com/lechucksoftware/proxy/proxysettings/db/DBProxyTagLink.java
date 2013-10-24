package com.lechucksoftware.proxy.proxysettings.db;

import java.lang.reflect.Field;

/**
 * Created by Marco on 13/09/13.
 */
public class DBProxyTagLink extends DBObject
{
    public long proxyId;
    public long tagId;

    public DBProxyTagLink()
    {
        super();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("P:%d-T:%d", proxyId, tagId));
        return sb.toString();
    }
}
