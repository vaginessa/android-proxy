package com.lechucksoftware.proxy.proxysettings.utils.startup;

import android.os.Parcel;
import android.os.Parcelable;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.StartupConditionType;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

/**
 * Created by Marco on 20/04/14.
 */
public class StartupCondition implements Parcelable
{
    public StartupConditionType conditionType;
    public Integer launchCount;
    public Integer launchDays;
    public Integer requiredVerCode;

    public static StartupCondition ElapsedDaysCondition(Integer days)
    {
        return new StartupCondition(StartupConditionType.ELAPSED_DAYS, days);
    }

    public static StartupCondition LaunchCountCondition(Integer count)
    {
        return new StartupCondition(StartupConditionType.LAUNCH_COUNT, count);
    }

    public static StartupCondition RequiredVersionCondition(Integer count)
    {
        return new StartupCondition(StartupConditionType.REQUIRED_VERSION, count);
    }

    private StartupCondition(StartupConditionType condType, Object ... args)
    {
        launchCount = -1;
        launchDays = -1;
        requiredVerCode = -1;
        conditionType = condType;

        switch (conditionType)
        {
            case ELAPSED_DAYS:
                launchDays = (Integer) args[0];
                break;

            case LAUNCH_COUNT:
                launchCount = (Integer) args[0];
                break;

            case REQUIRED_VERSION:
                requiredVerCode = (Integer) args[0];
                break;
        }
    }

    public boolean isValid()
    {
        Boolean result = false;

        switch (conditionType)
        {
            case ELAPSED_DAYS:
                checkElapsedDays(launchDays);
                break;

            case LAUNCH_COUNT:
                result = checkLaunchCount(launchCount);
                break;

            case REQUIRED_VERSION:
                result = checkRequiredAppVersion(requiredVerCode);
                break;
        }

        return result;
    }

    private static boolean checkRequiredAppVersion(Integer requiredVerCode)
    {
        Boolean result = false;

        if (requiredVerCode == null)
        {
            result = true;
        }
        else if (App.getAppStats().majorVersion == requiredVerCode)
        {
            result = true;
        }

        return result;
    }

    public static Boolean checkLaunchCount(Integer launchCount)
    {
        Boolean result = false;

        if (App.getAppStats().launchCount >= launchCount)
        {
            result = true;
        }

        return result;
    }

    public static Boolean checkElapsedDays(Integer daysCount)
    {
        Boolean result = false;

        if (Utils.ElapsedNDays(App.getAppStats().launhcFirstDate, daysCount))
        {
            result = true;
        }

        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof StartupCondition)) return false;

        StartupCondition that = (StartupCondition) o;

        if (conditionType != that.conditionType) return false;
        if (launchCount != null ? !launchCount.equals(that.launchCount) : that.launchCount != null)
            return false;
        if (launchDays != null ? !launchDays.equals(that.launchDays) : that.launchDays != null)
            return false;
        return !(requiredVerCode != null ? !requiredVerCode.equals(that.requiredVerCode) : that.requiredVerCode != null);
    }

    @Override
    public int hashCode()
    {
        int result = conditionType != null ? conditionType.hashCode() : 0;
        result = 31 * result + (launchCount != null ? launchCount.hashCode() : 0);
        result = 31 * result + (launchDays != null ? launchDays.hashCode() : 0);
        result = 31 * result + (requiredVerCode != null ? requiredVerCode.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.conditionType == null ? -1 : this.conditionType.ordinal());
        dest.writeValue(this.launchCount);
        dest.writeValue(this.launchDays);
        dest.writeValue(this.requiredVerCode);
    }

    private StartupCondition(Parcel in)
    {
        int tmpConditionType = in.readInt();
        this.conditionType = tmpConditionType == -1 ? null : StartupConditionType.values()[tmpConditionType];
        this.launchCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.launchDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.requiredVerCode = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<StartupCondition> CREATOR = new Creator<StartupCondition>()
    {
        public StartupCondition createFromParcel(Parcel source) {return new StartupCondition(source);}

        public StartupCondition[] newArray(int size) {return new StartupCondition[size];}
    };


}
