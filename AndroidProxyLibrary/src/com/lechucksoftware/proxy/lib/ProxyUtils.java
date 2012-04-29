package com.lechucksoftware.proxy.lib;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

public class ProxyUtils
{
	public static final String TAG = "ProxyUtils";
	
	/**
	 * For API < 12
	 * */
	public static Intent getGlobalProxyIntent()
	{
		Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings","com.android.settings.ProxySelector"));
    	
        return intent;
	}
	
	public static boolean isSystemProxyReachable(HttpHost proxy)
	{
		int exitValue;
		Runtime runtime = Runtime.getRuntime();
		Process proc;

		try {
			proc = runtime.exec("ping -c 1   " + proxy.getHostName());
			proc.waitFor();
			exitValue = proc.exitValue();

			Log.d(TAG, "Ping exit value: " + exitValue);

			if (exitValue == 0)
				return true;
			else
				return false;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean isInternetReachable(HttpHost proxy)
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);

		HttpGet request;
		HttpResponse response;

		try {
			request = new HttpGet("http://www.google.com");
			response = httpclient.execute(request);

			Log.d(TAG, "Is internet reachable : "
					+ response.getStatusLine().toString());
			if (response != null
					&& response.getStatusLine().getStatusCode() == 200) {
				return true;
			} else
				return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}
