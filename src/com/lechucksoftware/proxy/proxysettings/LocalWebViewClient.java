package com.lechucksoftware.proxy.proxysettings;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class LocalWebViewClient extends WebViewClient
{
	public static String TAG = "LocalWebViewClient"; 
	
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) 
    {
    	// Don't ever leave the local webview client
    	return false;
    }
    
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
    	// TODO Auto-generated method stub
    	super.onPageStarted(view, url, favicon);
    }
    
    @Override
    public void onLoadResource(WebView view, String url)
    {
    	super.onLoadResource(view, url);
    	Log.d(TAG, "onLoadResource: " + url);
    }
    
    @Override
    public void onPageFinished(WebView view, String url)
    {
    	// TODO Auto-generated method stub
    	super.onPageFinished(view, url);
    }
    
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
        Toast.makeText(view.getContext(), "Error: " + description, Toast.LENGTH_SHORT).show();
    }
}
