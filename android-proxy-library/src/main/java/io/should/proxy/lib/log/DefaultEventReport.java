package io.should.proxy.lib.log;

import com.shouldit.proxy.lib.APL;

/**
 * Created by Marco on 16/11/13.
 */
public class DefaultEventReport implements IEventReporting
{
    private static String TAG = DefaultEventReport.class.getSimpleName();

    @Override
    public void send(Exception e)
    {
        APL.getLogger().e(TAG, e.toString());
    }

    @Override
    public void send(String s)
    {
        APL.getLogger().e(TAG, s);
    }
}
