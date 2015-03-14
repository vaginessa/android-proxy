package com.lechucksoftware.proxy.proxysettings.constants;

/**
 * Created by Marco on 11/02/14.
 */
public interface Requests
{
    /**
     * Request to update a proxy's TAGs
     */
    int PROXY_TAGS_UPDATE = 1;

    /**
     * Request to update Wi-Fi access points after change on Proxy in use
     */
    int UPDATE_LINKED_WIFI_AP = 2;

    /**
     * Request to create a new proxy configuration after showing dialog that no proxies are defined
     * */
    int CREATE_NEW_PROXY = 3;

    /**
     * Request to select a proxy configuration
     * */
    int SELECT_PROXY_FOR_WIFI_NETWORK = 4;
}
