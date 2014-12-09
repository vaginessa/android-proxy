package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import java.util.List;
import java.util.UUID;

import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncUpdateLinkedWiFiAP extends AsyncTask<Void, UUID, Integer>
{
    private final Context context;
    private final ProxyEntity currentProxy;
    private final ProxyEntity updatedProxy;
    private static final String TAG = AsyncUpdateLinkedWiFiAP.class.getSimpleName();

    public AsyncUpdateLinkedWiFiAP(Context caller, ProxyEntity current, ProxyEntity updated)
    {
        currentProxy = current;
        updatedProxy = updated;
        context = caller;
    }

    @Override
    protected void onPostExecute(Integer updatedWiFiAP)
    {
        super.onPostExecute(updatedWiFiAP);

        final int updatedWifi = updatedWiFiAP;

        Toast.makeText(context, context.getResources().getQuantityString(R.plurals.updated_wifi_networks, updatedWifi, updatedWifi), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Integer doInBackground(Void... voids)
    {
        int updatedWiFiAP = 0;

        List<WiFiAPConfig> configurations = App.getWifiNetworksManager().getSortedWifiApConfigsList();

        if (configurations != null)
        {
//            List<WiFiAPConfig> configurations = new ArrayList<WiFiAPConfig>(sortedConfigurations);

            App.getLogger().d(TAG, "Current proxy: " + currentProxy.toString());
            App.getLogger().d(TAG, "Updated proxy: " + updatedProxy.toString());

//            App.getInstance().wifiActionEnabled = false;

            if (configurations != null)
            {
                for (WiFiAPConfig conf : configurations)
                {
                    if (conf.getProxySetting() == ProxySetting.STATIC)
                    {
                        App.getLogger().d(TAG, "Checking AP: " + conf.toShortString());

                        if (conf.isValidProxyConfiguration())
                        {
                            String host = conf.getProxyHost();
                            Integer port = conf.getProxyPort();
                            String exclusion = conf.getProxyExclusionList();

                            if (host.equalsIgnoreCase(currentProxy.getHost())
                                    && port.equals(currentProxy.getPort())
                                    && exclusion.equalsIgnoreCase(currentProxy.getExclusion()))
                            {
                                conf.setProxyHost(updatedProxy.getHost());
                                conf.setProxyPort(updatedProxy.getPort());
                                conf.setProxyExclusionString(updatedProxy.getExclusion());

                                App.getLogger().d(TAG, "Writing updated AP configuration on device: " + conf.toShortString());

                                try
                                {
                                    conf.writeConfigurationToDevice();
                                }
                                catch (Exception e)
                                {
                                    App.getEventsReporter().sendException(e);
                                }

                                updatedWiFiAP++;

                                try
                                {
                                    Thread.sleep(1);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }

                                // Calling refresh intent only after save of all AP configurations
//                                App.getLogger().i(TAG, "Sending broadcast intent: " + Intents.WIFI_AP_UPDATED);
//                                Intent intent = new Intent(Intents.WIFI_AP_UPDATED);
//                                APL.getContext().sendBroadcast(intent);
                            }
                        }
                    }
                }
            }
        }

//        App.getInstance().wifiActionEnabled = true;

        App.getLogger().d(TAG, "Current proxy: " + currentProxy.toString());
        App.getLogger().d(TAG, "Updated proxy: " + updatedProxy.toString());

//        App.getDBManager().upsertProxy(updatedProxy);

        return updatedWiFiAP;
    }
}
