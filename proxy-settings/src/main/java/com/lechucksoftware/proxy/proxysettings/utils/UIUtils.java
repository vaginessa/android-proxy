package com.lechucksoftware.proxy.proxysettings.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WiFiApListActivity;
import com.lechucksoftware.proxy.proxysettings.constants.CodeNames;
import com.shouldit.proxy.lib.enums.CheckStatusValues;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.log.LogWrapper;
import com.shouldit.proxy.lib.utils.ProxyUIUtils;

import java.io.File;
import java.net.Proxy.Type;
import java.util.Random;

public class UIUtils
{
    public static final String TAG = "UIUtils";

    public static int PROXY_NOTIFICATION_ID = 1;
    public static int URL_DOWNLOADER_COMPLETED_ID = 2;

    public static void showError(Context ctx, String errorMessage)
    {
        new AlertDialog.Builder(ctx)
                .setTitle(R.string.proxy_error)
                .setMessage(errorMessage)
                .setPositiveButton(R.string.proxy_error_dismiss, null)
                .show();
    }

    public static void showError(Context ctx, int error)
    {
        showError(ctx, ctx.getResources().getString(error));
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static int booleanToVisibility(boolean b)
    {
        if (b)
        {
            return View.VISIBLE;
        }
        else
        {
            return View.GONE;
        }
    }

    public static int getTagsColor(Context ctx, int i)
    {
        int c;

        switch (i)
        {
            case 1:
                c = ctx.getResources().getColor(R.color.Holo_Red_Light);
                break;
            case 2:
                c = ctx.getResources().getColor(R.color.Holo_Yellow_Light);
                break;
            case 3:
                c = ctx.getResources().getColor(R.color.Holo_Green_Light);
                break;
            case 4:
                c = ctx.getResources().getColor(R.color.Holo_Purple_Light);
                break;
            case 5:
                c = ctx.getResources().getColor(R.color.Holo_Blue_Dark);
                break;

            default:
                c = ctx.getResources().getColor(R.color.Gray);
                break;
        }

        return c;
    }

    public static BitmapDrawable writeWarningOnDrawable(Context callerContext, int drawableId, String text)
    {
        return writeOnDrawable(callerContext, drawableId, text, Color.rgb(0xFF, 0xBB, 0x33));
    }

    public static BitmapDrawable writeErrorOnDrawable(Context callerContext, int drawableId, String text)
    {
        return writeOnDrawable(callerContext, drawableId, text, Color.rgb(0xFF, 0x44, 0x44));
    }

    public static BitmapDrawable writeErrorDisabledOnDrawable(Context callerContext, int drawableId, String text)
    {
        BitmapDrawable bd = writeOnDrawable(callerContext, drawableId, text, Color.RED);
        return bd;
    }

    public static BitmapDrawable writeOnDrawable(Context callerContext, int drawableId, String text, int color)
    {
        Bitmap bm = BitmapFactory.decodeResource(callerContext.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Style.FILL);
        paint.setColor(color);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(20);

        /*		      
         *            W					
         **************************
         *            *  	      *
         *            *  	      *
         *            *           *
         *            *           *
         *            *           *
         *            *           *
         **************************	H
         *            *  	      *
         *            *  	      *
         *            *     ##### *
         *            *     ##### *
         *            *     ##### *
         *            *    		  *
         **************************
         *
         */

        Canvas canvas = new Canvas(bm);

        int w = bm.getWidth();
        int h = bm.getHeight();

        int x0 = (int) (w * 0.65);
        int x1 = (int) (w * 0.99);
        int xr = (int) (w * 0.72);

        int y0 = (int) (h * 0.65);
        int y1 = (int) (h * 0.99);
        int yr = (int) (h * 0.94);

//		LogWrapper.d(TAG, String.format("W: %d; H: %d; ", w, h));
//		LogWrapper.d(TAG, String.format("x0: %d; x1: %d; xm: %d; y0: %d; y1: %d; ym: %d;", x0, x1, xr, y1, y0, yr));

        canvas.drawRect(new Rect(x0, y0, x1, y1), paint);
        paint.setColor(Color.WHITE);
        canvas.drawText(text, xr, yr, paint);

        BitmapDrawable bd = new BitmapDrawable(callerContext.getResources(), bm);
        return bd;
    }

    public static AlertDialog getBetaTestDialog(final Context ctx)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.beta_testing);
        builder.setMessage(R.string.beta_testing_instructions);
        builder.setPositiveButton(R.string.cont, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                openBetaTestProject(ctx);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public static void openBetaTestProject(Context ctx)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/communities/104290788068260973104"));
        ctx.startActivity(browserIntent);
    }

    public static void showHTMLAssetsAlertDialog(final Context ctx, String title, String filename, String closeString, final DialogInterface.OnDismissListener mOnDismissListener)
    {
        String BASE_URL = "file:///android_asset/www/www-" + LocaleManager.getTranslatedAssetLanguage() + '/';

        LogWrapper.startTrace(TAG,"showHTMLAssetsAlertDialog", Log.DEBUG);

        try
        {
            //Create web view and load html
            final WebView webView = new WebView(ctx);
            webView.setWebViewClient(new WebViewClient()
            {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url)
                {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    ctx.startActivity(intent);
                    return true;
                }

            });

            LogWrapper.getPartial(TAG,"showHTMLAssetsAlertDialog", Log.DEBUG);

            webView.loadUrl(BASE_URL + filename);

            LogWrapper.getPartial(TAG,"showHTMLAssetsAlertDialog", Log.DEBUG);

            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                    .setTitle(title)
                    .setView(webView)
                    .setPositiveButton(closeString, new Dialog.OnClickListener()
                    {
                        public void onClick(final DialogInterface dialogInterface, final int i)
                        {
                            dialogInterface.dismiss();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener()
                    {

                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            dialog.dismiss();
                        }
                    });

            LogWrapper.getPartial(TAG,"showHTMLAssetsAlertDialog", Log.DEBUG);

            AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(final DialogInterface dialog)
                {
                    if (mOnDismissListener != null)
                    {
                        mOnDismissListener.onDismiss(dialog);
                    }
                }
            });

            LogWrapper.getPartial(TAG,"showHTMLAssetsAlertDialog", Log.DEBUG);

            dialog.show();
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
            return;
        }

        LogWrapper.stopTrace(TAG,"showHTMLAssetsAlertDialog", Log.DEBUG);
    }

    public static void showHTMLAlertDialog(final Context ctx, String title, String htmlText, String closeString, final DialogInterface.OnDismissListener mOnDismissListener)
    {
        //Create web view and load html
        final WebView webView = new WebView(ctx);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                ctx.startActivity(intent);
                return true;
            }

        });

        webView.loadDataWithBaseURL(null, htmlText, "text/html", "utf-8", null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setView(webView)
                .setPositiveButton(closeString, new Dialog.OnClickListener()
                {
                    public void onClick(final DialogInterface dialogInterface, final int i)
                    {
                        dialogInterface.dismiss();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener()
                {

                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(final DialogInterface dialog)
            {
                if (mOnDismissListener != null)
                {
                    mOnDismissListener.onDismiss(dialog);
                }
            }
        });
        dialog.show();
    }

    public static String GetStatusSummary(ProxyConfiguration conf, Context ctx)
    {
        //		if (ApplicationGlobals.getInstance().proxyCheckStatus == ProxyCheckStatus.CHECKING)
        {
            return ProxyUIUtils.GetStatusTitle(conf, ctx);
        }
        //		else
        //		{
        // if (APLGlobals.getInstance().proxyConf.status.getEnabled())
        // {
        //			return UIUtils.ProxyConfigToStatusString(ctx);
        // }
        // else
        // {
        // return
        // ctx.getText(R.string.preference_proxy_host_port_summary_default).toString();
        // }
        //		}
    }

    public static void UpdateStatusBarNotification(ProxyConfiguration conf, Context context)
    {
        if (conf == null)
        {
            EventReportingUtils.sendException(new Exception("Cannot find valid instance of ProxyConfiguration"));
            return;
        }

        if (conf.getCheckingStatus() == CheckStatusValues.CHECKED)
        {
            if (conf.getProxy().type() == Type.DIRECT)
            {
                DisableProxyNotification(context);
            }
            else
            {
                SetProxyNotification(conf, context);
            }
        }
        else
        {

        }
    }

    /**
     * Notification related methods
     */
    public static void SetProxyNotification(ProxyConfiguration conf, Context callerContext)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callerContext);
        if (prefs.getBoolean("preference_notification_enabled", false))
        {

            String notificationTitle = ProxyUIUtils.GetStatusTitle(conf, callerContext);
            String notificationDescription = ProxyUIUtils.GetStatusDescription(conf, callerContext);

            // The PendingIntent will launch activity if the user selects this
            // notification
            Intent preferencesIntent = new Intent(callerContext, WiFiApListActivity.class);
            EnableProxyNotification(callerContext, preferencesIntent, notificationTitle, notificationDescription);
        }
        else
        {
            DisableProxyNotification(callerContext);
        }
    }

    public static void NotifyCompletedDownload(Context callerContext, String downloadedFilePath)
    {
        // Intent intent = new Intent();
        // intent.setAction(android.content.Intent.ACTION_VIEW);
        File downloadedFile = new File(downloadedFilePath);
        // intent.setData(Uri.fromFile(downloadedFile.getParentFile()));

        // NotificationManager manager = (NotificationManager)
        // callerContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // PendingIntent contentIntent =
        // PendingIntent.getActivity(callerContext, 0, intent, 0);
        //
        // NotificationCompat.Builder builder = new
        // NotificationCompat.Builder(callerContext);
        // builder.setContentIntent(contentIntent).
        // setSmallIcon(R.drawable.ic_stat_proxy_notification).
        // setTicker("Proxy Settings completed a download ...").
        // setWhen(System.currentTimeMillis()).
        // setContentTitle(downloadedFile.getName()).
        // setContentText("Download completed ");
        //
        // Notification n;
        // n = builder.getNotification();
        //
        // manager.notify(URL_DOWNLOADER_COMPLETED_ID, n);

        CharSequence text = downloadedFile.getName() + " " + callerContext.getResources().getText(R.string.preference_test_proxy_urlretriever_dialog_file_saved);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(callerContext, text, duration);
        toast.show();
    }

    public static void NotifyExceptionOnDownload(Context callerContext, String exceptionDetail)
    {
        CharSequence text = callerContext.getResources().getText(R.string.preference_test_proxy_urlretriever_dialog_file_exception) + "\n\n" + exceptionDetail;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(callerContext, text, duration);
        toast.show();
    }

    private static void EnableProxyNotification(Context callerContext, Intent intentToCall, String notificationTitle, String notificationDescription)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(callerContext)
                        .setSmallIcon(R.drawable.ic_stat_proxy_notification)
                        .setContentTitle(notificationTitle)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setContentText(notificationDescription);


        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(callerContext);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(WiFiApListActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intentToCall);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) callerContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(PROXY_NOTIFICATION_ID, mBuilder.build());
    }

    public static void DisableProxyNotification(Context callerContext)
    {
        if (callerContext != null)
        {
            NotificationManager manager = (NotificationManager) callerContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
            {
                try
                {
                    manager.cancel(PROXY_NOTIFICATION_ID);
                }
                catch (Exception e)
                {
                    EventReportingUtils.sendException(e);
                }
            }
        }
    }

    public static CodeNames getRandomCodeName()
    {
        int pick = new Random().nextInt(CodeNames.values().length);
        return CodeNames.values()[pick];
    }
}
