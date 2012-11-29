package com.lechucksoftware.proxy.proxysettings.dialogs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.UrlManager;

public class UrlDownloaderDialogPreference extends DialogPreference
{

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

		final AutoCompleteTextView input = (AutoCompleteTextView) root.findViewById(R.id.url_downloader_dialog_autocomplete_text);
		String[] urls = UrlManager.getUsedUrls(root.getContext());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_dropdown_item_1line, urls);
		input.setThreshold(1);
		input.setAdapter(adapter);
		
		return root;
	}
}
