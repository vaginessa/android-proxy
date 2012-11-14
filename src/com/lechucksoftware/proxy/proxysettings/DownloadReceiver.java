package com.lechucksoftware.proxy.proxysettings;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivity;
import com.lechucksoftware.proxy.proxysettings.services.DownloadService;

public class DownloadReceiver extends ResultReceiver
{
	ProxyPreferencesActivity _activity;
	
	public DownloadReceiver(Handler handler, ProxyPreferencesActivity activity)
	{
		super(handler);
		_activity = activity;
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData)
	{
		super.onReceiveResult(resultCode, resultData);
		if (resultCode == DownloadService.UPDATE_PROGRESS)
		{
			int progress = resultData.getInt("progress");
			_activity.mProgressDialog.setProgress(progress);
			if (progress == 100)
			{
				_activity.mProgressDialog.dismiss();
			}
		}
	}
}