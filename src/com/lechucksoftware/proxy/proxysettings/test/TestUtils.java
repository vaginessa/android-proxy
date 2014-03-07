package com.lechucksoftware.proxy.proxysettings.test;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.constants.CodeNames;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyStatusItem;
import com.shouldit.proxy.lib.enums.SecurityType;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;
import com.shouldit.proxy.lib.utils.ProxyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by marco on 10/10/13.
 */
public class TestUtils
{

    // "0123456789" + "ABCDE...Z"
    private static final String ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
    private static final int MIN_LENGHT = 3;
    private static final int MAX_LENGHT = 15;

    private static final int MIN_TAGS = 2;
    private static final int MAX_TAGS = 6;
    private static final int MAX_EXCLUSION = 10;

    private static final String TAG = TestUtils.class.getSimpleName();

    public static String getRandomExclusionList()
    {
        Random r = new Random();
        int maxEx = r.nextInt(MAX_EXCLUSION);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxEx; i++)
        {
            if (i != 0)
                sb.append(",");
            sb.append(getRandomIP());
        }

        return sb.toString();
    }

    public static String getRandomIP()
    {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }

    public static int getRandomPort()
    {
        Random r = new Random();
        return r.nextInt(65536);
    }

    public static String getRandomTag()
    {
        Random r = new Random();
        int len = r.nextInt(MAX_LENGHT) + MIN_LENGHT;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++)
        {
            int charpos = r.nextInt(ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(charpos));
        }

        return sb.toString();
    }

    public static List<ProxyEntity> readProxyExamples(Context ctx)
    {
        String line = null;
        List<ProxyEntity> proxies = new ArrayList<ProxyEntity>();

        try
        {
            AssetManager am = ctx.getAssets();
            if (am != null)
            {
                InputStream inputStream = am.open("proxy_examples.csv");
                if (inputStream != null)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = br.readLine()) != null)
                    {
                        if (line.contains(":"))
                        {
                            ProxyEntity p = new ProxyEntity();
                            String [] host_port = line.split(":");
                            p.host = host_port[0];
                            p.port = Integer.parseInt(host_port[1]);
                            proxies.add(p);
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            LogWrapper.e(TAG, "No proxy examples found");
            return null;
        }
        catch (Exception e)
        {
            LogWrapper.e(TAG, "Generic exception during read of proxy examples: " + e.toString());
            return null;
        }

        return proxies;
    }

    public static ProxyEntity getRandomProxy()
    {
        ProxyEntity pd = new ProxyEntity();
        pd.host = getRandomIP();
        pd.port = getRandomPort();
        pd.exclusion = getRandomExclusionList();

        Random r = new Random();
        int tagNum = r.nextInt(MAX_TAGS) + MIN_TAGS;
        for (int i = 0; i < tagNum; i++)
        {
            TagEntity tag = ApplicationGlobals.getDBManager().getRandomTag();
            if (tag != null)
            {
                pd.addTag(tag);
            }
        }

        return pd;
    }

    public static ProxyEntity getModifiedExistingProxy()
    {
        ProxyEntity pd = ApplicationGlobals.getDBManager().getRandomProxy();

        if (pd != null)
        {
            Random r = new Random();
            int typeOfModification = r.nextInt(5);

            switch (typeOfModification)
            {
                case 0:
                    pd.host = getRandomIP();
                    break;
                case 1:
                    pd.port = getRandomPort();
                    break;
                case 2:
                    pd.exclusion = getRandomExclusionList();
                    break;
                case 3:
                    TagEntity tag = ApplicationGlobals.getDBManager().getRandomTag();
                    if (tag != null)
                    {
                        pd.addTag(tag);
                    }
                    break;
                case 4:
                    if (pd.getTags().size() > 0)
                    {
                        TagEntity tagToRemove = pd.getTags().get(0);
                        pd.removeTag(tagToRemove);
                    }
                    break;
            }
        }

        return pd;
    }

    public static void addProxy()
    {
        ProxyEntity pd = getRandomProxy();

        ProxyEntity savedProxy = ApplicationGlobals.getDBManager().upsertProxy(pd);

        if (!savedProxy.equals(pd))
        {

        }
        else
        {

        }
    }

    public static void addProxyExamples(Context ctx)
    {
        List<ProxyEntity> proxies = readProxyExamples(ctx);

        for (ProxyEntity p : proxies)
        {
            ApplicationGlobals.getDBManager().upsertProxy(p);
        }
    }

    public static void updateProxy()
    {
        ProxyEntity pd = getModifiedExistingProxy();
        if (pd != null)
        {
            ApplicationGlobals.getDBManager().updateProxy(pd.getId(), pd);
        }
    }

    public static void addTags()
    {
        TagEntity tag = new TagEntity();
        Random r = new Random();
        tag.tag = getRandomTag();
        tag.tagColor = r.nextInt(4) + 1;
        ApplicationGlobals.getDBManager().upsertTag(tag);
    }

    public static void assignProxies(ProxyConfiguration conf, ProxyEntity proxy) throws Exception
    {
        ProxySetting originalSettings = conf.getProxySettings();
        ProxyEntity originalData = new ProxyEntity();

        if (originalSettings == ProxySetting.STATIC)
        {
            originalData.host = conf.getProxyHost();
            originalData.port = conf.getProxyPort();
            originalData.exclusion = conf.getProxyExclusionList();
        }

        conf.setProxySetting(ProxySetting.STATIC);
        conf.setProxyHost(proxy.host);
        conf.setProxyPort(proxy.port);
        conf.setProxyExclusionList(proxy.exclusion);

        conf.writeConfigurationToDevice();

        Thread.sleep(5000);

        for (int i = 0; i < 20; i++)
        {
            Thread.sleep(1000);

            ProxyConfiguration updatedConf = ApplicationGlobals.getProxyManager().getConfiguration(conf.id);

            if (updatedConf.getProxySettings() == ProxySetting.STATIC &&
                    updatedConf.getProxyHost() == proxy.host &&
                    updatedConf.getProxyPort() == proxy.port &&
                    updatedConf.getProxyExclusionList() == proxy.exclusion)
            {
                LogWrapper.d(TAG, updatedConf.toShortString());
            }
            else
            {
                throw new Exception("ERROR ASSIGNING PROXY");
            }
        }

        conf.setProxySetting(ProxySetting.NONE);
        conf.setProxyHost(null);
        conf.setProxyPort(null);
        conf.setProxyExclusionList(null);
        conf.writeConfigurationToDevice();

        Thread.sleep(5000);

        for (int i = 0; i < 20; i++)
        {
            Thread.sleep(1000);

            ProxyConfiguration updatedConf = ApplicationGlobals.getProxyManager().getConfiguration(conf.id);

            if (updatedConf.getProxySettings() == ProxySetting.NONE &&
                    (updatedConf.getProxyHost() == null || updatedConf.getProxyHost() == "") &&
                    (updatedConf.getProxyPort() == null || updatedConf.getProxyPort() == -1) &&
                    (updatedConf.getProxyExclusionList() == null || updatedConf.getProxyExclusionList() == ""))
            {
                LogWrapper.d(TAG, updatedConf.toShortString());
            }
            else
            {
                throw new Exception("ERROR ASSIGNING PROXY");
            }
        }

        conf.setProxySetting(originalSettings);
        if (originalSettings == ProxySetting.STATIC)
        {
            conf.setProxyHost(originalData.host);
            conf.setProxyPort(originalData.port);
            conf.setProxyExclusionList(originalData.exclusion);
        }
        conf.writeConfigurationToDevice();
        Thread.sleep(5000);
    }

    public static void clearInUse()
    {
        ProxyEntity pd = ApplicationGlobals.getDBManager().getRandomProxy();
        ApplicationGlobals.getDBManager().clearInUseFlag(pd.getId());

        ProxyEntity pd1 = ApplicationGlobals.getDBManager().getRandomProxy();
        ProxyEntity pd2 = ApplicationGlobals.getDBManager().getRandomProxy();
        ProxyEntity pd3 = ApplicationGlobals.getDBManager().getRandomProxy();

        ApplicationGlobals.getDBManager().clearInUseFlag(pd1.getId(), pd2.getId(), pd3.getId());

        ApplicationGlobals.getDBManager().clearInUseFlag();
    }

    public static void testValidation()
    {
        ProxyStatusItem result = ProxyUtils.isProxyValidExclusionAddress("shouldit.it");
        result = ProxyUtils.isProxyValidExclusionAddress("localhost");
        result = ProxyUtils.isProxyValidExclusionAddress("DEV-*");
        result = ProxyUtils.isProxyValidExclusionAddress("*.local");
        result = ProxyUtils.isProxyValidExclusionAddress("*.shouldit.it");
    }

    public static void clearAllProxies(Context ctx)
    {
        ApplicationGlobals.getInstance().wifiActionEnabled = false;

        for (ProxyConfiguration configuration : ApplicationGlobals.getProxyManager().getSortedConfigurationsList())
        {
            if (configuration.ap.security == SecurityType.SECURITY_EAP)
            {
                // skip 802.1x security networks
                continue;
            }

            try
            {
                configuration.setProxySetting(ProxySetting.NONE);
                configuration.setProxyHost("");
                configuration.setProxyPort(0);
                configuration.setProxyExclusionList("");
                configuration.writeConfigurationToDevice();
            }
            catch (Exception e)
            {
                EventReportingUtils.sendException(e);
            }
        }

        ApplicationGlobals.getInstance().wifiActionEnabled = true;
    }

    public static void setAllProxies(Context ctx)
    {
        ProxyEntity p = getRandomProxy();

        ApplicationGlobals.getInstance().wifiActionEnabled = false;

        for (ProxyConfiguration configuration : ApplicationGlobals.getProxyManager().getSortedConfigurationsList())
        {
            if (configuration.ap.security == SecurityType.SECURITY_EAP)
            {
                // skip 802.1x security networks
                continue;
            }

            configuration.setProxySetting(ProxySetting.STATIC);
            configuration.setProxyHost(p.host);
            configuration.setProxyPort(p.port);
            configuration.setProxyExclusionList(p.exclusion);
            try
            {
                configuration.writeConfigurationToDevice();
            }
            catch (Exception e)
            {
                EventReportingUtils.sendException(e);
            }
        }

        ApplicationGlobals.getInstance().wifiActionEnabled = true;
    }
}

