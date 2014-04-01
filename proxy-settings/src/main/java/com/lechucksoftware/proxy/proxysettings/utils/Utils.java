package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.shouldit.proxy.lib.utils.HttpAnswer;
import com.shouldit.proxy.lib.utils.ProxyUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;

public class Utils
{
    public static final String HTTP_FREEGEOIP_NET_JSON_STRING = "http://freegeoip.net/json/";

    // TODO: add fallback on telize
    public static final String HTTP_TELIZE_NET_JSON_STRING = "http://www.telize.com/geoip/";

    public static String TAG = "Utils";
    public static String BASE_ASSETS = "file:///android_asset/";

    public static String getProxyCountryCode(ProxyEntity proxy) throws Exception
    {
        String stringUrl = (HTTP_FREEGEOIP_NET_JSON_STRING + proxy.host).trim();
        URI uri = null;

        int timeout = 1000 * 60;
        HttpAnswer answer = null;
        String result = null;

        try
        {
            Uri parsedUri = Uri.parse(stringUrl);
            if (parsedUri != null)
            {
                String parsedUriString = parsedUri.toString();
                uri = new URI(parsedUriString);
            }
        }
        catch (URISyntaxException e)
        {
            EventReportingUtils.sendException(e);
        }

        if (uri != null)
        {
            try
            {
                answer = ProxyUtils.getHttpAnswerURI(uri, App.getProxyManager().getCurrentConfiguration().getProxy(), timeout);
            }
            catch (IOException e)
            {
                App.getLogger().w(TAG, "Exception on getProxyCountryCode: " + e.toString());
            }

            if (answer != null)
            {
                String answerBody = answer.getBody();

                if (answer.getStatus() == HttpURLConnection.HTTP_OK && !TextUtils.isEmpty(answerBody))
                {
                    JSONObject jsonObject = new JSONObject(answerBody);
                    if (jsonObject.has("country_code"))
                    {
                        result = jsonObject.getString("country_code");
                    }
                }
            }
        }

        return result;
    }

    public static String getAppVersionName(Context ctx)
    {
        PackageInfo pi = Utils.getAppInfo(ctx);
        String appVersionName;

        if (pi != null)
        {
            appVersionName = ctx.getResources().getString(R.string.app_versionname, pi.versionName);
        }
        else
        {
            appVersionName = "";
        }

        return appVersionName;
    }

    public static PackageInfo getAppInfo(Context ctx)
    {
        PackageInfo pInfo = null;
        try
        {
            pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            EventReportingUtils.sendException(e);
        }

        return pInfo;
    }

    public static void startMarketActivity(Context ctx)
    {
        Uri marketUri = getMarketUri(App.getInstance().activeMarket);

        boolean marketShown = false;

        try
        {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
            marketShown = true;
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
        }

        if (!marketShown)
        {
            Toast.makeText(ctx,R.string.market_not_found,Toast.LENGTH_SHORT).show();
        }
    }

    public static Uri getMarketUri(AndroidMarket market)
    {
        switch (market)
        {
            case AMAZON:
                return Constants.AMAZON_MARKET_URL;
            case PLAY:
                return Constants.PLAY_MARKET_URL;
            default:
                return null;
        }
    }

    public static AndroidMarket getInstallerMarket(Context ctx)
    {
        String market;
        market = ctx.getPackageManager().getInstallerPackageName(getAppInfo(ctx).packageName);

        AndroidMarket res = null;

        if (market != null)
        {
            if (market.equals(Constants.PLAY_MARKET_PACKAGE))
            {
                res = AndroidMarket.PLAY;
            }
            else if (market.equals(Constants.AMAZON_MARKET_PACKAGE))
            {
                res = AndroidMarket.AMAZON;
            }
        }

        if (res == null)
        {
            res = AndroidMarket.OTHER;

            if (!BuildConfig.DEBUG)
                EventReportingUtils.sendException(new Exception("No InstallerPackageName recognized: " + market));
            else
                res = AndroidMarket.PLAY;
        }

        return res;
    }

    public static void setHTTPAuthentication(final String user, final String password)
    {
        Authenticator.setDefault(new Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(user, password.toCharArray());
            }
        });
    }

    public static String getFullAsset(Context ctx, String filename)
    {
        String text = null;
        InputStream inputStream = null;
        try
        {
            inputStream = ctx.getAssets().open(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String aux;

            while ((aux = br.readLine()) != null)
            {
                builder.append(aux);
            }

            text = builder.toString();
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
        }

        return text;
    }

    public static void checkDemoMode(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        if (prefs.getBoolean(Constants.PREFERENCES_DEMO_MODE, false))
        {
            App.getInstance().demoMode = true;
        }
        else
        {
            App.getInstance().demoMode = false;
        }
    }

    public static void setDemoMode(Context ctx, boolean enabled)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(Constants.PREFERENCES_DEMO_MODE, enabled);
        editor.commit();
    }
}
