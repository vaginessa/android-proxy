package com.lechucksoftware.proxy.proxysettings.exception;

import android.text.TextUtils;

import com.google.android.gms.analytics.ExceptionParser;

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
