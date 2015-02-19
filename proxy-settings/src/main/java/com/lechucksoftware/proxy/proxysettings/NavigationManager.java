package com.lechucksoftware.proxy.proxysettings;

import android.content.Context;
import android.os.Build;

import com.lechucksoftware.proxy.proxysettings.constants.NavigationAction;
import com.lechucksoftware.proxy.proxysettings.ui.components.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by mpagliar on 2/19/2015.
 */
public class NavigationManager
{
    private final Context context;
    private ArrayList<NavigationAction> navigationActionsList;

    public NavigationManager(Context ctx)
    {
        context = ctx;

        initNavigationActionMap();
    }

    public void initNavigationActionMap()
    {
        navigationActionsList = new ArrayList<>();

        navigationActionsList.add(NavigationAction.WIFI_NETWORKS);
        navigationActionsList.add(NavigationAction.HTTP_PROXIES_LIST);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            navigationActionsList.add(NavigationAction.PAC_PROXIES_LIST);
        }

        navigationActionsList.add(NavigationAction.HELP);

        if (BuildConfig.DEBUG)
        {
            navigationActionsList.add(NavigationAction.DEVELOPER);
        }
    }

    public NavigationAction getAction(int position)
    {
        if (navigationActionsList.size() > position)
            return navigationActionsList.get(position);
        else
            return NavigationAction.NOT_DEFINED;
    }

    public List<NavDrawerItem> getNavigationDrawerItems()
    {
        List<NavDrawerItem> items = new ArrayList<>();

        int wifiNetworksNum = 0;
        int staticProxyNum = 0;
        int pacProxyNum = 0;

        try
        {
            wifiNetworksNum = (int) App.getDBManager().getWifiApCount();
            staticProxyNum = (int) App.getDBManager().getProxiesCount();
            pacProxyNum = (int) App.getDBManager().getPacCount();
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception retrieving NavDrawersItems counters");
        }

        for(int i=0; i<navigationActionsList.size(); i++)
        {
            NavigationAction action = navigationActionsList.get(i);
            NavDrawerItem navDrawerItem = null;

            switch (action)
            {
                case WIFI_NETWORKS:
                    navDrawerItem = new NavDrawerItem(NavigationAction.WIFI_NETWORKS, context.getString(R.string.wifi_access_points), R.drawable.ic_wifi_action_light, wifiNetworksNum);
                    break;

                case HTTP_PROXIES_LIST:
                    navDrawerItem = new NavDrawerItem(NavigationAction.HTTP_PROXIES_LIST, context.getString(R.string.static_proxies), R.drawable.ic_action_proxy_light, staticProxyNum);
                    break;

                case PAC_PROXIES_LIST:
                    navDrawerItem = new NavDrawerItem(NavigationAction.PAC_PROXIES_LIST, context.getString(R.string.pac_proxies), R.drawable.ic_action_pac_light, pacProxyNum);
                    break;

                case HELP:
                    navDrawerItem = new NavDrawerItem(NavigationAction.HELP, context.getString(R.string.help), R.drawable.ic_action_action_help_light);
                    break;

                case DEVELOPER:
                    navDrawerItem = new NavDrawerItem(NavigationAction.DEVELOPER, context.getString(R.string.developers_options), R.drawable.ic_action_developer_light);
                    break;
            }

            items.add(navDrawerItem);
        }

        return items;
    }
}
