package com.lechucksoftware.proxy.proxysettings.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.provider.Telephony;
import android.text.TextUtils;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.CodeNames;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
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

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.ProxyStatusItem;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.constants.APLReflectionConstants;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import timber.log.Timber;

/**
 * Created by marco on 10/10/13.
 */
public class DevelopmentUtils
{
    // "0123456789" + "ABCDE...Z"
    private static final String ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
    private static final int MIN_LENGHT = 3;
    private static final int MAX_LENGHT = 15;

    private static final int MIN_TAGS = 2;
    private static final int MAX_TAGS = 6;
    private static final int MAX_EXCLUSION = 10;

    private static final String TAG = DevelopmentUtils.class.getSimpleName();

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
                            p.setHost(host_port[0]);
                            p.setPort(Integer.parseInt(host_port[1]));
                            proxies.add(p);
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            Timber.e("No proxy examples found");
            return null;
        }
        catch (Exception e)
        {
            Timber.e("Generic exception during read of proxy examples: " + e.toString());
            return null;
        }

        return proxies;
    }

    public static ProxyEntity createRandomHTTPProxy()
    {
        ProxyEntity pd = new ProxyEntity();
        pd.setHost(getRandomIP());
        pd.setPort(getRandomPort());
        pd.setExclusion(getRandomExclusionList());

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

    public static PacEntity createRandomPACProxy()
    {
        PacEntity pacEntity = new PacEntity();
        pacEntity.setPacUrlFile("http://" + getRandomIP() + "/proxy.pac");

        return pacEntity;
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
                    pd.setHost(getRandomIP());
                    break;
                case 1:
                    pd.setPort(getRandomPort());
                    break;
                case 2:
                    pd.setExclusion(getRandomExclusionList());
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

    public static void addRandomProxy()
    {
        ProxyEntity pd = createRandomHTTPProxy();
        ProxyEntity savedProxy = App.getDBManager().upsertProxy(pd);


        PacEntity pac = createRandomPACProxy();
        PacEntity savedPac = App.getDBManager().upsertPac(pac);
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
        tag.setTag(getRandomTag());
        tag.setTagColor(r.nextInt(4) + 1);
        App.getDBManager().upsertTag(tag);
    }

//    public static void assignProxies(WiFiApConfig conf, ProxyEntity proxy) throws Exception
//    {
//        ProxySetting originalSettings = conf.getProxySetting();
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
//            WiFiApConfig updatedConf = App.getWifiNetworksManager().getConfiguration(conf.id);
//
//            if (updatedConf.getProxySetting() == ProxySetting.STATIC &&
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
//            WiFiApConfig updatedConf = App.getWifiNetworksManager().getConfiguration(conf.id);
//
//            if (updatedConf.getProxySetting() == ProxySetting.NONE &&
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

    public static void resetPreferences(Context ctx)
    {
        SharedPreferences preferences = ctx.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        App.getAppStats().updateInstallationDetails();
    }

    public static void testValidation()
    {
        ProxyStatusItem result = ProxyUtils.isProxyValidExclusionAddress("shouldit.it");
        result = ProxyUtils.isProxyValidExclusionAddress("localhost");
        result = ProxyUtils.isProxyValidExclusionAddress("DEV-*");
        result = ProxyUtils.isProxyValidExclusionAddress("*.local");
        result = ProxyUtils.isProxyValidExclusionAddress("*.shouldit.it");
    }

    public static void clearProxyForAllAP(Context ctx)
    {
        List<WiFiApConfig> configsToSave = new ArrayList<WiFiApConfig>();

        for (WiFiApConfig configuration : App.getWifiNetworksManager().getSortedWifiApConfigsList())
        {
            if (configuration.getSecurityType() == SecurityType.SECURITY_EAP)
            {
                // skip 802.1x security networks
                continue;
            }

            configuration.setProxySetting(ProxySetting.NONE);
            configuration.setProxyHost("");
            configuration.setProxyPort(0);
            configuration.setProxyExclusionString("");
            configsToSave.add(configuration);
        }

        try
        {
            App.getWifiNetworksManager().addSavingOperation(configsToSave);
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception clearing proxy for all Wi-Fi AP");
        }
    }

    public static void setProxyForAllAP(Context ctx)
    {
        ProxyEntity p;

        if (App.getDBManager().getProxiesCount() > 0)
        {
            p = App.getDBManager().getRandomProxy();
        }
        else
        {
            p = createRandomHTTPProxy();
        }

        List<WiFiApConfig> configsToSave = new ArrayList<WiFiApConfig>();

        for (WiFiApConfig configuration : App.getWifiNetworksManager().getSortedWifiApConfigsList())
        {
            if (configuration.getSecurityType() == SecurityType.SECURITY_EAP)
            {
                // skip 802.1x security networks
                continue;
            }

            configuration.setProxySetting(ProxySetting.STATIC);
            configuration.setProxyHost(p.getHost());
            configuration.setProxyPort(p.getPort());
            configuration.setProxyExclusionString(p.getExclusion());
            configsToSave.add(configuration);
        }

        try
        {
            App.getWifiNetworksManager().addSavingOperation(configsToSave);
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception writing configuration to device");
        }
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
            Timber.e(e, "Exception testing APN activity");
        }
    }

    public static void testSerialization()
    {
        String s = null;
        WiFiApConfig conf = App.getWifiNetworksManager().getCachedConfiguration();

        ObjectOutputStream out = null;

        if (conf != null)
        {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                out = new ObjectOutputStream(baos);
                out.writeObject(conf);
                out.close();

                s = new String(baos.toByteArray());

                if (TextUtils.isEmpty(s))
                {
                    Timber.e("Not serialized correctly");
                }
                else
                {
                    Timber.d(s);
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public static String createFakeWifiNetwork(Context ctx)
    {
        WifiConfiguration wc = prepareFakeWifiNetwork();

        int res = APL.getWifiManager().addNetwork(wc);
        Timber.d("add Network returned " + res);
        boolean es = APL.getWifiManager().saveConfiguration();
        Timber.d("saveConfiguration returned " + es);

        return wc.SSID;
    }

    public static WifiConfiguration prepareFakeWifiNetwork()
    {
        WifiConfiguration wc = new WifiConfiguration();
        Random r = new Random();

        String ssid = UIUtils.getRandomCodeName().name() + String.valueOf(r.nextInt(10));
        String password = "\"aaabbb1234567\""; //This is the WEP Password

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
            default:
                setupWPAWifiConfig(wc, ssid, password);
                break;
//            case 3:
//                setup802xWifiConfig(wc, ssid, password);
//                break;
        }
        return wc;
    }

    public static int deleteFakeWifiNetworks(Context ctx)
    {
        int removedNetworks = 0;
        List<WifiConfiguration> configurations = APL.getWifiManager().getConfiguredNetworks();
        List<Integer> networksToDelete = new ArrayList<Integer>();

        if (configurations != null && configurations.size() > 0)
        {
            for (WifiConfiguration conf : configurations)
            {
                String SSID = ProxyUtils.cleanUpSSID(conf.SSID);
                for (CodeNames codename : CodeNames.values())
                {
                    if (SSID.contains(codename.toString()))
                    {
                        networksToDelete.add(conf.networkId);
                    }
                }
            }
        }

        for (int i = 0; i < networksToDelete.size(); i++)
        {
            int networkId = networksToDelete.get(i);
            boolean res = APL.getWifiManager().removeNetwork(networkId);
            Timber.d("removeNetwork returned " + res);
            boolean es = APL.getWifiManager().saveConfiguration();
            Timber.d("saveConfiguration returned " + es);

            removedNetworks++;

            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                Timber.e(e, "Exception during sleep");
            }
        }

        return removedNetworks;
    }

    @TargetApi(18)
    private static void setup802xWifiConfig(WifiConfiguration config, String ssid, String password)
    {
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        config.enterpriseConfig = new WifiEnterpriseConfig();

        int eapMethod = WifiEnterpriseConfig.Eap.PEAP;
        int phase2Method = WifiEnterpriseConfig.Phase2.GTC;

        config.enterpriseConfig.setEapMethod(eapMethod);

        switch (eapMethod)
        {
            case WifiEnterpriseConfig.Eap.PEAP:
                // PEAP supports limited phase2 values
                // Map the index from the PHASE2_PEAP_ADAPTER to the one used
                // by the API which has the full list of PEAP methods.
                switch (phase2Method)
                {
                    case APLReflectionConstants.WIFI_PEAP_PHASE2_NONE:
                        config.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);
                        break;
                    case APLReflectionConstants.WIFI_PEAP_PHASE2_MSCHAPV2:
                        config.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
                        break;
                    case APLReflectionConstants.WIFI_PEAP_PHASE2_GTC:
                        config.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.GTC);
                        break;
                    default:
                        Timber.e("Unknown phase2 method" + phase2Method);
                        break;
                }
                break;
            default:
                // The default index from PHASE2_FULL_ADAPTER maps to the API
                config.enterpriseConfig.setPhase2Method(phase2Method);
                break;
        }

        String caCert = "";
//        config.enterpriseConfig.setCaCertificateAlias(caCert);
//        String clientCert = (String) mEapUserCertSpinner.getSelectedItem();
//        if (clientCert.equals(unspecifiedCert)) clientCert = "";
//        config.enterpriseConfig.setClientCertificateAlias(clientCert);
//        config.enterpriseConfig.setIdentity(mEapIdentityView.getText().toString());
//        config.enterpriseConfig.setAnonymousIdentity(mEapAnonymousView.getText().toString());

//         if (mPasswordView.isShown())
//         {
//             // For security reasons, a previous password is not displayed to user.
//             // Update only if it has been changed.
//             if (mPasswordView.length() > 0)
//             {
        config.enterpriseConfig.setPassword(password.toString());
//             }
//         }
//         else
//         {
//             // clear password
//             config.enterpriseConfig.setPassword(mPasswordView.getText().toString());
//         }
    }

    private static void setupNOSECWifiConfig(WifiConfiguration wc, String ssid, String password)
    {
        wc.SSID = String.format("\"%s\"", ssid);
        wc.BSSID = randomMACAddress();

        wc.priority = 40;
        wc.status = WifiConfiguration.Status.DISABLED;

        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    }

    public static void setupWEPWifiConfig(WifiConfiguration wc, String ssid, String password)
    {
        wc.SSID = String.format("\"%s\"", ssid);
        wc.BSSID = randomMACAddress();

        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);

        int length = password.length();

        // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
        if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*"))
        {
            wc.wepKeys[0] = password;
        }
        else
        {
            wc.wepKeys[0] = '"' + password + '"';
        }

//        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;
//        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//
//        wc.wepKeys[0] = password;
//        wc.wepTxKeyIndex = 0;
    }

    public static void setupWPAWifiConfig(WifiConfiguration wc, String ssid, String password)
    {
        wc.SSID = String.format("\"%s\"", ssid);
        wc.BSSID = randomMACAddress();
//        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;

        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        if (password.matches("[0-9A-Fa-f]{64}"))
        {
            wc.preSharedKey = password;
        }
        else
        {
            wc.preSharedKey = '"' + password + '"';
        }

//        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//
//        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

//        wc.preSharedKey = password;
//        wc.wepTxKeyIndex = 0;
    }

    private static String randomMACAddress()
    {
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte) (macAddr[0] & (byte) 254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for (byte b : macAddr)
        {

            if (sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}

