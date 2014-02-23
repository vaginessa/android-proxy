package com.lechucksoftware.proxy.proxysettings.test;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.reflection.android.ProxySetting;

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
    private static final String TAG = TestUtils.class.getSimpleName();

    public static String getRandomExclusionList()
    {
        Random r = new Random();
        int maxEx = r.nextInt(10);
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
        ProxySetting originalSettings = conf.proxySetting;
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

            if (updatedConf.proxySetting == ProxySetting.STATIC &&
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

            if (updatedConf.proxySetting == ProxySetting.NONE &&
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
}

