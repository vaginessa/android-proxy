package com.lechucksoftware.proxy.lib.activities;

import java.net.MalformedURLException;
import java.net.URI;

import com.lechucksoftware.proxy.lib.ProxyUtils;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewWithProxyActivity extends Activity
{
	WebView mWebView;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.webview);
	    ProxyUtils.setWebViewProxy(getApplicationContext());
	    
	    Bundle extras = getIntent().getExtras();
	    URI uri = (URI) extras.getSerializable("URI");

	    mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    
	    try
		{
			mWebView.loadUrl(uri.toURL().toString());
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
