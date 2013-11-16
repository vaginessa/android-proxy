package com.shouldit.proxy.lib.log;

/**
 * Created by Marco on 16/11/13.
 */
public interface IExceptionReport
{
    public void send(Exception e);
    public void addExtraData(String s1, String s2);
    public void send(String s);
}
