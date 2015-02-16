package com.lechucksoftware.proxy.proxysettings.ui.components;

import com.lechucksoftware.proxy.proxysettings.constants.NavigationAction;

/**
 * Created by mpagliar on 21/10/2014.
 */
public class NavDrawerItem
{
    private NavigationAction action;
    private String title;
    private String tag;
    private int icon;
    private String count = "0";
    // boolean to set visibility of the counter
    private boolean isCounterVisible = false;

    public NavDrawerItem(NavigationAction action, String title, String tag, int icon)
    {
        this.action = action;
        this.title = title;
        this.tag = tag;
        this.icon = icon;
    }

    public NavDrawerItem(NavigationAction action, String title, String tag, int icon, boolean isCounterVisible, String count)
    {
        this.action = action;
        this.title = title;
        this.tag = tag;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }

    public NavigationAction getAction()
    {
        return action;
    }

    public void setAction(NavigationAction action)
    {
        this.action = action;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getTag()
    {
        return this.tag;
    }

    public int getIcon()
    {
        return this.icon;
    }

    public String getCount()
    {
        return this.count;
    }

    public boolean getCounterVisibility()
    {
        return this.isCounterVisible;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public void setCount(String count)
    {
        this.count = count;
    }

    public void setCounterVisibility(boolean isCounterVisible)
    {
        this.isCounterVisible = isCounterVisible;
    }

    @Override
    public boolean equals(Object another)
    {
        Boolean result = false;

        if ((another instanceof NavDrawerItem))
        {
            NavDrawerItem anotherItem = (NavDrawerItem) another;

            if (anotherItem.getAction().equals(this.action)
                    && anotherItem.getCount() == this.count
                    && anotherItem.getTag() == this.tag
                    && anotherItem.getTitle() == this.title
                    && anotherItem.getIcon() == this.icon)
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }

        return result;
    }
}

