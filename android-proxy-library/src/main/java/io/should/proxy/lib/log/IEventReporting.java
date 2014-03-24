package io.should.proxy.lib.log;

/**
 * Created by Marco on 16/11/13.
 */
public interface IEventReporting
{
    public void send(Exception e);
    public void send(String s);
}
