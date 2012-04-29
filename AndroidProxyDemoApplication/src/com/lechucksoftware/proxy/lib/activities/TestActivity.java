/**
 * 
 */
package com.lechucksoftware.proxy.lib.activities;

import java.net.URI;
import android.app.Activity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.lechucksoftware.proxy.lib.ProxyConfiguration;
import com.lechucksoftware.proxy.lib.ProxySettings;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		uriInput = (AutoCompleteTextView) findViewById(R.id.uri_input);
		device_version = (TextView) findViewById(R.id.device_api_version_content);
		proxy_enabled = (TextView) findViewById(R.id.proxy_enabled_content);
		proxy_host = (TextView) findViewById(R.id.proxy_host_content);
		proxy_port = (TextView) findViewById(R.id.proxy_port_content);
		apl_tostring = (TextView) findViewById(R.id.apl_proxy_tostring_content);
		
		UpdateSettings();
	}
	
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