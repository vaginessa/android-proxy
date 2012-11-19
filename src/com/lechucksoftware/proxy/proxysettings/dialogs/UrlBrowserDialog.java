package com.lechucksoftware.proxy.proxysettings.dialogs;

import java.net.URI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
            	
				UrlManager.addUsedUrl(activity, uriString);
				
        		URI uri = URI.create(uriString);
                Intent webViewIntent = new Intent(activity.getApplicationContext(),WebViewWithProxyActivity.class);
                webViewIntent.putExtra("URI", uri);
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
