package be.shouldit.proxy.lib.log;

/**
 * Created by Marco on 16/11/13.
 */
public interface IEventReporting
{
    public void sendException(Exception e);
    public void sendEvent(String s);
}
