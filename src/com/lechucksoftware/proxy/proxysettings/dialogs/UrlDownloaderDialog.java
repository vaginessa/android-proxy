package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.lechucksoftware.proxy.proxysettings.DownloadReceiver;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivity;
import com.lechucksoftware.proxy.proxysettings.services.DownloadService;

public class UrlDownloaderDialog
{
	static String[] uriTypes = 
		{
    		"http://",
    		"http://www.",
    		"https://",
    		"https://www.",
    		"ftp://",
		};
	
	public static AlertDialog newInstance(final ProxyPreferencesActivity activity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getResources().getString(R.string.preference_test_proxy_urlretriever_dialog_title));
		
		// Set an EditText view to get user input 
		final AutoCompleteTextView input = new AutoCompleteTextView(activity);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_dropdown_item_1line, uriTypes);
		input.setThreshold(1);
		input.setAdapter(adapter);
		
		builder.setView(input);
		
		builder.setPositiveButton(activity.getResources().getText(R.string.app_rater_dialog_button_rate), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				activity.mProgressDialog.show();
				String url = input.getText().toString();
				Intent intent = new Intent(activity.getApplicationContext(), DownloadService.class);
				intent.putExtra("url", url);
				intent.putExtra("receiver", new DownloadReceiver(new Handler(), activity));
				activity.startService(intent);
			}
		});

		builder.setNegativeButton(activity.getResources().getText(R.string.app_rater_dialog_button_nothanks), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{

			}
		});

		AlertDialog alert = builder.create();
		return alert;
	}
}
