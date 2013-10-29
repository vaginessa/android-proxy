package com.lechucksoftware.proxy.proxysettings.db;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/09/13.
 */
public class DBProxy extends DBObject
{
    public String host;
    public Integer port;
    public String exclusion;
    public List<DBTag> tags;
    private String countryCode;

    public DBProxy()
    {
        super();
        tags = new ArrayList<DBTag>();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:%d", host, port));

        sb.append(" tags: ");
        for(DBTag tag:tags)
        {
            sb.append(tag.toString());
            sb.append(" ");
        }

        if (exclusion != null && !exclusion.equals("")) sb.append(String.format(" (%s)",exclusion));
        if (countryCode != null && !countryCode.equals("")) sb.append(String.format(" (%s)",countryCode));

        return sb.toString();
    }

    public String getDebugInfo()
    {
        StringBuilder sb = new StringBuilder();
        for (Field f : this.getClass().getFields())
        {
            try
            {
                String name = f.getName();
                String value = f.get(this).toString();
                sb.append(String.format("%s: %s ",name,value));
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public String getCountryCode()
    {
        if (countryCode != null)
            return countryCode;
        else
            return "";
    }

    public void setCountryCode(String code)
    {
        countryCode = code;
    }
}
