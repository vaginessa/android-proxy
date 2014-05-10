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

        // SHOW Quick tour at first start
        StartupAction quickTour = new StartupAction(context,
                StartupActionType.FIRST_QUICK_TOUR,
                StartupActionStatus.NOT_AVAILABLE,
                new StartupCondition(0, null));
        availableActions.add(quickTour);

        StartupAction rating = new StartupAction(context,
                StartupActionType.RATE_DIALOG,
                StartupActionStatus.NOT_AVAILABLE,
                new StartupCondition(50, null),
                new StartupCondition(70, null),
                new StartupCondition(90, null),
                new StartupCondition(null, 50));
        availableActions.add(rating);

        StartupAction betaTest = new StartupAction(context,
                StartupActionType.BETA_TEST_DIALOG,
                StartupActionStatus.NOT_AVAILABLE,
                new StartupCondition(100, null),
                new StartupCondition(130, null),
                new StartupCondition(160, null),
                new StartupCondition(null, 100));

        availableActions.add(betaTest);
    }

    public List<StartupAction> getAvailableActions()
    {
        return availableActions;
    }
}
