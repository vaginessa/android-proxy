package com.lechucksoftware.proxy.proxysettings;

import com.lechucksoftware.proxy.proxysettings.constants.StatusFragmentStates;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.StatusFragment;

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

    public void setStatus(StatusFragmentStates status)
    {
        StatusFragment.getInstance().setStatus(status);
    }

    public void setStatus(StatusFragmentStates status, String message)
    {
        StatusFragment.getInstance().setStatus(status, message);
    }

    public void setStatus(StatusFragmentStates status, String message, Boolean isInProgress)
    {
        StatusFragment.getInstance().setStatus(status, message, isInProgress);
    }

    public void hide()
    {
        StatusFragment.getInstance().hide();
    }
}
