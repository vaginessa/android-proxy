package com.lechucksoftware.proxy.proxysettings.dialogs;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.UrlManager;
import com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivity;
import com.lechucksoftware.proxy.proxysettings.activities.WebViewWithProxyActivity;

public class UrlBrowserDialog
{	
	public static AlertDialog newInstance(final ProxyPreferencesActivity activity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getResources().getString(R.string.preference_test_proxy_urlbrowser_dialog_title));
		
		View view = LayoutInflater.from(activity).inflate(R.layout.url_browser_dialog, (ViewGroup) activity.findViewById(R.id.layout_root));
		
		// Set an EditText view to get user input 
		final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.url_browser_dialog_autocomplete_text);
		
		String [] urls = UrlManager.getUsedUrls(activity);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_dropdown_item_1line, urls);
		input.setThreshold(1);
		input.setAdapter(adapter);
		
		builder.setView(view);
		
		builder.setPositiveButton(activity.getResources().getText(R.string.open), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
            	String uriString = input.getText().toString().trim();
				
            	URL url = null;
            	
				try
				{
					String guessedUrl = URLUtil.guessUrl(uriString);
					url = URI.create(guessedUrl).toURL();
				}
				catch(Exception e)
				{
					Toast.makeText(activity, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
					return;
				}
				
				UrlManager.addUsedUrl(activity, uriString);
                Intent webViewIntent = new Intent(activity.getApplicationContext(),WebViewWithProxyActivity.class);
                webViewIntent.putExtra("URL", url);
                activity.startActivity(webViewIntent);
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
