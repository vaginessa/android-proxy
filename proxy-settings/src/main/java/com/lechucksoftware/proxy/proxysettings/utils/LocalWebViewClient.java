package com.lechucksoftware.proxy.proxysettings.utils;


import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import io.should.proxy.lib.log.LogWrapper;

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
		LogWrapper.d(TAG, "onLoadResource: " + (url.length() > 50 ? url.substring(0, 50) : url));
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
