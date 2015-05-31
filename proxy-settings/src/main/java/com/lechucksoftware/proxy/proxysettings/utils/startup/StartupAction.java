package com.lechucksoftware.proxy.proxysettings.utils.startup;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;
import com.lechucksoftware.proxy.proxysettings.utils.ApplicationStatistics;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import java.util.Arrays;

/**
 * Created by Marco on 12/04/14.
 */
public class StartupAction implements Parcelable
{
    private static String keyPrefix = "STARTUP_ACTION_";

    public String preferenceKey;
    public StartupActionType actionType;
    public StartupActionStatus actionStatus;

    public StartupCondition [] startupConditions;

    public StartupAction(StartupActionType type, StartupActionStatus status, StartupCondition ... conditions)
    {
        actionType = type;
        actionStatus = status;
        preferenceKey = keyPrefix + actionType;
        startupConditions = conditions;
    }

    public void updateStatus(StartupActionStatus status)
    {
        SharedPreferences prefs = App.getInstance().getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = prefs.edit();

        if (editor != null)
        {
            editor.putInt(preferenceKey, status.getValue());
            editor.commit();

            App.getEventsReporter().sendEvent(App.getInstance().getString(R.string.analytics_cat_user_action), App.getInstance().getString(R.string.analytics_act_startup_action), preferenceKey, (long) status.getValue());
        }
    }

    public boolean canExecute()
    {
        SharedPreferences prefs = App.getInstance().getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
        StartupActionStatus status = StartupActionStatus.parseInt(prefs.getInt(preferenceKey, StartupActionStatus.NOT_AVAILABLE.getValue()));

        Boolean result;

        switch (status)
        {
            case NOT_AVAILABLE:
            case POSTPONED:
                result = checkInstallationConditions(startupConditions);
                break;

            case REJECTED:
            case DONE:
            case NOT_APPLICABLE:
            default:
                result = false;
        }

        return result;
    }

    public static Boolean checkInstallationConditions(StartupCondition [] conditions)
    {
        Boolean result = false;

        if (conditions != null)
        {
            for (StartupCondition condition: conditions)
            {
                result = condition.isValid();
                if (result)
                    break;
            }
        }

        return result;
    }

    @Override
    public String toString()
    {
        return String.format("%s: %s %s", preferenceKey, actionType, actionStatus);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.preferenceKey);
        dest.writeInt(this.actionType == null ? -1 : this.actionType.ordinal());
        dest.writeInt(this.actionStatus == null ? -1 : this.actionStatus.ordinal());

        dest.writeInt(this.startupConditions.length);
        dest.writeTypedArray(this.startupConditions, flags);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof StartupAction)) return false;

        StartupAction that = (StartupAction) o;

        if (actionStatus != that.actionStatus) return false;
        if (actionType != that.actionType) return false;
        if (preferenceKey != null ? !preferenceKey.equals(that.preferenceKey) : that.preferenceKey != null)
            return false;

        if (!Arrays.equals(startupConditions, that.startupConditions)) return false;

        return true;
    }

    private StartupAction(Parcel in)
    {
        this.preferenceKey = in.readString();
        int tmpActionType = in.readInt();
        this.actionType = tmpActionType == -1 ? null : StartupActionType.values()[tmpActionType];
        int tmpActionStatus = in.readInt();
        this.actionStatus = tmpActionStatus == -1 ? null : StartupActionStatus.values()[tmpActionStatus];

        this.startupConditions = new StartupCondition[in.readInt()];
        in.readTypedArray(this.startupConditions, StartupCondition.CREATOR);
    }

    public static final Creator<StartupAction> CREATOR = new Creator<StartupAction>()
    {
        public StartupAction createFromParcel(Parcel source) {return new StartupAction(source);}

        public StartupAction[] newArray(int size) {return new StartupAction[size];}
    };
}
