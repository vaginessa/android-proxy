package com.lechucksoftware.proxy.proxysettings.utils.startup;

/**
 * Created by Marco on 20/04/14.
 */
public class StartupCondition
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
}
