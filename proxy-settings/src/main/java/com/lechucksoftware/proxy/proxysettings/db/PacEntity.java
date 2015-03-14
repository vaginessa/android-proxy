package com.lechucksoftware.proxy.proxysettings.db;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Created by Marco on 13/09/13.
 */
public class PacEntity extends BaseEntity implements Parcelable, Comparable<PacEntity>
{
    private Uri pacUriFile;
    private int usedByCount;

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest,flags);

        dest.writeParcelable(this.pacUriFile, 0);
        dest.writeInt(this.usedByCount);
    }

    private PacEntity(Parcel in)
    {
        super(in);

        this.pacUriFile = in.readParcelable(Uri.class.getClassLoader());
        this.usedByCount = in.readInt();
    }

    public static final Creator<PacEntity> CREATOR = new Creator<PacEntity>()
    {
        public PacEntity createFromParcel(Parcel source) {return new PacEntity(source);}

        public PacEntity[] newArray(int size) {return new PacEntity[size];}
    };

    public PacEntity()
    {
        super();

        pacUriFile = Uri.EMPTY;
        usedByCount = 0;
    }

    public PacEntity(PacEntity pacEntity)
    {
        super();
        this.pacUriFile = pacEntity.pacUriFile;
        this.usedByCount = pacEntity.usedByCount;
    }

    public boolean getInUse()
    {
        return usedByCount > 0;
    }

    public int getUsedByCount()
    {
        return usedByCount;
    }

    @Override
    public boolean equals(Object another)
    {
        Boolean result = false;

        if ((another instanceof PacEntity))
        {
            PacEntity anotherProxy = (PacEntity) another;

            if (anotherProxy.pacUriFile.equals(this.pacUriFile)
                   && anotherProxy.getInUse() == this.getInUse())
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }

        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("PAC: '%s'", pacUriFile.toString()));
        sb.append(String.format(" used by %s AP", usedByCount));

        return sb.toString();
    }

    public String getDebugInfo()
    {
        StringBuilder sb = new StringBuilder();
        for (Field f : PacEntity.class.getFields())
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

    public void setUsedByCount(int usedBy)
    {
        this.usedByCount = usedBy;
    }

    @Override
    public int compareTo(PacEntity pacEntity)
    {
        int result = this.pacUriFile.compareTo(pacEntity.pacUriFile);
        return result;
    }

    public void setPacUrlFile(String pacUrlFile)
    {
        pacUriFile = Uri.parse(pacUrlFile);
    }

    public Uri getPacUriFile()
    {
        return pacUriFile;
    }

    @Override
    public int describeContents() { return 0; }
}
