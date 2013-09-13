package com.lechucksoftware.proxy.proxysettings;

import com.lechucksoftware.proxy.proxysettings.fragments.StatusFragment;
import com.shouldit.proxy.lib.APL;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.ProxyConfiguration;

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

    public void setStatus(Constants.StatusFragmentStates status)
    {
        StatusFragment.getInstance().setStatus(status);
    }

    public void setStatus(Constants.StatusFragmentStates status, String message)
    {
        StatusFragment.getInstance().setStatus(status, message);
    }

    public void setStatus(Constants.StatusFragmentStates status, String message, Boolean isInProgress)
    {
        StatusFragment.getInstance().setStatus(status, message, isInProgress);
    }

//    public void refreshUI()
//    {
//        ProxyConfiguration selConf = ApplicationGlobals.getSelectedConfiguration();
//
//        if (selConf != null)
//        {
//            if (selConf.isCurrentNetwork())
//            {
//                if (selConf.status != null)
//                {
//                    if (selConf.status.getCheckingStatus() == APLConstants.CheckStatusValues.CHECKED)
//                    {
//                        setStatus(Constants.StatusFragmentStates.CONNECTED, selConf.getAPConnectionStatus());
//                    }
//                    else
//                    {
//                        setStatus(Constants.StatusFragmentStates.CHECKING);
//                    }
//                }
//                else
//                {
//                    setStatus(Constants.StatusFragmentStates.CHECKING);
//                }
//            }
//            else if (selConf.ap.getLevel() > -1)
//            {
//                setStatus(Constants.StatusFragmentStates.CONNECT_TO, getResources().getString(R.string.connect_to_wifi_action, selConf.ap.ssid));
//            }
//            else
//            {
//                setStatus(Constants.StatusFragmentStates.NOT_AVAILABLE, selConf.getAPConnectionStatus());
//            }
//        }
//        else
//        {
//            // No configuration selected
//            if (!APL.getWifiManager().isWifiEnabled())
//            {
//                // Wi-Fi disabled -> ask to enable!
//                setStatus(Constants.StatusFragmentStates.ENABLE_WIFI, getResources().getString(R.string.enable_wifi_action));
//            }
//            else
//            {
//                // Wi-Fi enabled
//                if (ApplicationGlobals.isConnectedToWiFi())
//                {
//                    // Connected to Wi-Fi ap
//
//                }
//                else
//                {
//                    if (ApplicationGlobals.getInstance().getNotConfiguredWifi().values().size() > 0)
//                    {
//                        // Wi-Fi AP available to connection -> Go to Wi-Fi Settings
//                        setStatus(Constants.StatusFragmentStates.GOTO_AVAILABLE_WIFI, getResources().getString(R.string.setupap_wifi_action));
//                    }
//                    else
//                    {
//                        // Wi-Fi AP not available to connection
////                                setStatusInternal(getResources().getString(R.string.enable_wifi_action), configureNewWifiAp, R.color.Holo_Green_Light);
//                    }
//                }
//            }
//        }
//
//    }

    public void hide()
    {
        StatusFragment.getInstance().hide();
    }
}
