package com.lechucksoftware.proxy.proxysettings.db;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by Marco on 13/09/13.
 */
public class ProxyTag
{
    private Long id;
    public String tag;
    public Integer tagColor;

    public Boolean isPersisted;

    public ProxyTag()
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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:%d", tag, tagColor));
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
}
