package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.AdvancedPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.HelpPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.MainAPPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyCheckerPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyStatusItem;

public class ProxyPreferencesActivity extends Activity
{
	public static final String TAG = "ProxyPreferencesActivity";

	public static ProxyPreferencesActivity instance;

	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;

	private MainAPPrefsFragment mainFragment;
	private HelpPrefsFragment helpFragment;
	private ProxyCheckerPrefsFragment checkFragment;
	private AdvancedPrefsFragment advFragment;

	private MenuItem menuItemWifiStatus;
	private MenuItem menuItemWifiToggle;
	private MenuItem menuItemProxyStatus;
	private MenuItem menuItemProxyEnabled;

	private MenuItem menuItemProxyStatusDetail;

	public void showProgressDialog()
	{
		if (mProgressDialog != null)
			mProgressDialog.show();
	}

	public void dismissProgressDialog()
	{
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	public void setProgressDialogMessage(String message)
	{
		if (mProgressDialog != null)
			mProgressDialog.setMessage(message);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mainFragment = new MainAPPrefsFragment();
		checkFragment = new ProxyCheckerPrefsFragment();
		advFragment = new AdvancedPrefsFragment();
		helpFragment = new HelpPrefsFragment();

		getFragmentManager().beginTransaction().replace(android.R.id.content, mainFragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.proxy_prefs_activity, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		dismissProgressDialog();
		
		//int a = 12/0; // TEST ACRA

		// Proxy checker section
		ProxyConfiguration pconf = ApplicationGlobals.getCachedConfiguration();
		menuItemProxyStatus = menu.findItem(R.id.menu_proxy_status);
		menuItemProxyStatusDetail = menu.findItem(R.id.menu_proxy_status_detail);
//		menuItemProxyEnabled = menu.findItem(R.id.menu_proxy_enabled);
		
		if (pconf.status.getCheckingStatus() == CheckStatusValues.CHECKED)
		{
		    ProxyStatusItem mostRelevantError =	pconf.status.getMostRelevantErrorProxyStatusItem();
			if (mostRelevantError == null)
			{
				menuItemProxyStatus.setIcon(R.drawable.ic_action_valid);
			}
			else
			{
				menuItemProxyStatus.setIcon(UIUtils.writeOnDrawable(ApplicationGlobals.getInstance().getApplicationContext(), R.drawable.ic_action_notvalid, pconf.status.getErrorCount().toString()));
			}	
		}
		else
			menuItemProxyStatus.setActionView(R.layout.actionbar_refresh_progress);

		
		menuItemWifiStatus = menu.findItem(R.id.menu_wifi_status);
		menuItemWifiToggle = menu.findItem(R.id.menu_wifi_toggle);

		// Wi-Fi section
		boolean wifiEnabled = ApplicationGlobals.getWifiManager().isWifiEnabled();
		if (wifiEnabled)
		{
			menuItemWifiToggle.setTitle(getResources().getString(R.string.wifi_toggle_off_summary));
			Drawable icon;
			if (pconf.ap.security == 0) 
				icon = getResources().getDrawable(R.drawable.wifi_signal_open);
			else
				icon = getResources().getDrawable(R.drawable.wifi_signal_lock);
			
			icon.setLevel(pconf.ap.getLevel());
			menuItemWifiStatus.setIcon(icon);
			menuItemWifiStatus.setTitle(pconf.ap.ssid);
		}
		else
		{
			menuItemWifiToggle.setTitle(getResources().getString(R.string.wifi_toggle_on_summary));
			menuItemWifiStatus.setIcon(getResources().getDrawable(R.drawable.ic_action_nowifi));
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getFragmentManager().beginTransaction().replace(android.R.id.content, mainFragment).commit();
				return true;

			case R.id.menu_proxy_status:
				return true;
//			case R.id.menu_proxy_enabled:
//			case R.id.menu_proxy_host:
//			case R.id.menu_proxy_port:
//			case R.id.menu_proxy_reachable:
//			case R.id.menu_proxy_web_reach:
			case R.id.menu_proxy_status_detail:
				getFragmentManager().beginTransaction().replace(android.R.id.content, checkFragment).commit();
				return true;

			case R.id.menu_wifi_settings:
				startActivity(new Intent("android.settings.WIFI_SETTINGS"));
				return true;

			case R.id.menu_wifi_toggle:
				boolean wifiStatus = ApplicationGlobals.getWifiManager().isWifiEnabled();
				ApplicationGlobals.getWifiManager().setWifiEnabled(!wifiStatus);
				item.setEnabled(false);
				menuItemWifiStatus.setActionView(R.layout.actionbar_refresh_progress);
				mainFragment.refreshUIComponents();
				return true;

			case R.id.menu_about:
				getFragmentManager().beginTransaction().replace(android.R.id.content, helpFragment).commit();
				return true;
			case R.id.menu_advanced:
				getFragmentManager().beginTransaction().replace(android.R.id.content, advFragment).commit();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals(APLConstants.APL_UPDATED_PROXY_CONFIGURATION))
			{
				LogWrapper.d(TAG, "Received broadcast for updated proxy configuration");
				refreshUI();
			}
			else if (action.equals(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK))
			{
				LogWrapper.d(TAG, "Received broadcast for partial update to proxy configuration");
				refreshUI();
			}
		}
	};

	private void refreshUI()
	{
		if(mainFragment != null 
			&&  mainFragment.selectedConfiguration != null 
			&&  mainFragment.selectedConfiguration.status.getCheckingStatus() == CheckStatusValues.CHECKING)
		{
			refreshingProxyStatus();
		}
		else
		{
			this.invalidateOptionsMenu();
		}
		
		mainFragment.selectAP();
		mainFragment.refreshUIComponents();
		checkFragment.refreshUIComponents();
	}

	private void refreshingProxyStatus()
	{
		menuItemProxyStatus.setActionView(R.layout.actionbar_refresh_progress);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// Start register the status receivers
		IntentFilter ifilt = new IntentFilter();
		ifilt.addAction(APLConstants.APL_UPDATED_PROXY_CONFIGURATION);
		ifilt.addAction(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);
		registerReceiver(changeStatusReceiver, ifilt);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// Stop the registered status receivers
		unregisterReceiver(changeStatusReceiver);
	}
}
