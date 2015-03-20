package com.lechucksoftware.proxy.proxysettings.db;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Marco on 13/09/13.
 */
public class TagEntity extends BaseEntity implements Parcelable
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

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest,flags);

        dest.writeString(this.tag);
        dest.writeValue(this.tagColor);
    }

    private TagEntity(Parcel in)
    {
        super(in);

        this.tag = in.readString();
        this.tagColor = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<TagEntity> CREATOR = new Creator<TagEntity>()
    {
        public TagEntity createFromParcel(Parcel source) {return new TagEntity(source);}

        public TagEntity[] newArray(int size) {return new TagEntity[size];}
    };
}
