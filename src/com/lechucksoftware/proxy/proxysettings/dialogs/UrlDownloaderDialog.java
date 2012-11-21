package com.lechucksoftware.proxy.proxysettings.dialogs;

import java.net.URI;
import java.net.URL;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.DownloadReceiver;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.UrlManager;
import com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivity;
import com.lechucksoftware.proxy.proxysettings.services.DownloadService;

public class UrlDownloaderDialog
{    
	public static SharedPreferences sharedPref;
			
	public static AlertDialog newInstance(final ProxyPreferencesActivity activity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getResources().getString(R.string.preference_test_proxy_urlretriever_dialog_title));
		
		View view = LayoutInflater.from(activity).inflate(R.layout.url_downloader_dialog, (ViewGroup) activity.findViewById(R.id.layout_root));
		
		// Set an EditText view to get user input 
		final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.url_downloader_dialog_autocomplete_text);
		
		String [] urls = UrlManager.getUsedUrls(activity);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_dropdown_item_1line, urls);
		input.setThreshold(1);
		input.setAdapter(adapter);
		
		TextView pathdescriptionView = (TextView) view.findViewById(R.id.url_downloader_dialog_path_description);
		pathdescriptionView.setText(activity.getResources().getText(R.string.preference_test_proxy_urlretriever_dialog_file_path_description));
		
		TextView pathView = (TextView) view.findViewById(R.id.url_downloader_dialog_path);
		pathView.setText("\"/mnt/sdcard/Download/\"");
		
		builder.setView(view);
		
		builder.setPositiveButton(activity.getResources().getText(R.string.download), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				activity.mProgressDialog.show();
				String urlstring = input.getText().toString();
				URL url = null;
				
				try
				{
					String guessedUrl = URLUtil.guessUrl(urlstring);
					url = URI.create(guessedUrl).toURL();
				}
				catch(Exception e)
				{
					Toast.makeText(activity, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
					return;
				}
				
				UrlManager.addUsedUrl(activity, urlstring);
				Intent intent = new Intent(activity.getApplicationContext(), DownloadService.class);
				intent.putExtra("URL", url);
				
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
