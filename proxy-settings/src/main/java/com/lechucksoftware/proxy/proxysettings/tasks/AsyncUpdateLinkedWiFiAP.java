package com.lechucksoftware.proxy.proxysettings.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import java.util.ArrayList;
import java.util.List;

import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import timber.log.Timber;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncUpdateLinkedWiFiAP extends AsyncTask<Void, Integer, Integer>
{
    private final Context context;

    private final ProxyEntity currentProxy;
    private final ProxyEntity updatedProxy;

    private final PacEntity currentPac;
    private final PacEntity updatedPac;

    private final ProxySetting proxySetting;

    private int updatedWiFiAP;
    private List<WiFiApConfig> configsToSave;

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
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);

        Timber.d("Updated #%d AP", values[0]);
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
        configsToSave = new ArrayList<>();

        if (configurations != null)
        {
//            List<WiFiApConfig> configurations = new ArrayList<WiFiApConfig>(sortedConfigurations);
            Timber.d("Current STATIC proxy: %s", currentProxy == null ? "NULL" : currentProxy);
            Timber.d("Updated STATIC proxy: %s", updatedProxy == null ? "NULL" : updatedProxy);
            Timber.d("Current PAC proxy: %s", currentPac == null ? "NULL" : currentPac);
            Timber.d("Updated PAC proxy: %s", updatedPac == null ? "NULL" : updatedPac);

            for (WiFiApConfig conf : configurations)
            {
                if (conf.getProxySetting() == proxySetting)
                {
                    Timber.d("Checking AP: " + conf.toShortString());

                    if (conf.isValidProxyConfiguration())
                    {
                        if (proxySetting == ProxySetting.STATIC)
                        {
                            updateWifiNetworkStaticProxy(conf);
                        }
                        else if (proxySetting == ProxySetting.PAC)
                        {
                            updateWifiNetworkPacProxy(conf);
                        }
                    }
                }
            }

            try
            {
                App.getWifiNetworksManager().addSavingOperation(configsToSave);
            }
            catch (Exception e)
            {
                Timber.e(e,"Exception on writeConfigurationToDevice");
            }
        }

        return updatedWiFiAP;
    }

    private void updateWifiNetworkPacProxy(WiFiApConfig conf)
    {
        Uri pacFileUri = conf.getPacFileUri();

        if (pacFileUri.equals(currentPac.getPacUriFile()))
        {
            conf.setPacUriFile(updatedPac.getPacUriFile());
            configsToSave.add(conf);
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
            configsToSave.add(conf);
        }
    }
}
