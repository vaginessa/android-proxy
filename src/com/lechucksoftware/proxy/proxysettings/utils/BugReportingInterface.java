package com.lechucksoftware.proxy.proxysettings.utils;

/**
 * Created by Marco on 16/11/13.
 */
public interface BugReportingInterface
{
    public void send(Exception e);
    public void addExtraData(String s1, String s2);
    public void send(String s);
}
