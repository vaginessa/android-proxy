package com.lechucksoftware.proxy.proxysettings.preferences;

import java.net.URI;
import java.net.URL;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivity;
import com.lechucksoftware.proxy.proxysettings.receivers.DownloadReceiver;
import com.lechucksoftware.proxy.proxysettings.services.DownloadService;
import com.lechucksoftware.proxy.proxysettings.utils.UrlManager;

public class UrlDownloaderDialogPreference extends DialogPreference
{
	AutoCompleteTextView input = null;
	
	public UrlDownloaderDialogPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setPersistent(false);
		setDialogLayoutResource(R.layout.url_downloader_dialog);
		setDialogTitle(context.getResources().getString(R.string.preference_test_proxy_urlretriever_dialog_title));
	}

	@Override
	protected View onCreateDialogView()
	{
		View root = super.onCreateDialogView();

		input = (AutoCompleteTextView) root.findViewById(R.id.url_downloader_dialog_autocomplete_text);
		String[] urls = UrlManager.getUsedUrls(root.getContext());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_dropdown_item_1line, urls);
		input.setThreshold(1);
		input.setAdapter(adapter);
		
		TextView pathdescriptionView = (TextView) root.findViewById(R.id.url_downloader_dialog_path_description);
		pathdescriptionView.setText(getContext().getResources().getText(R.string.preference_test_proxy_urlretriever_dialog_file_path_description));
		
		TextView pathView = (TextView) root.findViewById(R.id.url_downloader_dialog_path);
		pathView.setText("\"/mnt/sdcard/Download/\"");
		
		return root;
	}
	
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder)
	{		
		super.onPrepareDialogBuilder(builder);
		builder.setTitle(getContext().getResources().getString(R.string.preference_test_proxy_urlretriever_dialog_title));
		
		builder.setPositiveButton(getContext().getResources().getText(R.string.download), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				ProxyPreferencesActivity.instance.showProgressDialog();
				String urlstring = input.getText().toString();
				URL url = null;
				
				try
				{
					String guessedUrl = URLUtil.guessUrl(urlstring);
					url = URI.create(guessedUrl).toURL();
				}
				catch(Exception e)
				{
					Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
					return;
				}
				
				UrlManager.addUsedUrl(getContext(), urlstring);
				Intent intent = new Intent(getContext(), DownloadService.class);
				intent.putExtra("URL", url);
				
				// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				intent.putExtra("downloadFolder", "/mnt/sdcard/Download/");
				intent.putExtra("receiver", new DownloadReceiver(new Handler(), ProxyPreferencesActivity.instance));
				getContext().startService(intent);
			}
		});
	}
	
	
}
