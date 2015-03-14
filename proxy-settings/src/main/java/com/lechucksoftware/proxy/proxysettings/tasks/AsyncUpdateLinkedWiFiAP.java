package com.lechucksoftware.proxy.proxysettings.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.WifiNetworksManager;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import java.util.List;
import java.util.UUID;

import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import timber.log.Timber;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncUpdateLinkedWiFiAP extends AsyncTask<Void, UUID, Integer>
{
    private final Context context;

    private final ProxyEntity currentProxy;
    private final ProxyEntity updatedProxy;

    private final PacEntity currentPac;
    private final PacEntity updatedPac;

    private final ProxySetting proxySetting;

    private static final String TAG = AsyncUpdateLinkedWiFiAP.class.getSimpleName();
    private int updatedWiFiAP;

    public AsyncUpdateLinkedWiFiAP(Context caller, ProxyEntity current, ProxyEntity updated)
    {
        currentProxy = current;
        updatedProxy = updated;

        proxySetting = ProxySetting.STATIC;

        currentPac = null;
        updatedPac = null;

        updatedWiFiAP = 0;

        context = caller;
    }

    public AsyncUpdateLinkedWiFiAP(Context caller, PacEntity current, PacEntity updated)
    {
        currentPac = current;
        updatedPac = updated;

        proxySetting = ProxySetting.PAC;

        currentProxy = null;
        updatedProxy = null;

        updatedWiFiAP = 0;

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
        List<WiFiApConfig> configurations = App.getWifiNetworksManager().getSortedWifiApConfigsList();

        if (configurations != null)
        {
//            List<WiFiApConfig> configurations = new ArrayList<WiFiApConfig>(sortedConfigurations);

            Timber.d("Current proxy: " + currentProxy.toString());
            Timber.d("Updated proxy: " + updatedProxy.toString());

            if (configurations != null)
            {
                for (WiFiApConfig conf : configurations)
                {
                    if (conf.getProxySetting() == proxySetting)
                    {
                        Timber.d("Checking AP: " + conf.toShortString());

                        if (conf.isValidProxyConfiguration())
                        {
                            updateWifiNetworkStaticProxy(conf);
                        }
                    }
                }
            }
        }

        Timber.d("Current proxy: " + currentProxy.toString());
        Timber.d("Updated proxy: " + updatedProxy.toString());

//        App.getDBManager().upsertProxy(updatedProxy);

        return updatedWiFiAP;
    }

    private void updateWifiNetworkPacProxy(WiFiApConfig conf)
    {
        Uri pacFileUri = conf.getPacFileUri();

        if (pacFileUri.equals(currentPac.getPacUriFile()))
        {
            conf.setPacUriFile(updatedPac.getPacUriFile());

            Timber.d("Writing updated AP configuration on device: " + conf.toShortString());

            try
            {
                App.getWifiNetworksManager().asyncSaveWifiApConfig(conf);
            }
            catch (Exception e)
            {
                Timber.e(e, "Exception on writeConfigurationToDevice");
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
        }
    }

    private void updateWifiNetworkStaticProxy(WiFiApConfig conf)
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

            Timber.d("Writing updated AP configuration on device: " + conf.toShortString());

            try
            {
                App.getWifiNetworksManager().asyncSaveWifiApConfig(conf);
            }
            catch (Exception e)
            {
                Timber.e(e,"Exception on writeConfigurationToDevice");
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

        }
    }
}
