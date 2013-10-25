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
            sb.append(tag.toString() + " ");
        }

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

    public static String getAutomaticDescription(DBProxy proxyData)
    {
        return String.format("Proxy %s",proxyData.host);
    }

//    public void setTags(List<DBTag> inTags)
//    {
//        tags = inTags;
//    }
//
//    public List<DBTag> getTags()
//    {
//        if (tags.size() == 0)
//        {
//            if (isPersisted)
//                tags = ApplicationGlobals.getDBManager().getTagsForProxy(getId());
//        }
//
//        return tags;
//    }
}
