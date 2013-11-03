package com.lechucksoftware.proxy.proxysettings.db;

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

    @Override
    public boolean equals(Object another)
    {
        Boolean result = false;

        if ((another instanceof DBTag))
        {
            DBTag anotherTag = (DBTag) another;

            if (this.isPersisted && anotherTag.isPersisted)
            {
                return anotherTag.getId() == this.getId();
            }
            else
            {
                return anotherTag.tag.equalsIgnoreCase(this.tag);
            }
        }

        return result;
    }
}
