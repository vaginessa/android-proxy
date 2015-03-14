package com.lechucksoftware.proxy.proxysettings.utils;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.AndroidMarket;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.utils.HttpAnswer;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import timber.log.Timber;

public class Utils
{
    public static final String HTTP_FREEGEOIP_NET_JSON_STRING = "http://freegeoip.net/json/";

    // TODO: add fallback on telize
    public static final String HTTP_TELIZE_NET_JSON_STRING = "http://www.telize.com/geoip/";

    public static String TAG = Utils.class.getSimpleName();
    public static String BASE_ASSETS = "file:///android_asset/";

    public static String getProxyCountryCode(ProxyEntity proxy) throws Exception
    {
        String result = null;

        String stringUrl = (HTTP_FREEGEOIP_NET_JSON_STRING + proxy.getHost()).trim();
        result = getProxyCountryCode(stringUrl, proxy);

        if (TextUtils.isEmpty(result))
        {
            stringUrl = (HTTP_TELIZE_NET_JSON_STRING + proxy.getHost()).trim();
            result = getProxyCountryCode(stringUrl, proxy);
        }

        return result;
    }

    private static String getProxyCountryCode(String requestUrl, ProxyEntity proxy) throws JSONException
    {
        URI uri = null;

        int timeout = 1000 * 60;
        HttpAnswer answer = null;
        String result = null;

        try
        {
            Uri parsedUri = Uri.parse(requestUrl);
            if (parsedUri != null)
            {
                String parsedUriString = parsedUri.toString();
                uri = new URI(parsedUriString);
            }
        }
        catch (URISyntaxException e)
        {
            Timber.e(e, "Exception parsing URI on getProxyCountryCode");
        }

        if (uri != null)
        {
            try
            {
                Proxy proxyConf = APL.getProxySelectorConfiguration(uri);
                answer = ProxyUtils.getHttpAnswerURI(uri,proxyConf, 10000, timeout);
            }
            catch (Exception e)
            {
                Timber.w("Exception on getProxyCountryCode: " + e.toString());
            }

            if (answer != null)
            {
                String answerBody = answer.getBody();


                //ONLY FOR DEBUG EXCEPTION HANDLING IN CASE OF WRONG ANSWER FROM THE SERVER
//                answerBody = "</pre><span style=\"height: 20px; width: 40px; min-height: 20px; min-width: 40px; position: absolute; opacity: 0.85; z-index: 8675309; display: none; cursor: pointer; background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAUCAYAAAD/Rn+7AAADU0lEQVR42s2WXUhTYRjHz0VEVPRFUGmtVEaFUZFhHxBhsotCU5JwBWEf1EWEEVHQx4UfFWYkFa2biPJiXbUta33OXFtuUXMzJ4bK3Nqay7m5NeZq6h/tPQ+xU20zugjOxR/+7/O8539+5znnwMtNTExwJtMb3L/fiLv3botCSmUjeCaejTOb39AiFothfHxcFIrHY8RksZjBsckJcOIRMfFsHD/SsbExUYpnI8DR0dGUGjSb0byhEJp5Uqg5CTSzc2CQleJbMEj9/ywBcGRkJEk9DQqouEVQT1sK444yWI9UonmTjGqauVLEIlHa9x8lAMbj8SSpp0rwKGMVvg8P46vbg0C7na8z8JsMcgHe7jlEa+edRhiLy8n/TUMfu6EvLElk+U0WtGwrTrdfAGQf5J8iiK4LVzDU28t8JtMSocf8E+l68myaNFXm/6rXslLK7ay5TOunuRvZWpJuvwAYjUaTpOIWoquuAZ219RTaxKYp9BbjycoN5FvL9qH9TBX5rvoGdJythvXYSTxdtRnWylO/ZdqrLsGwszzhWQ593z2KlAwCYCQSSZJ6ehZ0W7bD9VBLgN0NCqr3qR7R2rBrL3pu3Sb/7nDlz2uy6cG0OXk0GTbZXzNp8trsPAQdTj6frlWzN2DcXZGKQQAMh8NJ6rpyHe+PnkCr/CAFdZyvpfpjuvkifLF9wIt1Wwlo0OHie1RvWrKa93RjzfzliTzPKz3ltB0/Tevmwp14wGUgHAzSOoUEwFAolFaaBSuhnslPRkJexUJtZ6v5HtUeLswl33n1BgEY5fvhs9sJ3FAiT+QYyyvoAQJuD0KBAFRTJNAuz5/s3gJgMBhMJwrVFRThM5tY5zUF/A4X1f2fvQTRLCuBreoim0YmAbqNJryvPEXeeq46kaNdkQ/1HCncbJKPs9ZSv2VHGfWsZ2hfkhKAfr8/pdxWKx4wwD69PmVfNSOL+lr2w+gYqHpWDtXt1xQ8AMlWU0e1lqLd/APRHoP8AJqWrQG9gYxcPMsvSJUvAA4MDKTUJ7MZLaVy8v+qT21tcDx/OemePr0RTkNrur4A6PP5xCgBsL+/X4wiQDpuuVxOeL1eMYmYeDY6sOp0z+B0OuHxeEQhxkJMFosJiSO/UinOI/8Pc+l7KKArAT8AAAAASUVORK5CYII=);\"></span><span id=\"buffer-extension-hover-button\" style=\"display: none;position: absolute;z-index: 8675309;width: 100px;height: 25px;background-image: url(chrome-extension://noojglkidnpfjbincgijbaiedldjfbhh/data/shared/img/buffer-hover-icon@1x.png);background-size: 100px 25px;opacity: 0.9;cursor: pointer;\"></span></body>";

                if (answer.getStatus() == HttpURLConnection.HTTP_OK && !TextUtils.isEmpty(answerBody))
                {
                    JSONObject jsonObject = null;

                    try
                    {
                        jsonObject = new JSONObject(answerBody);
                    }
                    catch (JSONException e)
                    {
                        //It's a common error to receive wrong answers due to the proxy servers
                        //between the Android device that make the request and the geoIP services
                        Timber.e("%s reading string: '%s'", e.toString(), answerBody);
                    }
                    catch (Exception e)
                    {
                        Timber.e(e,"Unhandled exception parsing JSON answer: '%s'",answerBody);
                    }

                    if (jsonObject != null && jsonObject.has("country_code"))
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
            appVersionName = ctx.getResources().getString(R.string.app_versionname, pi.versionName, pi.versionCode);
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
            Timber.e(e,"Exception on getAppInfo");
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
            Timber.e(e,"Exception starting Market activity: '%s",marketUri.toString());
        }

        if (!marketShown)
        {
            Toast.makeText(ctx, R.string.market_not_found, Toast.LENGTH_SHORT).show();
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
        AndroidMarket res = null;

        if (BuildConfig.MARKET_URI != null)
        {
            if (BuildConfig.MARKET_URI.equals(Constants.PLAY_MARKET_PACKAGE))
            {
                res = AndroidMarket.PLAY;
            }
            else if (BuildConfig.MARKET_URI.equals(Constants.AMAZON_MARKET_PACKAGE))
            {
                res = AndroidMarket.AMAZON;
            }
        }

        if (res == null)
        {
            res = AndroidMarket.OTHER;

            if (BuildConfig.DEBUG)
            {
                res = AndroidMarket.PLAY;
                Timber.d("Enabling Play market because during debug the InstallerPackageName is not filled: '%s'", BuildConfig.MARKET_URI);
            }
            else
            {
                Timber.e(new Exception(),"Got a not recognizable InstallerPackageName: '%s' ",BuildConfig.MARKET_URI);

            }
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
            Timber.e(e, "Exception getting Full Asset");
        }

        return text;
    }

    public static void checkDemoMode(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
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
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(Constants.PREFERENCES_DEMO_MODE, enabled);
        editor.commit();
    }

    public static boolean ElapsedNDays(Date date, int days)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);

        if (System.currentTimeMillis() >= c.getTime().getTime())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Intent createEmailOnlyChooserIntent(Context context, Intent source, CharSequence chooserTitle)
    {
        Stack<Intent> intents = new Stack<Intent>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "info@domain.com", null));
        List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(i, 0);

        for (ResolveInfo ri : activities)
        {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }

        if (!intents.isEmpty())
        {
            Intent chooserIntent = Intent.createChooser(intents.remove(0), chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
            return chooserIntent;
        }
        else
        {
            return Intent.createChooser(source, chooserTitle);
        }
    }

    public static void sendFeedbackMail(Context context)
    {
        /* Create the Intent */
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        /* Fill it with Data */
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@shouldit.be"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.mail_feedback_subject, getAppInfo(context).versionName));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        /* Send it off to the Activity-Chooser */
        try
        {
            context.startActivity(createEmailOnlyChooserIntent(context, emailIntent, "Send us an email..."));
        }
        catch (ActivityNotFoundException ex)
        {
            Toast.makeText(context, R.string.no_email_client_installed, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Timber.e(e,"Unhandled exception starting email client activity");
        }
    }

    public static Object cloneThroughJson(Object t)
    {
        App.getTraceUtils().startTrace(TAG,"cloneThroughJson", Log.DEBUG);
        Gson gson = new Gson();
        String json = gson.toJson(t);
        Object result = gson.fromJson(json, t.getClass());
        App.getTraceUtils().stopTrace(TAG,"cloneThroughJson", Log.DEBUG);

        return result;
    }

    public static boolean airplaneModeEnabled(Context context)
    {
        boolean result = false;

        try
        {
            result = isAirplaneModeOn(context);
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception getting airplaneModeEnabled");
        }

        return result;
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * @param context
     * @return true if enabled.
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeOn(Context context)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }
        else
        {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }
}
