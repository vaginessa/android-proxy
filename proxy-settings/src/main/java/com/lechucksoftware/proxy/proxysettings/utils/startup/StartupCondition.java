package com.lechucksoftware.proxy.proxysettings.utils.startup;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Marco on 20/04/14.
 */
public class StartupCondition implements Parcelable
{
    public Integer launchCount;
    public Integer launchDays;
    public Integer requiredVerCode;

    public StartupCondition(Integer count, Integer days, Integer versionCode)
    {
        launchCount = count;
        launchDays = days;
        requiredVerCode = versionCode;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeValue(this.launchCount);
        dest.writeValue(this.launchDays);
        dest.writeValue(this.requiredVerCode);
    }

    private StartupCondition(Parcel in)
    {
        this.launchCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.launchDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.requiredVerCode = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<StartupCondition> CREATOR = new Creator<StartupCondition>()
    {
        public StartupCondition createFromParcel(Parcel source) {return new StartupCondition(source);}

        public StartupCondition[] newArray(int size) {return new StartupCondition[size];}
    };

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof StartupCondition)) return false;

        StartupCondition that = (StartupCondition) o;

        if (launchCount != null ? !launchCount.equals(that.launchCount) : that.launchCount != null)
            return false;
        if (launchDays != null ? !launchDays.equals(that.launchDays) : that.launchDays != null)
            return false;
        if (requiredVerCode != null ? !requiredVerCode.equals(that.requiredVerCode) : that.requiredVerCode != null)
            return false;

        return true;
    }
}
