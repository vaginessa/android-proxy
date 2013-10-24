package com.lechucksoftware.proxy.proxysettings.db;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by Marco on 13/09/13.
 */
public class DBTag extends DBObject
{
    public String tag;
    public Integer tagColor;

    public DBTag()
    {
        super();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s-%d", tag, tagColor));
        return sb.toString();
    }
}
