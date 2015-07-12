package com.lechucksoftware.proxy.proxysettings.utils.startup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionStatus;
import com.lechucksoftware.proxy.proxysettings.constants.StartupActionType;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Marco on 12/04/14.
 */
public class StartupActions
{
    private static final String TAG = StartupActions.class.getSimpleName();
    private static Map<StartupActionType,StartupAction> availableActions;

    private static Map<StartupActionType,StartupAction> buildStartupActions()
    {
        HashMap<StartupActionType,StartupAction> actions = new HashMap<>();

        // SHOW Quick tour at first start
//        StartupAction quickTour = new StartupAction(activity,
//                StartupActionType.FIRST_QUICK_TOUR,
//                StartupActionStatus.NOT_AVAILABLE,
//                new StartupCondition(1, null, null));
//        actions.add(quickTour);

//        // SHOW Whats new at first start for 3.00 version
//        StartupAction whatsNew300 = new StartupAction(activity,
//                StartupActionType.WHATSNEW_300,
//                StartupActionStatus.NOT_AVAILABLE,
//                new StartupCondition(null, null, 1300300));
//        actions.add(whatsNew300);
//
//        // SHOW Whats new at first start for 2.16 version
//        StartupAction whatsNew216 = new StartupAction(activity,
//                StartupActionType.WHATSNEW_216,
//                StartupActionStatus.NOT_AVAILABLE,
//                new StartupCondition(null, null, 1300216));
//        actions.add(whatsNew216);

        StartupAction likeAction = new StartupAction(
                StartupActionType.RATE_DIALOG,
                StartupActionStatus.NOT_AVAILABLE,
                R.string.analytics_act_rate_dialog,
                StartupCondition.LaunchCountCondition(20,5),
                StartupCondition.ElapsedDaysCondition(60));
        actions.put(likeAction.actionType, likeAction);

        if (App.getInstance().activeMarket == AndroidMarket.PLAY)
        {
            StartupAction donateAction = new StartupAction(
                    StartupActionType.DONATE_DIALOG,
                    StartupActionStatus.NOT_AVAILABLE,
                    R.string.analytics_act_donate_dialog,
                    StartupCondition.LaunchCountCondition(40, 10));
            actions.put(donateAction.actionType, donateAction);
        }

        StartupAction betaTest = new StartupAction(
                StartupActionType.BETA_TEST_DIALOG,
                StartupActionStatus.NOT_AVAILABLE,
                R.string.analytics_act_beta_dialog,
                StartupCondition.LaunchCountCondition(200,50));
        actions.put(betaTest.actionType, betaTest);

        return actions;
    }

    public static Map<StartupActionType,StartupAction> getAvailableActions()
    {
        if (availableActions == null)
        {
            App.getTraceUtils().startTrace(TAG, "build startup actions list", Log.DEBUG);
            availableActions = buildStartupActions();
            App.getTraceUtils().stopTrace(TAG, "build startup actions list", Log.DEBUG);
        }

        return availableActions;
    }

    public static boolean canExecute(StartupActionType actionType)
    {
        boolean canExecute = false;

        try
        {
            Map<StartupActionType, StartupAction> availableActions = StartupActions.getAvailableActions();

            if (availableActions != null && availableActions.containsKey(actionType))
            {
                StartupAction startupAction = availableActions.get(actionType);

                SharedPreferences prefs = App.getInstance().getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
                StartupActionStatus status = StartupActionStatus.parseInt(prefs.getInt(startupAction.preferenceKey, StartupActionStatus.NOT_AVAILABLE.getValue()));

                switch (status)
                {
                    case NOT_AVAILABLE:
                    case POSTPONED:
                        canExecute = StartupActions.checkInstallationConditions(startupAction.startupConditions);
                        break;

                    case REJECTED:
                    case DONE:
                    case NOT_APPLICABLE:
                    default:
                        canExecute = false;
                }
            }

            Timber.d("canExecute evaluation for StartupAction '%s' : %b", actionType,canExecute);
        }
        catch (Exception e)
        {
            Timber.e(e,"Error during canExecute evaluation for StartupAction: '%s'", actionType);
        }

        return canExecute;
    }

    public static void updateStatus(StartupActionType type, StartupActionStatus status)
    {
        String description = null;
        Map<StartupActionType, StartupAction> availableActions = StartupActions.getAvailableActions();

        if (availableActions != null && availableActions.containsKey(type))
        {
            description = availableActions.get(type).description;
        }

        updateStatus(StartupAction.STARTUP_KEY_PREFIX + type, status, description);
    }

    private static void updateStatus(String actionKey, StartupActionStatus status, String description)
    {
        SharedPreferences prefs = App.getInstance().getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = prefs.edit();

        if (editor != null)
        {
            editor.putInt(actionKey, status.getValue());
            editor.commit();

            App.getEventsReporter().sendEvent(App.getInstance().getString(R.string.analytics_cat_startup_action), description, status.toString(), 0L);
        }
    }

    public static Boolean checkInstallationConditions(StartupCondition [] conditions)
    {
        Boolean result = false;

        if (conditions != null)
        {
            for (StartupCondition condition: conditions)
            {
                result = condition.isValid();
                if (result)
                    break;
            }
        }

        return result;
    }

    public static Boolean checkLaunchCount(Integer launchCount, Integer delayRepeat)
    {
        Boolean result = false;

        if (App.getAppStats().launchCount >= launchCount &&
            (delayRepeat == -1 || App.getAppStats().launchCount % delayRepeat == 0))
        {
            result = true;
        }

        return result;
    }

    public static Boolean checkElapsedDays(Integer daysCount)
    {
        Boolean result = false;

        if (Utils.ElapsedNDays(App.getAppStats().launhcFirstDate, daysCount))
        {
            result = true;
        }

        return result;
    }

    public static boolean checkRequiredAppVersion(Integer requiredVerCode)
    {
        Boolean result = false;

        if (requiredVerCode == null)
        {
            result = true;
        }
        else if (App.getAppStats().majorVersion == requiredVerCode)
        {
            result = true;
        }

        return result;
    }
}
