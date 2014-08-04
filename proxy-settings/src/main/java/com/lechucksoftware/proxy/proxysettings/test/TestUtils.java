package com.lechucksoftware.proxy.proxysettings.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.wifi.WifiConfiguration;
import android.provider.Telephony;
import android.text.TextUtils;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.CodeNames;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import be.shouldit.proxy.lib.WiFiAPConfig;
import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.ProxyStatusItem;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import be.shouldit.proxy.lib.utils.ProxyUtils;

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
                            String[] host_port = line.split(":");
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
            App.getLogger().e(TAG, "No proxy examples found");
            return null;
        }
        catch (Exception e)
        {
            App.getLogger().e(TAG, "Generic exception during read of proxy examples: " + e.toString());
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
            TagEntity tag = App.getDBManager().getRandomTag();
            if (tag != null)
            {
                pd.addTag(tag);
            }
        }

        return pd;
    }

    public static ProxyEntity getModifiedExistingProxy()
    {
        ProxyEntity pd = App.getDBManager().getRandomProxy();

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
                    TagEntity tag = App.getDBManager().getRandomTag();
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

        ProxyEntity savedProxy = App.getDBManager().upsertProxy(pd);

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
            App.getDBManager().upsertProxy(p);
        }
    }

    public static void updateProxy()
    {
        ProxyEntity pd = getModifiedExistingProxy();
        if (pd != null)
        {
            App.getDBManager().updateProxy(pd.getId(), pd);
        }
    }

    public static void addTags()
    {
        TagEntity tag = new TagEntity();
        Random r = new Random();
        tag.tag = getRandomTag();
        tag.tagColor = r.nextInt(4) + 1;
        App.getDBManager().upsertTag(tag);
    }

//    public static void assignProxies(WiFiAPConfig conf, ProxyEntity proxy) throws Exception
//    {
//        ProxySetting originalSettings = conf.getProxySettings();
//        ProxyEntity originalData = new ProxyEntity();
//
//        if (originalSettings == ProxySetting.STATIC)
//        {
//            originalData.host = conf.getProxyHost();
//            originalData.port = conf.getProxyPort();
//            originalData.exclusion = conf.getProxyExclusionList();
//        }
//
//        conf.setProxySetting(ProxySetting.STATIC);
//        conf.setProxyHost(proxy.host);
//        conf.setProxyPort(proxy.port);
//        conf.setProxyExclusionString(proxy.exclusion);
//
//        conf.writeConfigurationToDevice();
//
//        Thread.sleep(5000);
//
//        for (int i = 0; i < 20; i++)
//        {
//            Thread.sleep(1000);
//
//            WiFiAPConfig updatedConf = App.getProxyManager().getConfiguration(conf.id);
//
//            if (updatedConf.getProxySettings() == ProxySetting.STATIC &&
//                    updatedConf.getProxyHost() == proxy.host &&
//                    updatedConf.getProxyPort() == proxy.port &&
//                    updatedConf.getProxyExclusionList() == proxy.exclusion)
//            {
//                LogWrapper.d(TAG, updatedConf.toShortString());
//            }
//            else
//            {
//                throw new Exception("ERROR ASSIGNING PROXY");
//            }
//        }
//
//        conf.setProxySetting(ProxySetting.NONE);
//        conf.setProxyHost(null);
//        conf.setProxyPort(null);
//        conf.setProxyExclusionString(null);
//        conf.writeConfigurationToDevice();
//
//        Thread.sleep(5000);
//
//        for (int i = 0; i < 20; i++)
//        {
//            Thread.sleep(1000);
//
//            WiFiAPConfig updatedConf = App.getProxyManager().getConfiguration(conf.id);
//
//            if (updatedConf.getProxySettings() == ProxySetting.NONE &&
//                    (updatedConf.getProxyHost() == null || updatedConf.getProxyHost() == "") &&
//                    (updatedConf.getProxyPort() == null || updatedConf.getProxyPort() == -1) &&
//                    (updatedConf.getProxyExclusionList() == null || updatedConf.getProxyExclusionList() == ""))
//            {
//                LogWrapper.d(TAG, updatedConf.toShortString());
//            }
//            else
//            {
//                throw new Exception("ERROR ASSIGNING PROXY");
//            }
//        }
//
//        conf.setProxySetting(originalSettings);
//        if (originalSettings == ProxySetting.STATIC)
//        {
//            conf.setProxyHost(originalData.host);
//            conf.setProxyPort(originalData.port);
//            conf.setProxyExclusionString(originalData.exclusion);
//        }
//        conf.writeConfigurationToDevice();
//        Thread.sleep(5000);
//    }

    public static void clearInUse()
    {
        ProxyEntity pd = App.getDBManager().getRandomProxy();

        if (pd != null)
        {
            App.getDBManager().clearInUseFlag(pd.getId());
        }
        else
        {
            App.getDBManager().clearInUseFlag();
        }

        ProxyEntity pd1 = App.getDBManager().getRandomProxy();
        ProxyEntity pd2 = App.getDBManager().getRandomProxy();
        ProxyEntity pd3 = App.getDBManager().getRandomProxy();

        if (pd1 != null && pd2 != null && pd3 != null)
        {
            App.getDBManager().clearInUseFlag(pd1.getId(), pd2.getId(), pd3.getId());
        }

        App.getDBManager().clearInUseFlag();
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
        App.getInstance().wifiActionEnabled = false;

        for (WiFiAPConfig configuration : App.getProxyManager().getSortedConfigurationsList())
        {
            if (configuration.security == SecurityType.SECURITY_EAP)
            {
                // skip 802.1x security networks
                continue;
            }

            try
            {
                configuration.setProxySetting(ProxySetting.NONE);
                configuration.setProxyHost("");
                configuration.setProxyPort(0);
                configuration.setProxyExclusionString("");
                configuration.writeConfigurationToDevice();
            }
            catch (Exception e)
            {
                App.getEventsReporter().sendException(e);
            }
        }

        App.getInstance().wifiActionEnabled = true;

        // Calling refresh intent only after save of all AP configurations
        App.getLogger().i(TAG, "Sending broadcast intent: " + Intents.WIFI_AP_UPDATED);
        Intent intent = new Intent(Intents.WIFI_AP_UPDATED);
        APL.getContext().sendBroadcast(intent);
    }

    public static void setAllProxies(Context ctx)
    {
        ProxyEntity p = getRandomProxy();

        App.getInstance().wifiActionEnabled = false;

        for (WiFiAPConfig configuration : App.getProxyManager().getSortedConfigurationsList())
        {
            if (configuration.security == SecurityType.SECURITY_EAP)
            {
                // skip 802.1x security networks
                continue;
            }

            configuration.setProxySetting(ProxySetting.STATIC);
            configuration.setProxyHost(p.host);
            configuration.setProxyPort(p.port);
            configuration.setProxyExclusionString(p.exclusion);
            try
            {
                configuration.writeConfigurationToDevice();
            }
            catch (Exception e)
            {
                App.getEventsReporter().sendException(e);
            }
        }

        App.getInstance().wifiActionEnabled = true;

        // Calling refresh intent only after save of all AP configurations
        App.getLogger().i(TAG, "Sending broadcast intent: " + Intents.WIFI_AP_UPDATED);
        Intent intent = new Intent(Intents.WIFI_AP_UPDATED);
        APL.getContext().sendBroadcast(intent);
    }

    @TargetApi(19)
    public static void testAPN(Context ctx)
    {
        try
        {
            ctx.startActivity(new Intent(Intent.ACTION_INSERT, Telephony.Carriers.CONTENT_URI));
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }

    public static void testSerialization()
    {
        String s = null;
        WiFiAPConfig conf = App.getProxyManager().getCurrentConfiguration();

        ObjectOutputStream out = null;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(conf);
            out.close();

            s = new String(baos.toByteArray());

            if (TextUtils.isEmpty(s))
            {
                App.getLogger().e(TAG,"Not serialized correctly");
            }
            else
            {
                App.getLogger().d(TAG,s);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static String createFakeWifiNetwork(Context ctx)
    {
        WifiConfiguration wc = new WifiConfiguration();

        String ssid = UIUtils.getRandomCodeName().name();
        String password = "\"aaabbb1234\""; //This is the WEP Password

        Random r = new Random();
        int securityType = r.nextInt(3);
        switch (securityType)
        {
            case 0:
                setupNOSECWifiConfig(wc, ssid, password);
                break;
            case 1:
                setupWEPWifiConfig(wc, ssid, password);
                break;
            case 2:
                setupWPAWifiConfig(wc, ssid, password);
                break;
//            case 3:
//                setup802xWifiConfig(wc, ssid, password);
//                break;
        }

        int res = APL.getWifiManager().addNetwork(wc);
        App.getLogger().d(TAG, "add Network returned " + res );
        boolean es = APL.getWifiManager().saveConfiguration();
        App.getLogger().d(TAG, "saveConfiguration returned " + es );

        return wc.SSID;
    }

    public static int deleteFakeWifiNetworks(Context ctx)
    {
        int removedNetworks = 0;
        List<WifiConfiguration> configurations = APL.getWifiManager().getConfiguredNetworks();
        if (configurations != null && configurations.size() > 0)
        {
            for(WifiConfiguration conf: configurations)
            {
                String SSID = ProxyUtils.cleanUpSSID(conf.SSID);
                for (CodeNames codename : CodeNames.values())
                {
                    if (SSID.contains(codename.toString()))
                    {
                        boolean res = APL.getWifiManager().removeNetwork(conf.networkId);
                        App.getLogger().d(TAG, "removeNetwork returned " + res);
                        boolean es = APL.getWifiManager().saveConfiguration();
                        App.getLogger().d(TAG, "saveConfiguration returned " + es);

                        removedNetworks++;
                    }
                }
            }
        }

        return removedNetworks;
    }

    private static void setup802xWifiConfig(WifiConfiguration wc, String ssid, String password)
    {
        
    }

    private static void setupNOSECWifiConfig(WifiConfiguration wc, String ssid, String password)
    {
        wc.SSID = String.format("\"%s\"",ssid);
        wc.hiddenSSID = false;
        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;
    }

    public static void setupWEPWifiConfig(WifiConfiguration wc, String ssid, String password)
    {
        wc.SSID = String.format("\"%s\"",ssid);
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

        wc.wepKeys[0] = password;
        wc.wepTxKeyIndex = 0;
    }

    public static void setupWPAWifiConfig(WifiConfiguration wc, String ssid, String password)
    {
        wc.SSID = String.format("\"%s\"",ssid);
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;

        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        wc.wepKeys[0] = password;
        wc.wepTxKeyIndex = 0;
    }
}

