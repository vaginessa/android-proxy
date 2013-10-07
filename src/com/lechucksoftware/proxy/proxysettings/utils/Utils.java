package com.lechucksoftware.proxy.proxysettings.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class Utils
{
    public static String TAG = "Utils";
    public static String BASE_ASSETS = "file:///android_asset/";

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
            BugReportingUtils.sendException(e);
        }

        return pInfo;
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
                BugReportingUtils.sendException(new Exception("No InstallerPackageName recognized: " + market));
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
            BugReportingUtils.sendException(e);
        }

        return text;
    }
}
