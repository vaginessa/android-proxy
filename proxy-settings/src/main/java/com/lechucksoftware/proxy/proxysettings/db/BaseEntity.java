package com.lechucksoftware.proxy.proxysettings.db;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Marco on 23/10/13.
 */
public class BaseEntity implements Serializable
{
    private UUID uuid;
    private long id;
    private long creationDate;
    private long lastModifiedDate;
    private boolean isPersisted;
    private boolean isSelected;

    public BaseEntity()
    {
        setPersisted(false);
        setSelected(false);
        uuid = UUID.randomUUID();
    }

    public UUID getUUID()
    {
        return uuid;
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

    public boolean isPersisted()
    {
        return isPersisted;
    }

    public void setPersisted(boolean isPersisted)
    {
        this.isPersisted = isPersisted;
    }

    public boolean isSelected()
    {
        return isSelected;
    }

    public void setSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
    }
}
