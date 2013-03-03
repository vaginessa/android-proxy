package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.preferences.ValidationPreference;
import com.lechucksoftware.proxy.proxysettings.preferences.ValidationPreference.ValidationStatus;
import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyStatusItem;

public class ProxyCheckerPrefsFragment extends PreferenceFragment
{
	private ValidationPreference proxyEnabledPref;
	private ValidationPreference proxyValidAddressPref;
	private ValidationPreference proxyReachablePref;
	private ValidationPreference proxyWebPref;
	private ValidationPreference proxyValidHostPref;
	private ValidationPreference proxyValidPortPref;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.checker_preferences);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);


	}
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = super.onCreateView(inflater, container, savedInstanceState);
		
		getUIComponents();
		refreshUIComponents();
		
		return v;
	}

	private void getUIComponents()
	{
		proxyEnabledPref = (ValidationPreference) findPreference("validation_proxy_enabled");
		proxyValidHostPref = (ValidationPreference) findPreference("validation_proxy_valid_host");
		proxyValidPortPref = (ValidationPreference) findPreference("validation_proxy_valid_port");
		proxyReachablePref = (ValidationPreference) findPreference("validation_proxy_reachable");
		proxyWebPref = (ValidationPreference) findPreference("validation_web_reachable");
	}

	public void refreshUIComponents()
	{
		if (proxyEnabledPref != null)
		{
			ProxyConfiguration conf = ApplicationGlobals.getCachedConfiguration();

			ProxyStatusItem enabled = conf.status.getProperty(ProxyStatusProperties.PROXY_ENABLED);
			ProxyStatusItem hostname = conf.status.getProperty(ProxyStatusProperties.PROXY_VALID_HOSTNAME);
			ProxyStatusItem port = conf.status.getProperty(ProxyStatusProperties.PROXY_VALID_PORT);
			ProxyStatusItem ping = conf.status.getProperty(ProxyStatusProperties.PROXY_REACHABLE);
			ProxyStatusItem web = conf.status.getProperty(ProxyStatusProperties.WEB_REACHABLE);

			if (enabled.status == CheckStatusValues.CHECKED)
			{
				if (enabled.result == true)
					proxyEnabledPref.SetStatus(ValidationStatus.VALID);
				else
					proxyEnabledPref.SetStatus(ValidationStatus.ERROR);
			}
			else
			{
				proxyEnabledPref.SetStatus(ValidationStatus.CHECKING);
			}

			if (hostname.status == CheckStatusValues.CHECKED)
			{

			}
			else
			{
				proxyValidHostPref.SetStatus(ValidationStatus.CHECKING);
			}

		}

	}

}
