package com.lechucksoftware.proxy.proxysettings.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WiFiApListActivity;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.WhatsNewDialog;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.Duration;

/**
 * Created by Marco on 29/11/13.
 */


public class AsyncUpdateLinkedWiFiAP extends AsyncTask<Void, UUID, Integer>
{
    private final Context context;
    private final ProxyEntity currentProxy;
    private final ProxyEntity updatedProxy;

    public AsyncUpdateLinkedWiFiAP(Context ctx, ProxyEntity current, ProxyEntity updated)
    {
        currentProxy = current;
        updatedProxy = updated;
        context = ctx;
    }

    @Override
    protected void onPostExecute(Integer updatedWiFiAP)
    {
        super.onPostExecute(updatedWiFiAP);

        if (updatedWiFiAP > 0)
        {
            Toast.makeText(context, String.format("Updated %d Wi-Fi access point configuration",updatedWiFiAP), Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected Integer doInBackground(Void ... voids)
    {
        int updatedWiFiAP = 0;

        List<ProxyConfiguration> configurations = ApplicationGlobals.getProxyManager().getSortedConfigurationsList();

        for(ProxyConfiguration conf: configurations)
        {
            long proxyId = ApplicationGlobals.getDBManager().findProxy(conf.getProxyHost(), conf.getProxyPort());

            if (proxyId == currentProxy.getId())
            {
                conf.setProxyHost(updatedProxy.host);
                conf.setProxyPort(updatedProxy.port);
                conf.setProxyExclusionList(updatedProxy.exclusion);
                try
                {
                    conf.writeConfigurationToDevice();
                }
                catch (Exception e)
                {
                    EventReportingUtils.sendException(e);
                }

                updatedWiFiAP++;
            }
        }

        ApplicationGlobals.getDBManager().upsertProxy(updatedProxy);

        return updatedWiFiAP;
    }
}
