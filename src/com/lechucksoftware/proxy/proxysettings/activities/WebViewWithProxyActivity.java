package com.lechucksoftware.proxy.proxysettings.activities;

import java.net.MalformedURLException;
import java.net.URI;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.LocalWebViewClient;
import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.proxy.lib.ProxyUtils;

public class WebViewWithProxyActivity extends FragmentActivity
{
	WebView mWebView;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.webview);
	    
	    if(Build.VERSION.SDK_INT < 12)
	    	ProxyUtils.setWebViewProxy(getApplicationContext(), Globals.getInstance().proxyConf);	// Only for 1.x and 2.x devices
	    
	    Bundle extras = getIntent().getExtras();
	    URI uri = (URI) extras.getSerializable("URI");

	    mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.setWebViewClient(new LocalWebViewClient());
	    
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // Check if the key event was the Back button and if there's history
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack())
	    {
	    	mWebView.goBack();
	        return true;
	    }
	    // If it wasn't the Back key or there's no web page history, bubble up to the default
	    // system behavior (probably exit the activity)
	    return super.onKeyDown(keyCode, event);
	}
}
