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
import android.widget.RelativeLayout;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.LocaleManager;

public class AboutDialog extends DialogFragment
{
    public static String TAG = AboutDialog.class.getSimpleName();
    private WebView webView;
    private RelativeLayout progress;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dialog_html, container, false);

        App.getLogger().startTrace(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);

        getDialog().setTitle(R.string.about);

        webView = (WebView) v.findViewById(R.id.dialog_webview);
        webView.setVisibility(View.GONE);
        progress = (RelativeLayout) v.findViewById(R.id.progress);
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

                progress.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale)
            {
                super.onScaleChanged(view, oldScale, newScale);
            }
        });

        String BASE_URL = "file:///android_asset/www/www-" + LocaleManager.getTranslatedAssetLanguage() + '/';
        App.getLogger().getPartial(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);
        webView.loadUrl(BASE_URL + "about.html");
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

    public static AboutDialog newInstance()
    {
        AboutDialog frag = new AboutDialog();
        return frag;
    }
}
