package com.lechucksoftware.proxy.proxysettings.exception;

import android.text.TextUtils;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import be.shouldit.proxy.lib.ProxyConfiguration;

import java.util.List;

/**
 * Created by Marco on 20/01/14.
 */
public class ProxyException extends Exception
{
    List<ProxyConfiguration> configurationList = null;

    public ProxyException()
    {

    }

    public ProxyException(List<ProxyConfiguration> configurations)
    {
        configurationList = configurations;
    }

    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder();
        String msg = super.getMessage();
        if (!TextUtils.isEmpty(msg))
            sb.append(msg);

        try
        {
            if (configurationList != null && configurationList.size() > 0)
            {
                for (ProxyConfiguration conf : configurationList)
                {
                    sb.append(conf.toShortString());
                    sb.append("\n");
                }
            }
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
        }

        return sb.toString();
    }
}
