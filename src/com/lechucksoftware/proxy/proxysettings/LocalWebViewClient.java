package com.lechucksoftware.proxy.proxysettings;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LocalWebViewClient extends WebViewClient
{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) 
    {
    	// Don't ever leave the local webview client
    	return false;
    }
}
