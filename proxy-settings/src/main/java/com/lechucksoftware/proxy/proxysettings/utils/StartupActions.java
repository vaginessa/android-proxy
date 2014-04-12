package com.lechucksoftware.proxy.proxysettings.utils;

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

        availableActions.add(new StartupAction(context, StartupActionType.RATE_DIALOG, StartupActionStatus.NOT_AVAILABLE, 1, null));
        availableActions.add(new StartupAction(context, StartupActionType.BETA_TEST_DIALOG, StartupActionStatus.NOT_AVAILABLE, 2, null));
    }

    public List<StartupAction> getAvailableActions()
    {
        return availableActions;
    }

//    for (StartupActionType actionType: StartupActionType.values())
//    {
//        StartupAction a = new StartupAction(actionType, StartupActionStatus.NOT_AVAILABLE);
//        availableActions.add(a);
//    }

}
