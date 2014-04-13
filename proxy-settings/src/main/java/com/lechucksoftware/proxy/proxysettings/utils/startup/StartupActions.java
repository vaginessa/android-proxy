package com.lechucksoftware.proxy.proxysettings.utils.startup;

import android.content.Context;

import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 12/04/14.
 */
public class StartupActions
{
    private Context context;
    List<StartupAction> availableActions;

    public StartupActions(Context ctx)
    {
        availableActions = new ArrayList<StartupAction>();
        context = ctx;

        StartupAction rating = new StartupAction(context,
                                                 StartupActionType.RATE_DIALOG,
                                                 StartupActionStatus.NOT_AVAILABLE,
                                                 1,
                                                 null);

        StartupAction betaTest = new StartupAction(context,
                                                   StartupActionType.BETA_TEST_DIALOG,
                                                   StartupActionStatus.NOT_AVAILABLE,
                                                   2,
                                                   null);

        availableActions.add(rating);
        availableActions.add(betaTest);
    }

    public List<StartupAction> getAvailableActions()
    {
        return availableActions;
    }
}
