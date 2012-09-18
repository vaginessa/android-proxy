package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import java.net.URI;
import java.net.Proxy.Type;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;
import com.shouldit.proxy.lib.ProxyUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ApplicationSubmitService extends IntentService 
{
	public static String TAG = "ApplicationSubmitService";
	
    public ApplicationSubmitService() 
    {
        super("ApplicationSubmitService");
    }
 
    @Override
    protected void onHandleIntent(Intent intent) 
    {
    	PInfo appInfo = (PInfo) intent.getSerializableExtra("appInfo");
        SubmitApplicationFeedback(getApplicationContext(),appInfo);
    }
    
    @Override
    public void onDestroy() 
    {
    	Log.d(TAG, "ApplicationSubmitService destroying");
    };
    
	/**
	 * @param context
	 */
	public void SubmitApplicationFeedback(Context context, PInfo appInfo) 
	{
		try
        {
			ProxyConfiguration proxyConf = ProxySettings.getCurrentHttpProxyConfiguration(context);
    		URI uri = URI.create("");
			String result = ProxyUtils.getURI(uri, proxyConf.proxyHost);	
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
}