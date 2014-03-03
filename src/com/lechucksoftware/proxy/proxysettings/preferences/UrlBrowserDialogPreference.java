package com.lechucksoftware.proxy.proxysettings.preferences;

import java.net.URI;
import java.net.URL;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.WebViewWithProxyActivity;
import com.lechucksoftware.proxy.proxysettings.utils.UrlManager;

public class UrlBrowserDialogPreference extends DialogPreference
{
	AutoCompleteTextView input = null;
	
	public UrlBrowserDialogPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setPersistent(false);
		setDialogLayoutResource(R.layout.url_browser_dialog);
		setDialogTitle(context.getResources().getString(R.string.preference_test_proxy_urlbrowser_dialog_title));
	}

	@Override
	protected View onCreateDialogView()
	{
		View root = super.onCreateDialogView();

		input = (AutoCompleteTextView) root.findViewById(R.id.url_browser_dialog_autocomplete_text);
		String[] urls = UrlManager.getUsedUrls(root.getContext());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_dropdown_item_1line, urls);
		input.setThreshold(1);
		input.setAdapter(adapter);
		
		return root;
	}
	
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder)
	{		
		super.onPrepareDialogBuilder(builder);
		builder.setTitle(getContext().getResources().getString(R.string.preference_test_proxy_urlbrowser_dialog_title));
		
		builder.setPositiveButton(getContext().getResources().getText(R.string.open), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface paramDialogInterface, int paramInt)
			{
				String urlstring = input.getText().toString().trim();
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
                Intent webViewIntent = new Intent(getContext().getApplicationContext(),WebViewWithProxyActivity.class);
                webViewIntent.putExtra("URL", url);
                getContext().startActivity(webViewIntent);
			}
		});
	}
	
	
}
