package be.shouldit.proxy.lib.log;

import be.shouldit.proxy.lib.APL;

/**
 * Created by Marco on 16/11/13.
 */
public class DefaultEventReport implements IEventReporting
{
    private static String TAG = DefaultEventReport.class.getSimpleName();

    @Override
    public void sendException(Exception e)
    {
        APL.getLogger().e(TAG, e.toString());
    }

    @Override
    public void sendEvent(String s)
    {
        APL.getLogger().e(TAG, s);
    }
}
