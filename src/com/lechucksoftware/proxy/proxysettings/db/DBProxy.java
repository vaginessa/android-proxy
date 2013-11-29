package com.lechucksoftware.proxy.proxysettings.db;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/09/13.
 */
public class DBProxy extends DBObject implements Serializable
{
    public String host;
    public Integer port;
    public String exclusion;
    private List<DBTag> tags;
    private String countryCode;

    public DBProxy()
    {
        super();
        tags = new ArrayList<DBTag>();
    }

    @Override
    public boolean equals(Object another)
    {
        Boolean result = false;

        if ((another instanceof DBProxy))
        {
            DBProxy anotherProxy = (DBProxy) another;

            if (this.isPersisted && anotherProxy.isPersisted)
            {
                return anotherProxy.getId() == this.getId();
            }
            else
            {
                if (anotherProxy.host.equalsIgnoreCase(this.host)
                       && anotherProxy.port.equals(this.port)
                       && anotherProxy.exclusion.equalsIgnoreCase(this.exclusion))
                {
                    // TODO: compare also linked TAGS?
                    result = true;
                }
                else
                {
                    result = false;
                }
            }
        }

        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:%d", host, port));

        sb.append(" tags: ");
        if (getTags() != null)
        {
            for(DBTag tag: getTags())
            {
                sb.append(tag.toString());
                sb.append(" ");
            }
        }

        if (exclusion != null && !exclusion.equals("")) sb.append(String.format(" (%s)",exclusion));
        if (countryCode != null && !countryCode.equals("")) sb.append(String.format(" (%s)",countryCode));

        return sb.toString();
    }

    public String getDebugInfo()
    {
        StringBuilder sb = new StringBuilder();
        for (Field f : DBProxy.class.getFields())
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

    public List<DBTag> getTags()
    {
        return tags;
    }

    public void setTags(List<DBTag> tags)
    {
        this.tags = tags;
    }

    public void addTag(DBTag tag)
    {
        if (!this.tags.contains(tag))
            this.tags.add(tag);
    }

    public void addTags(List<DBTag> tags)
    {
        for(DBTag tag : tags)
        {
            addTag(tag);
        }
    }

    public DBTag removeTag(DBTag tag)
    {
        int indexOf = this.tags.indexOf(tag);

        if (indexOf >= 0 && indexOf < this.tags.size())
        {
            return this.tags.remove(indexOf);
        }
        else
            return null;
    }
}
