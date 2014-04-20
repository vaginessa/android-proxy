package com.lechucksoftware.proxy.proxysettings.utils.startup;

/**
 * Created by Marco on 20/04/14.
 */
public class StartupCondition
{
    public Integer launchCount;
    public Integer launchDays;

    public StartupCondition(Integer count, Integer days)
    {
        launchCount = count;
        launchDays = days;
    }
}
