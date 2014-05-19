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
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.LocaleManager;

public class AboutDialog extends DialogFragment
{
    public static String TAG = AboutDialog.class.getSimpleName();
    private TextView aboutVersionTextView;
    private TextView aboutOSSTextView;
    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.about_html, container, false);

        getDialog().setTitle(R.string.about);

        webView = (WebView) v.findViewById(R.id.about_webview);

        App.getLogger().getPartial(TAG, "showHTMLAssetsAlertDialog", Log.DEBUG);

        String BASE_URL = "file:///android_asset/www/www-" + LocaleManager.getTranslatedAssetLanguage() + '/';
        webView.loadUrl(BASE_URL + "about.html");
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
                return true;
            }
        });

//        aboutVersionTextView = (TextView) v.findViewById(R.id.about_version);
//        aboutOSSTextView = (TextView) v.findViewById(R.id.about_opensource);
//
//        aboutVersionTextView.setText(getResources().getString(R.string.app_versionname, Utils.getAppVersionName(getActivity())));
//
//        // TODO: Evaluate use of spannable: http://eazyprogramming.blogspot.it/2013/06/spannable-string-in-android-url-span.html
////        Spanned spanned = Html.fromHtml(getResources().getString(R.string.about_opensource));
//        Spanned spanned = Html.fromHtml("<a href=\"mailto:info@shouldit.be\">MAILTO</a>");
//
//        aboutOSSTextView.setText(spanned);

        // Watch for button clicks.
        Button button = (Button) v.findViewById(R.id.about_close);
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
