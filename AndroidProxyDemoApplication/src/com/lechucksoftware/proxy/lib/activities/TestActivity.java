/**
 * 
 */
package com.lechucksoftware.proxy.lib.activities;

import java.net.URI;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.lechucksoftware.proxy.lib.ProxyConfiguration;
import com.lechucksoftware.proxy.lib.ProxySettings;
import com.lechucksoftware.proxy.lib.ProxyUtils;

public class TestActivity extends Activity
{
	public static final String TAG = "TestActivity";
	static final int DIALOG_ID_PROXY = 0;

	
	String[] uriTypes = 
		{
    		"http://",
    		"http://www.",
    		"https://",
    		"https://www.",
    		"ftp://",
		};
	
	AutoCompleteTextView uriInput;
	TextView device_version;
	TextView proxy_enabled;
	TextView proxy_host;
	TextView proxy_port;
	TextView apl_tostring;
	Button get_settings;
	Button edit_settings;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
			
		uriInput = (AutoCompleteTextView) findViewById(R.id.uri_input);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, uriTypes);
		uriInput.setThreshold(1);
		uriInput.setAdapter(adapter);
		
		device_version = (TextView) findViewById(R.id.device_api_version_content);
		proxy_enabled = (TextView) findViewById(R.id.proxy_enabled_content);
		proxy_host = (TextView) findViewById(R.id.proxy_host_content);
		proxy_port = (TextView) findViewById(R.id.proxy_port_content);
		apl_tostring = (TextView) findViewById(R.id.apl_proxy_tostring_content);
		
		get_settings = (Button) findViewById(R.id.get_settings);
		get_settings.setOnClickListener(OnGetSettingsClick);
		
		edit_settings = (Button) findViewById(R.id.open_proxy_settings);
		edit_settings.setOnClickListener(OnEditProxySettings);
		
		UpdateSettings();
	}
	
	private final OnClickListener OnGetSettingsClick = new OnClickListener() {
		
		@Override
		public void onClick(View v)
		{
			UpdateSettings();
		}
	};
	
	private final OnClickListener OnEditProxySettings = new OnClickListener() {
		
		@Override
		public void onClick(View v)
		{
			Intent proxyIntent = ProxyUtils.getProxyIntent();
			startActivity(proxyIntent);
			UpdateSettings();
		}
	};
	
	
	public void UpdateSettings()
	{
		String uriString = uriInput.getText().toString();
		URI uri = URI.create(uriString);
		ProxyConfiguration proxyConf = null;
		
		try
		{
			proxyConf = ProxySettings.getCurrentProxyConfiguration(getApplicationContext(), uri);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (proxyConf != null)
		{
			ShowSettings(proxyConf);
		}
	}
	
	public void ShowSettings(ProxyConfiguration proxyConf)
	{
		device_version.setText(String.valueOf(proxyConf.deviceVersion));
		proxy_enabled.setText(String.valueOf(proxyConf.isProxyEnabled()));
		apl_tostring.setText(proxyConf.toString());
		
		switch (proxyConf.getConnectionType())
		{
			case DIRECT:
				proxy_host.setText(getApplicationContext().getResources().getString(R.id.proxy_host_content));
				proxy_port.setText(getApplicationContext().getResources().getString(R.id.proxy_port_content));
				break;
			case HTTP:
				proxy_host.setText(String.valueOf(proxyConf.getProxyHost()));
				proxy_port.setText(String.valueOf(proxyConf.getProxyPort()));
				break;
			case SOCKS:
				throw new UnsupportedOperationException("SOCKS not already supported");
		}
	}
}