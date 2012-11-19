package com.lechucksoftware.proxy.proxysettings.activities;

import java.net.MalformedURLException;
import java.net.URI;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.LocalWebViewClient;
import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.proxy.lib.ProxyUtils;

public class WebViewWithProxyActivity extends FragmentActivity
{
	WebView mWebView;
	public static String TAG = "WebViewWithProxyActivity";
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
	    // Let's display the progress in the activity title bar, like the
	    // browser app does.
	    getWindow().requestFeature(Window.FEATURE_PROGRESS);
		
	    setContentView(R.layout.webview);
	    
	    if(Build.VERSION.SDK_INT < 12)
	    	ProxyUtils.setWebViewProxy(getApplicationContext(), Globals.getInstance().proxyConf);	// Only for 1.x and 2.x devices
	    	    
	    mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);

	    final Activity activity = this;
	    mWebView.setWebChromeClient(new WebChromeClient() 
	    {
	      public void onProgressChanged(WebView view, int progress) 
	      {
	        // Activities and WebViews measure progress with different scales.
	        // The progress meter will automatically disappear when we reach 100%
	        activity.setProgress(progress * 1000);
	      }
	    });
    
	    mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.setWebViewClient(new LocalWebViewClient());

	    Bundle extras = getIntent().getExtras();
	    URI uri = (URI) extras.getSerializable("URI");
	    
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
	
    public void onBackPressed() 
    {
    	Log.d(TAG, "Back Pressed");
    	return;
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        setProgressBarVisibility(true);
        
        Log.d(TAG, "Start");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }
    @Override
    protected void onStop() {
    	Log.d(TAG, "Stop");
        super.onStop();
    }
    @Override
    protected void onDestroy() {
    	Log.d(TAG, "Destroy");
        super.onDestroy();
    }
}
