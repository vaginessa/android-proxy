package com.lechucksoftware.proxy.proxysettings.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.R.string;
import com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivity;
import com.lechucksoftware.proxy.proxysettings.services.DownloadService;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

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
			long downloaded = resultData.getLong("downloaded");

			String message = (String) _activity.getResources().getText(R.string.preference_test_proxy_urlretriever_dialog_status);
			message = message.concat(" " + String.valueOf(downloaded) + " bytes");
			_activity.setProgressDialogMessage(message);
			
			if (resultData.getBoolean("finish"))
			{
				_activity.dismissProgressDialog();
				UIUtils.NotifyCompletedDownload(_activity,resultData.getString("filename"));
			}
		}
		else if (resultCode == DownloadService.UPDATE_EXCEPTION)
		{
			_activity.dismissProgressDialog();
			
			Exception e = (Exception) resultData.getSerializable("exception");
			UIUtils.NotifyExceptionOnDownload(_activity, e.getMessage());
		}
	}
}