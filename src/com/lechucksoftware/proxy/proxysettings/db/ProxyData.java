package com.lechucksoftware.proxy.proxysettings.db;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by Marco on 13/09/13.
 */
public class ProxyData
{
    private Long id;
    public String host;
    public Integer port;
    public String exclusion;
    public String description;
    private Long creationDate;
    private Long lastModifiedDate;

    public Boolean isPersisted;

    public ProxyData()
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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (description != null && !description.equals("")) sb.append(description);
        sb.append(String.format("%s:%d", host, port));
        if (exclusion != null && !exclusion.equals("")) sb.append(String.format(" (%s)",exclusion));

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

    public static String getAutomaticDescription(ProxyData proxyData)
    {
        return String.format("Proxy %s",proxyData.host);
    }
}
