package com.lechucksoftware.proxy.proxysettings.ui.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.LocalWebViewClient;

import java.net.URL;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import timber.log.Timber;

public class WebViewWithProxyActivity extends AppCompatActivity
{
	WebView mWebView;
	public static String TAG = WebViewWithProxyActivity.class.getSimpleName();

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Let's display the progress in the activity title bar, like the
		// browser app does.
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.webview);

		if (Build.VERSION.SDK_INT < 12)
        {
            try
            {
                ProxyUtils.setWebViewProxy(getApplicationContext(), APL.getCurrentHttpProxyConfiguration()); // Only
            }
            catch (Exception e)
            {
                Timber.e(e,"Exception setting proxy for WebView");
            }
        }																					  // for

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setLoadsImagesAutomatically(true);
//		mWebView.setBackgroundColor(0x00000000);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		final Activity activity = this;
		mWebView.setWebChromeClient(new WebChromeClient()
		{
			public void onProgressChanged(WebView view, int progress)
			{
				activity.setTitle("Loading...");		

				// Activities and WebViews measure progress with different
				// scales. The progress meter will automatically disappear when we reach 100%
				int activityProgress = progress * 100;
                		Timber.d("webprogress,activityprogress: " + progress + "," + activityProgress);
				activity.setProgress(activityProgress);


				if (progress == 100)
					activity.setTitle(R.string.app_name);
			}

		});

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new LocalWebViewClient());

		Bundle extras = getIntent().getExtras();
		URL url = (URL) extras.getSerializable("URL");

		mWebView.loadUrl(url.toString());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack())
		{
			mWebView.goBack();
			return true;
		}

		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		setProgressBarVisibility(true);

        Timber.d("Start");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
        Timber.d("Resume");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
        Timber.d("Pause");
	}

	@Override
	protected void onStop()
	{
        Timber.d("Stop");
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
        Timber.d("Destroy");
		super.onDestroy();
	}
}
