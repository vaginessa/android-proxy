package com.shouldit.proxy.lib.log;

/**
 * Created by Marco on 16/11/13.
 */
public class DefaultExceptionReport implements IExceptionReport
{
    private static String TAG = DefaultExceptionReport.class.getSimpleName();

    @Override
    public void send(Exception e)
    {
        LogWrapper.e(TAG, e.toString());
    }

    @Override
    public void addExtraData(String s1, String s2)
    {
        LogWrapper.e(TAG, s1);
        LogWrapper.e(TAG, s2);
    }

    @Override
    public void send(String s)
    {
        LogWrapper.e(TAG, s);
    }
}
