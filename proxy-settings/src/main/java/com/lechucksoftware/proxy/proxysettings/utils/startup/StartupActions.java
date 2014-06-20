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
                new StartupCondition(0, null, null));
        availableActions.add(quickTour);

        // SHOW Whats new at first start for 2.15 version
        StartupAction whatsNew215 = new StartupAction(context,
                StartupActionType.WHATSNEW_215,
                StartupActionStatus.NOT_AVAILABLE,
                new StartupCondition(null, null, 1300215));
        availableActions.add(whatsNew215);

        StartupAction rating = new StartupAction(context,
                StartupActionType.RATE_DIALOG,
                StartupActionStatus.NOT_AVAILABLE,
                new StartupCondition(50, null, null),
                new StartupCondition(70, null, null),
                new StartupCondition(90, null, null),
                new StartupCondition(null, 50, null));
        availableActions.add(rating);

        StartupAction betaTest = new StartupAction(context,
                StartupActionType.BETA_TEST_DIALOG,
                StartupActionStatus.NOT_AVAILABLE,
                new StartupCondition(100, null, null),
                new StartupCondition(130, null, null),
                new StartupCondition(160, null, null),
                new StartupCondition(null, 100, null));

        availableActions.add(betaTest);
    }

    public List<StartupAction> getAvailableActions()
    {
        return availableActions;
    }
}
