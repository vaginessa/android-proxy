package com.lechucksoftware.proxy.proxysettings.db;

import com.lechucksoftware.proxy.proxysettings.App;

import java.io.Serializable;
import java.lang.reflect.Field;

import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;

/**
 * Created by Marco on 13/09/13.
 */
public class WiFiAPEntity extends BaseEntity implements Serializable
{
    private String ssid;
    private SecurityType securityType;

    private ProxySetting proxySetting;

    private Long proxyId;
    private Long pacId;

    private ProxyEntity proxyEntity;
    private PacEntity pacEntity;

    public WiFiAPEntity()
    {
        super();

        proxyId = -1L;
        proxyEntity = null;

        pacId = -1L;
        pacEntity =  null;
    }

    public WiFiAPEntity(WiFiAPEntity ap)
    {
        super();
        this.setSsid(ap.getSsid());
        this.setSecurityType(ap.getSecurityType());
        this.setProxySetting(ap.getProxySetting());

        this.proxyId = ap.proxyId;
        this.proxyEntity = ap.proxyEntity;

        this.pacId = ap.pacId;
        this.pacEntity = ap.pacEntity;
    }

    public void setProxy(ProxyEntity proxy)
    {
        ProxyEntity upsertedProxy = App.getDBManager().upsertProxy(proxy);
        proxyEntity = upsertedProxy;
        proxyId = upsertedProxy.getId();
    }

    public void setProxyPAC(PacEntity pac)
    {
        PacEntity upsertedPac = App.getDBManager().upsertPac(pac);
        pacEntity = upsertedPac;
        pacId = upsertedPac.getId();
    }

    public ProxyEntity getProxy()
    {
        return proxyEntity;
    }

    public Long getProxyId()
    {
        return proxyId;
    }

    public void setProxyId(Long id)
    {
        proxyId = id;

        if (id != -1)
        {
            proxyEntity = App.getDBManager().getProxy(id);
        }
        else
        {
            proxyEntity = null;
        }
    }

    public void setPACId(Long id)
    {
        pacId = id;

        if (id != -1)
        {
            pacEntity = App.getDBManager().getPac(id);
        }
        else
        {
            pacEntity = null;
        }
    }

    @Override
    public boolean equals(Object another)
    {
        Boolean result = false;

        if ((another instanceof WiFiAPEntity))
        {
            WiFiAPEntity otherAp = (WiFiAPEntity) another;

            if (this.isPersisted() && otherAp.isPersisted())
            {
                return otherAp.getId() == this.getId();
            }
            else
            {
                if (otherAp.getSsid().equalsIgnoreCase(this.getSsid())
                        && otherAp.getSecurityType().equals(this.getSecurityType()))
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
        sb.append(String.format("'%s' - '%s', ", getSsid(), getSecurityType()));

        switch (proxySetting)
        {
            case NONE:
            case UNASSIGNED:
                sb.append(String.format("Proxy NOT enabled (%s)", proxySetting.toString()));
                break;

            case STATIC:
                if (proxyId != null && proxyId != -1)
                {
                    sb.append(getProxy().toString());
                }
                else
                {
                    sb.append("STATIC proxy enabled but not assigned");
                }
                break;

            case PAC:
                if (pacId != null && pacId != -1)
                {
                    sb.append(getProxyPAC().toString());
                }
                else
                {
                    sb.append("PAC proxy enabled but not assigned");
                }
                break;

            default:
                sb.append(String.format("Inconsistent proxySetting value (%s)", proxySetting.toString()));
        }

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
                sb.append(String.format("%s: %s ", name, value));
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public String getSsid()
    {
        return ssid;
    }

    public void setSsid(String ssid)
    {
        this.ssid = ssid;
    }

    public SecurityType getSecurityType()
    {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType)
    {
        this.securityType = securityType;
    }

    public ProxySetting getProxySetting()
    {
        return proxySetting;
    }

    public void setProxySetting(ProxySetting proxySetting)
    {
        this.proxySetting = proxySetting;
    }

    public PacEntity getProxyPAC()
    {
        return this.pacEntity;
    }

    public Long getPacId()
    {
        return pacId;
    }
}
