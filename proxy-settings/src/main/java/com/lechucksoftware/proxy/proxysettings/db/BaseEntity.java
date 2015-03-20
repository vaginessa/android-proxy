package com.lechucksoftware.proxy.proxysettings.db;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Marco on 23/10/13.
 */
public class BaseEntity implements Parcelable
{
    private UUID uuid;
    private long id;
    private long creationDate;
    private long lastModifiedDate;
    private boolean isPersisted;
    private boolean isSelected;

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeSerializable(this.uuid);
        dest.writeLong(this.id);
        dest.writeLong(this.creationDate);
        dest.writeLong(this.lastModifiedDate);
        dest.writeByte(isPersisted ? (byte) 1 : (byte) 0);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
    }

    public BaseEntity(Parcel in)
    {
        this.uuid = (UUID) in.readSerializable();
        this.id = in.readLong();
        this.creationDate = in.readLong();
        this.lastModifiedDate = in.readLong();
        this.isPersisted = in.readByte() != 0;
        this.isSelected = in.readByte() != 0;
    }

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

    @Override
    public int describeContents() { return 0; }


}
