package com.lechucksoftware.proxy.proxysettings.constants;

/**
 * Created by Marco on 20/01/14.
 */
public enum NavigationAction
{
    WIFI_NETWORKS(0),
    HTTP_PROXIES_LIST(1),
    PAC_PROXIES_LIST(2),
    HELP(3),
    DEVELOPER(4),
    NOT_DEFINED(-1);

    private final Integer value;

    NavigationAction(int val)
    {
        this.value = val;
    }

    public Integer getValue()
    {
        return value;
    }

    public static NavigationAction parseInt(int val)
    {
        NavigationAction result = NOT_DEFINED;

        for (NavigationAction status : NavigationAction.values())
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
