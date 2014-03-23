package com.lechucksoftware.proxy.proxysettings.exception;

import android.text.TextUtils;
import com.google.analytics.tracking.android.ExceptionParser;

/**
 * Created by Marco on 20/01/14.
 */
public class DetailedExceptionParser implements ExceptionParser
{
    @Override
    public String getDescription(String threadName, Throwable t)
    {
        String description = "threadName = " + threadName
                + "\ngetMessage()= " + t.getMessage()
                + "\ngetCause()=" + t.getCause()
                + "\ngetStackTrace()=" + TextUtils.join("\n",t.getStackTrace());

        return description;
    }
}
