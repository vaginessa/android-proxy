package com.lechucksoftware.proxy.proxysettings.db;

import java.io.Serializable;

/**
 * Created by Marco on 13/09/13.
 */
public class TagEntity extends BaseEntity implements Serializable
{
    private String tag;
    private Integer tagColor;

    public TagEntity()
    {
        super();
    }

    public TagEntity(TagEntity t)
    {
        super();
        this.setTag(t.getTag());
        this.setTagColor(t.getTagColor());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s-%d", getTag(), getTagColor()));
        return sb.toString();
    }

    @Override
    public boolean equals(Object another)
    {
        Boolean result = false;

        if ((another instanceof TagEntity))
        {
            TagEntity anotherTag = (TagEntity) another;

            if (this.isPersisted() && anotherTag.isPersisted())
            {
                return anotherTag.getId() == this.getId();
            }
            else
            {
                return anotherTag.getTag().equalsIgnoreCase(this.getTag());
            }
        }

        return result;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public Integer getTagColor()
    {
        return tagColor;
    }

    public void setTagColor(Integer tagColor)
    {
        this.tagColor = tagColor;
    }
}
