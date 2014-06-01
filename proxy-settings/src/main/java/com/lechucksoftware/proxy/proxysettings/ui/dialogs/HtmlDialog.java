package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;

public class HtmlDialog extends DialogFragment
{
    public static String TAG = HtmlDialog.class.getSimpleName();

    public String title;
    public String fileName;
    private WebView webView;
//    private ProgressDialog spinner;
    private HTMLDialogHandler htmlDialogHandler;

    public HtmlDialog()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        htmlDialogHandler = new HTMLDialogHandler();

//        htmlDialogHandler.sendMessage(HTMLDialogHandler.SHOW_PROGRESS_ACTION);

        webView = new WebView(getActivity());

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                App.getLogger().stopTrace(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);

//                try
//                {
//                    Thread.sleep(500);
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }

                htmlDialogHandler.sendMessage(HTMLDialogHandler.HIDE_PROGRESS_ACTION,
                        HTMLDialogHandler.SHOW_DIALOG_ACTION);
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale)
            {
                super.onScaleChanged(view, oldScale, newScale);
            }
        });

        webView.loadUrl(fileName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
        builder.setTitle(title);
        builder.setView(webView);

        builder.setPositiveButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                dismiss();
            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    private class HTMLDialogHandler extends Handler
    {
        public static final String SHOW_DIALOG_ACTION = "SHOW_DIALOG_ACTION";
        public static final String SHOW_PROGRESS_ACTION = "SHOW_PROGRESS_ACTION";
        public static final String HIDE_DIALOG_ACTION = "HIDE_DIALOG_ACTION";
        public static final String HIDE_PROGRESS_ACTION = "HIDE_PROGRESS_ACTION";

        @Override
        public void handleMessage(Message message)
        {
            Bundle b = message.getData();

            App.getLogger().w(TAG, "handleMessage: " + b.toString());

            if (b.containsKey(SHOW_DIALOG_ACTION))
            {

            }

            if (b.containsKey(SHOW_PROGRESS_ACTION))
            {
//                spinner.show();
            }

            if (b.containsKey(HIDE_DIALOG_ACTION))
            {

            }

            if (b.containsKey(HIDE_PROGRESS_ACTION))
            {
//                spinner.dismiss();
            }
        }

        public void sendMessage(String ... actions)
        {
            Message message = this.obtainMessage();
            Bundle b = new Bundle();

            for(String action:actions)
            {
                b.putString(action, "");
                message.setData(b);
            }

            sendMessageDelayed(message, 0);
        }
    }

    public static HtmlDialog newInstance(String tit, String file)
    {
        HtmlDialog frag = new HtmlDialog();

        // TODO: pass this arguments using the setArguments method
        frag.title = tit;
        frag.fileName = file;

        return frag;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        View v = inflater.inflate(R.layout.dialog_html, container, false);
//
//        App.getLogger().startTrace(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);
//
//        getDialog().setTitle(title);
//
//        webView = (WebView) v.findViewById(R.id.dialog_webview);
//        webView.setVisibility(View.GONE);
//
//        App.getLogger().getPartial(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);
//
//        webView.setWebViewClient(new WebViewClient(){
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url)
//            {
//                Uri uri = Uri.parse(url);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                getActivity().startActivity(intent);
//                return true;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url)
//            {
//                App.getLogger().stopTrace(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);
//
////                webView.requestLayout();
//
////                try
////                {
////                    Thread.sleep(1000);
////                }
////                catch (InterruptedException e)
////                {
////                    e.printStackTrace();
////                }
//
////                spinner.dismiss();
////                webView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onScaleChanged(WebView view, float oldScale, float newScale)
//            {
//                super.onScaleChanged(view, oldScale, newScale);
//            }
//        });
//
//        App.getLogger().getPartial(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);
//        webView.loadUrl(fileName);
//        App.getLogger().getPartial(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);
//
//        // Watch for button clicks.
//        Button button = (Button) v.findViewById(R.id.dialog_close);
//        button.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//                dismiss();
//            }
//        });
//
//        return v;
//    }
}
