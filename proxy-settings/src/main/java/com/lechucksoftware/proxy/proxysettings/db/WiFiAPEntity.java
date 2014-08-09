package com.lechucksoftware.proxy.proxysettings.db;

import android.text.TextUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import be.shouldit.proxy.lib.enums.SecurityType;

/**
 * Created by Marco on 13/09/13.
 */
public class WiFiAPEntity extends BaseEntity implements Serializable
{
    public String ssid;
    public SecurityType securityType;
    public boolean proxyEnabled;
    public int proxyId;


    public WiFiAPEntity()
    {
        super();
    }

    public WiFiAPEntity(WiFiAPEntity ap)
    {
        super();
        this.ssid = ap.ssid;
        this.securityType = ap.securityType;
        this.proxyEnabled = ap.proxyEnabled;
        this.proxyId = ap.proxyId;
    }

    @Override
    public boolean equals(Object another)
    {
        Boolean result = false;

        if ((another instanceof WiFiAPEntity))
        {
            WiFiAPEntity otherAp = (WiFiAPEntity) another;

            if (this.isPersisted && otherAp.isPersisted)
            {
                return otherAp.getId() == this.getId();
            }
            else
            {
                if (otherAp.ssid.equalsIgnoreCase(this.ssid)
                       && otherAp.securityType.equals(this.securityType))
                {
                    result = true;
                }
                else
                {
                    result = false;
                }
            }
        }

        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s - %d", ssid, securityType));
        return sb.toString();
    }

    public String getDebugInfo()
    {
        StringBuilder sb = new StringBuilder();
        for (Field f : WiFiAPEntity.class.getFields())
        {
            try
            {
                String name = f.getName();
                String value = f.get(this).toString();
                sb.append(String.format("%s: %s ",name,value));
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
