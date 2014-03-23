package com.lechucksoftware.proxy.proxysettings.db;

/**
 * Created by Marco on 13/09/13.
 */
public class ProxyTagLinkEntity extends BaseEntity
{
    public long proxyId;
    public long tagId;

    public ProxyTagLinkEntity()
    {
        super();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("P:%d-T:%d", proxyId, tagId));
        return sb.toString();
    }
}
