package com.lechucksoftware.proxy.proxysettings.db;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by Marco on 23/10/13.
 */
public class DBObject
{
    private long id;
    private long creationDate;
    private long lastModifiedDate;
    public Boolean isPersisted;

    public DBObject()
    {
        isPersisted = false;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setCreationDate(long date)
    {
        creationDate = date;
    }

    public Date getCreationDate()
    {
        Date d = new Date(creationDate);
        return d;
    }

    public void setModifiedDate(long date)
    {
        lastModifiedDate = date;
    }

    public Date getModifiedDate()
    {
        Date d = new Date(lastModifiedDate);
        return d;
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
}
