package com.lechucksoftware.proxy.proxysettings;

import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;

/**
 * Created by Marco on 06/08/13.
 */
public class ActionManager
{
    private static ActionManager instance;

    /**
     * Create a new instance of ActionManager
     */
    public static ActionManager getInstance()
    {
        if (instance == null)
            instance = new ActionManager();

        return instance;
    }

    public void refreshUI()
    {
        StatusFragment.getInstance().refreshUI();
    }
}
