package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.util.Rfc822Tokenizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

import com.lechucksoftware.proxy.proxysettings.DownloadReceiver;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivity;
import com.lechucksoftware.proxy.proxysettings.services.DownloadService;

public class UrlDownloaderDialog
{
    private static final String[] uriTypes = new String[] {
    	"http://",
		"http://www.",
		"https://",
		"https://www.",
		"ftp://"
    };
		
	public static AlertDialog newInstance(final ProxyPreferencesActivity activity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getResources().getString(R.string.preference_test_proxy_urlretriever_dialog_title));
		
		View view = LayoutInflater.from(activity).inflate(R.layout.url_downloader_dialog, (ViewGroup) activity.findViewById(R.id.layout_root));
		
		// Set an EditText view to get user input 
		final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.url_downloader_dialog_autocomplete_text);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_dropdown_item_1line, uriTypes);
		input.setThreshold(1);
		input.setAdapter(adapter);
		
		builder.setView(view);
		
		builder.setPositiveButton(activity.getResources().getText(R.string.download), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				activity.mProgressDialog.show();
				String url = input.getText().toString();
				Intent intent = new Intent(activity.getApplicationContext(), DownloadService.class);
				intent.putExtra("url", url);
				
				// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				intent.putExtra("downloadFolder", "/mnt/sdcard/Download/");
				intent.putExtra("receiver", new DownloadReceiver(new Handler(), activity));
				activity.startService(intent);
			}
		});

		builder.setNegativeButton(activity.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{

			}
		});

		AlertDialog alert = builder.create();
		return alert;
	}
}
