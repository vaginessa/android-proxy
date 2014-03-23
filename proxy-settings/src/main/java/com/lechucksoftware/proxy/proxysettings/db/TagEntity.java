package com.lechucksoftware.proxy.proxysettings.db;

import java.io.Serializable;

/**
 * Created by Marco on 13/09/13.
 */
public class TagEntity extends BaseEntity implements Serializable
{
    public String tag;
    public Integer tagColor;

    public TagEntity()
    {
        super();
    }

    public TagEntity(TagEntity t)
    {
        super();
        this.tag = t.tag;
        this.tagColor = t.tagColor;
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

        if ((another instanceof TagEntity))
        {
            TagEntity anotherTag = (TagEntity) another;

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
