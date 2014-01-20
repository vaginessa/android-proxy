package com.shouldit.proxy.lib.log;

/**
 * Created by Marco on 16/11/13.
 */
public class DefaultEventReport implements IEventReporting
{
    private static String TAG = DefaultEventReport.class.getSimpleName();

    @Override
    public void send(Exception e)
    {
        LogWrapper.e(TAG, e.toString());
    }

    @Override
    public void send(String s)
    {
        LogWrapper.e(TAG, s);
    }
}
