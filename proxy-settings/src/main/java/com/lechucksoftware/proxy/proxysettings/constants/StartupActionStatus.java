package com.lechucksoftware.proxy.proxysettings.constants;

/**
 * Created by Marco on 12/04/14.
 */
public enum StartupActionStatus
{
    DONE(1),
    REJECTED(2),
    POSTPONED(3),
    NOT_AVAILABLE(0);

    private final Integer value;

    StartupActionStatus(int val)
    {
        this.value = val;
    }

    public Integer getValue()
    {
        return value;
    }

    public static StartupActionStatus parseInt(int val)
    {
        StartupActionStatus result = NOT_AVAILABLE;

        for (StartupActionStatus status : StartupActionStatus.values())
        {
            if (status.value == val)
            {
                result = status;
                break;
            }
        }

        return result;
    }
}
