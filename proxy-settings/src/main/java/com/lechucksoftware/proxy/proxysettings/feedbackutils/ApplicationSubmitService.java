package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.net.Proxy;
import java.net.URI;

import be.shouldit.proxy.lib.APL;
import be.shouldit.proxy.lib.constants.APLConstants;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import timber.log.Timber;

public class ApplicationSubmitService extends IntentService 
{
	public static String TAG = ApplicationSubmitService.class.getSimpleName();
	
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
        Timber.d("ApplicationSubmitService destroying");
    }
    
	/**
	 * @param context
	 */
	public void SubmitApplicationFeedback(Context context, PInfo appInfo) 
	{
		try
        {
			Proxy proxyConf = APL.getCurrentHttpProxyConfiguration();
    		URI uri = URI.create("");
			String result = ProxyUtils.getURI(uri, proxyConf, APLConstants.MAX_DOWNLOAD_LENGTH, APLConstants.DEFAULT_TIMEOUT);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
}
