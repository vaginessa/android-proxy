package com.lechucksoftware.proxy.proxysettings.ui.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.components.EnhancedProgress;

public class HtmlDialog extends DialogFragment
{
    public static String TAG = HtmlDialog.class.getSimpleName();

    public String title;
    public String fileName;
    private WebView webView;
    private EnhancedProgress progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dialog_html, container, false);

        App.getLogger().startTrace(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);

        getDialog().setTitle(title);

        webView = (WebView) v.findViewById(R.id.dialog_webview);
        webView.setVisibility(View.GONE);
        progress = (EnhancedProgress) v.findViewById(R.id.dialog_progress);
        progress.setVisibility(View.VISIBLE);

        App.getLogger().getPartial(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                App.getLogger().stopTrace(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);

//                webView.requestLayout();

//                try
//                {
//                    Thread.sleep(1000);
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }

                webView.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale)
            {
                super.onScaleChanged(view, oldScale, newScale);
            }
        });

        App.getLogger().getPartial(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);
        webView.loadUrl(fileName);
        App.getLogger().getPartial(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);

        // Watch for button clicks.
        Button button = (Button) v.findViewById(R.id.dialog_close);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                dismiss();
            }
        });

        return v;
    }

    public static HtmlDialog newInstance(String tit, String file)
    {
        HtmlDialog frag = new HtmlDialog();
        frag.title = tit;
        frag.fileName = file;
        return frag;
    }
}
