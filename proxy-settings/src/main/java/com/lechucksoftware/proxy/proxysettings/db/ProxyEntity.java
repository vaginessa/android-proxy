package com.lechucksoftware.proxy.proxysettings.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/09/13.
 */
public class ProxyEntity extends BaseEntity implements Parcelable, Comparable<ProxyEntity>
{
    private String host;
    private Integer port;
    private String exclusion;
    private List<TagEntity> tags;
    private String countryCode;
    private int usedByCount;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest,flags);

        dest.writeString(this.host);
        dest.writeValue(this.port);
        dest.writeString(this.exclusion);
        dest.writeList(this.tags);
        dest.writeString(this.countryCode);
        dest.writeInt(this.usedByCount);
    }

    private ProxyEntity(Parcel in)
    {
        super(in);

        this.host = in.readString();
        this.port = (Integer) in.readValue(Integer.class.getClassLoader());
        this.exclusion = in.readString();
        this.tags = new ArrayList<TagEntity>();
        in.readList(this.tags, TagEntity.class.getClassLoader());
        this.countryCode = in.readString();
        this.usedByCount = in.readInt();
    }

    public static final Creator<ProxyEntity> CREATOR = new Creator<ProxyEntity>()
    {
        public ProxyEntity createFromParcel(Parcel source) {return new ProxyEntity(source);}

        public ProxyEntity[] newArray(int size) {return new ProxyEntity[size];}
    };

    public ProxyEntity()
    {
        super();
        tags = new ArrayList<TagEntity>();
        exclusion = "";
        countryCode = null;
        usedByCount = -1;
    }

    public ProxyEntity(ProxyEntity proxy)
    {
        super();
        this.host = proxy.host;
        this.port = proxy.port;
        this.exclusion = proxy.exclusion;
        this.countryCode = proxy.countryCode;
        this.tags = new ArrayList<TagEntity>();
        this.usedByCount = proxy.usedByCount;

        for(TagEntity t : proxy.tags)
        {
            this.tags.add(new TagEntity(t));
        }
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getExclusion()
    {
        return exclusion;
    }

    public void setExclusion(String exclusion)
    {
        this.exclusion = exclusion;
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ProxyEntity)) return false;

        ProxyEntity that = (ProxyEntity) o;

        if (usedByCount != that.usedByCount)
            return false;
        if (countryCode != null ? !countryCode.equals(that.countryCode) : that.countryCode != null)
            return false;
        if (exclusion != null ? !exclusion.equals(that.exclusion) : that.exclusion != null)
            return false;
        if (host != null ? !host.equals(that.host) : that.host != null)
            return false;
        if (port != null ? !port.equals(that.port) : that.port != null)
            return false;

// TODO: compare also linked TAGS?
//        if (tags != null ? !tags.equals(that.tags) : that.tags != null)
//            return false;

        return true;
    }

//    @Override
//    public boolean equals(Object another)
//    {
//        Boolean result = false;
//
//        if ((another instanceof ProxyEntity))
//        {
//            ProxyEntity anotherProxy = (ProxyEntity) another;
//
////            if (this.isPersisted() && anotherProxy.isPersisted())
////            {
////                return anotherProxy.getId() == this.getId();
////            }
////            else
////            {
//                if (anotherProxy.host.equalsIgnoreCase(this.host)
//                       && anotherProxy.port.equals(this.port)
//                       && anotherProxy.exclusion.equalsIgnoreCase(this.exclusion)
//                       && anotherProxy.getInUse() == this.getInUse())
//                {
//                    // TODO: compare also linked TAGS?
//                    result = true;
//                }
//                else
//                {
//                    result = false;
//                }
////            }
//        }
//
//        return result;
//    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("HTTP Proxy: '%s:%d'", host, port));
        sb.append(String.format(" used by %s AP", usedByCount));

        if (getTags() != null)
        {
            sb.append(" TAGS: ");
            for(TagEntity tag: getTags())
            {
                sb.append(tag.toString());
                sb.append(" ");
            }
        }

        if (!TextUtils.isEmpty(exclusion)) sb.append(String.format(" (EX: %s)",exclusion));
        if (!TextUtils.isEmpty(countryCode)) sb.append(String.format(" (COUNTRY: %s)",countryCode));

        return sb.toString();
    }

    public String getDebugInfo()
    {
        StringBuilder sb = new StringBuilder();
        for (Field f : ProxyEntity.class.getFields())
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

    public List<TagEntity> getTags()
    {
        return tags;
    }

    public void setTags(List<TagEntity> tags)
    {
        this.tags = tags;
    }

    public void addTag(TagEntity tag)
    {
        if (!this.tags.contains(tag))
            this.tags.add(tag);
    }

    public void addTags(List<TagEntity> tags)
    {
        for(TagEntity tag : tags)
        {
            addTag(tag);
        }
    }

    public TagEntity removeTag(TagEntity tag)
    {
        int indexOf = this.tags.indexOf(tag);

        if (indexOf >= 0 && indexOf < this.tags.size())
        {
            return this.tags.remove(indexOf);
        }
        else
            return null;
    }

    public void setUsedByCount(int usedBy)
    {
        this.usedByCount = usedBy;
    }

    @Override
    public int compareTo(ProxyEntity proxyEntity)
    {
        int result = this.host.compareTo(proxyEntity.host);
        return result;
    }
}
