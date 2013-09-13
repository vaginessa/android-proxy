package com.lechucksoftware.proxy.proxysettings.db;

/**
 * Created by Marco on 13/09/13.
 */
public class ProxyData
{
    private long id;
    public String host;
    public int port;
    public String exclusion;
    public String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return host + ":" + port;
    }
}
