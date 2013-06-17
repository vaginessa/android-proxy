package com.lechucksoftware.proxy.proxysettings.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.*;
import com.lechucksoftware.proxy.proxysettings.services.ViewServer;
import com.shouldit.android.utils.lib.log.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.APLConstants;
import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;
import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyStatusItem;

public class ProxyPreferencesActivity extends Activity
{
	public static final String TAG = "ProxyPreferencesActivity";
	public static final String CALLING_EXTRA = "com.lechucksoftware.proxy.proxysettings.activities.ProxyPreferencesActivity.CALLING_EXTRA";

	public static ProxyPreferencesActivity instance;

	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;

	private MainAPPrefsFragment mainFragment;
	private HelpPrefsFragment helpFragment;
	private ProxyCheckerPrefsFragment checkFragment;
	private AdvancedPrefsFragment advFragment;
    private APSelectorFragment apSelectorFragment;

	private MenuItem menuItemWifiStatus;
	private MenuItem menuItemWifiToggle;
	private MenuItem menuItemProxyStatus;
	private MenuItem menuItemProxyEnabled;

	private MenuItem menuItemProxyStatusDetail;
	private MenuItem menuItemWifiSettings;

//	public void showProgressDialog()
//	{
//		if (mProgressDialog != null)
//			mProgressDialog.show();
//	}
//
//	public void dismissProgressDialog()
//	{
//		if (mProgressDialog != null && mProgressDialog.isShowing())
//			mProgressDialog.dismiss();
//	}
//
//	public void setProgressDialogMessage(String message)
//	{
//		if (mProgressDialog != null)
//			mProgressDialog.setMessage(message);
//	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pref_container);

		ViewServer.get(this).addWindow(this);

		mainFragment = new MainAPPrefsFragment();
		checkFragment = new ProxyCheckerPrefsFragment();
		advFragment = new AdvancedPrefsFragment();
		helpFragment = new HelpPrefsFragment();
        apSelectorFragment = new APSelectorFragment();

		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null)
		{
			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null)
			{
				return;
			}

			// Add the fragment to the 'fragment_container' FrameLayout
			getFragmentManager().beginTransaction().add(R.id.fragment_container, ap).commit();
		}
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
//		dismissProgressDialog();

		ProxyConfiguration pconf = ApplicationGlobals.getCachedConfiguration();

		updateProxyAction(menu, pconf);
		updateWifiAction(menu, pconf);

		return true;
	}

	private void updateProxyAction(Menu menu, ProxyConfiguration pconf)
	{
		menuItemProxyStatus = menu.findItem(R.id.menu_proxy_status);
		menuItemProxyStatusDetail = menu.findItem(R.id.menu_proxy_status_detail);
		// menuItemProxyEnabled = menu.findItem(R.id.menu_proxy_enabled);

		if (pconf.status.getCheckingStatus() == CheckStatusValues.CHECKED)
		{
			ProxyStatusItem mostRelevantError = pconf.status.getMostRelevantErrorProxyStatusItem();
			if (mostRelevantError == null)
			{
				// No errors -> valid configuration
				menuItemProxyStatus.setIcon(R.drawable.ic_action_valid);
				menuItemProxyStatusDetail.setTitle(getResources().getString(R.string.validation_proxy_summary_ok));
			}
			else
			{
				if (pconf.status.getProperty(ProxyStatusProperties.WEB_REACHABLE).result)
				{
					// Errors, but internet is reachable
					menuItemProxyStatus.setIcon(UIUtils.writeWarningOnDrawable(ApplicationGlobals.getInstance().getApplicationContext(), R.drawable.ic_action_valid, pconf.status.getErrorCount().toString()));
					menuItemProxyStatusDetail.setTitle(getResources().getString(R.string.validation_proxy_summary_warning));
				}
				else
				{
					// Errors & internet is not reachable
					menuItemProxyStatus.setIcon(UIUtils.writeErrorOnDrawable(ApplicationGlobals.getInstance().getApplicationContext(), R.drawable.ic_action_notvalid, pconf.status.getErrorCount().toString()));
					menuItemProxyStatusDetail.setTitle(getResources().getString(R.string.validation_proxy_summary_errors));
				}
			}
		}
		else
		{
			menuItemProxyStatus.setActionView(R.layout.actionbar_refresh_progress);
			menuItemProxyStatusDetail.setTitle(getResources().getString(R.string.validation_proxy_summary_checking));
		}
	}

	private void updateWifiAction(Menu menu, ProxyConfiguration pconf)
	{
		menuItemWifiStatus = menu.findItem(R.id.menu_wifi_status);
		menuItemWifiToggle = menu.findItem(R.id.menu_wifi_toggle);
		menuItemWifiSettings = menu.findItem(R.id.menu_wifi_settings);

		// Wi-Fi Toggle
		boolean wifiEnabled = ApplicationGlobals.getWifiManager().isWifiEnabled();
		if (wifiEnabled)
			menuItemWifiToggle.setTitle(getResources().getString(R.string.wifi_toggle_off_summary));
		else
			menuItemWifiToggle.setTitle(getResources().getString(R.string.wifi_toggle_on_summary));

		// Wi-Fi Supplicant state
		SupplicantState ss = ApplicationGlobals.getWifiManager().getConnectionInfo().getSupplicantState();
		LogWrapper.d(TAG, "Supplicant state: " + ss.toString());

		if (wifiEnabled)
		{
			if (ss == SupplicantState.COMPLETED)
			{
				if (pconf.ap != null)
				{
					Drawable icon;

					if (pconf.ap.security == 0)
						icon = getResources().getDrawable(R.drawable.wifi_signal_open);
					else
						icon = getResources().getDrawable(R.drawable.wifi_signal_lock);

					icon.setLevel(pconf.ap.getLevel());
					menuItemWifiStatus.setIcon(icon);

					menuItemWifiSettings.setTitle(getResources().getString(R.string.connected_to, pconf.ap.ssid));
				}
				else
				{
					menuItemWifiStatus.setActionView(R.layout.actionbar_refresh_progress);
				}
			}
			else if (ss == SupplicantState.SCANNING) // Supplicant can remain
														// int SCANNING state
														// forever
			{
				menuItemWifiStatus.setIcon(getResources().getDrawable(R.drawable.ic_action_nowifi));
			}
			else
			{
				menuItemWifiStatus.setActionView(R.layout.actionbar_refresh_progress);
			}
		}
		else
		{
			menuItemWifiStatus.setIcon(getResources().getDrawable(R.drawable.ic_action_wifi_disabled));
		}

		// ss == SupplicantState.ASSOCIATED
		// ss == SupplicantState.ASSOCIATING
		// ss == SupplicantState.AUTHENTICATING
		// ss == SupplicantState.COMPLETED
		// ss == SupplicantState.DISCONNECTED
		// ss == SupplicantState.DORMANT
		// ss == SupplicantState.FOUR_WAY_HANDSHAKE
		// ss == SupplicantState.GROUP_HANDSHAKE
		// ss == SupplicantState.INACTIVE
		// ss == SupplicantState.FOUR_WAY_HANDSHAKE
		// ss == SupplicantState.INTERFACE_DISABLED
		// ss == SupplicantState.INVALID
		// ss == SupplicantState.SCANNING
		// ss == SupplicantState.UNINITIALIZED
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		FragmentTransaction transaction = null;

		switch (item.getItemId())
		{
			case android.R.id.home:
				// Clean-up the backstack when going back to home
				getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, mainFragment);
				//transaction.addToBackStack(null);
				transaction.commit();
				return true;

			case R.id.menu_proxy_status:
				return true;

			case R.id.menu_proxy_status_detail:
				transaction = getFragmentManager().beginTransaction();
				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack so the user can navigate back
				transaction.replace(R.id.fragment_container, checkFragment);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
				return true;

			case R.id.menu_wifi_settings:
				startActivity(new Intent("android.settings.WIFI_SETTINGS"));
				return true;

			case R.id.menu_wifi_toggle:
				boolean wifiStatus = ApplicationGlobals.getWifiManager().isWifiEnabled();
				ApplicationGlobals.getWifiManager().setWifiEnabled(!wifiStatus);
				item.setEnabled(false);
				menuItemWifiStatus.setActionView(R.layout.actionbar_refresh_progress);
				refreshUI();
				return true;

				// case R.id.menu_about:
				// getFragmentManager().beginTransaction().replace(android.R.id.content,
				// helpFragment).commit();
				// return true;
				// case R.id.menu_advanced:
				// getFragmentManager().beginTransaction().replace(android.R.id.content,
				// advFragment).commit();
				// return true;

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
				LogWrapper.d(TAG, "Received broadcast for updated proxy configuration - RefreshUI");
				refreshUI();
			}
			else if (action.equals(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK))
			{
				LogWrapper.d(TAG, "Received broadcast for partial update to proxy configuration - RefreshUI");
				refreshUI();
			}
			else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) || action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION) || action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) || action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
			{
				LogWrapper.logIntent(TAG, intent, Log.DEBUG, true);
				refreshUI();
			}
			else
			{
				LogWrapper.e(TAG, "Received intent not handled: " + intent.getAction());
			}
		}
	};

	private void refreshUI()
	{
		this.invalidateOptionsMenu();

		if (mainFragment.isVisible())
		{
			mainFragment.selectAP();
			mainFragment.refreshUIComponents();
		}

		if (checkFragment.isVisible())
		{
			checkFragment.refreshUIComponents();
		}
	}

	public void onDestroy()
	{
		super.onDestroy();
		ViewServer.get(this).removeWindow(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// Start register the status receivers
		IntentFilter ifilt = new IntentFilter();

		ifilt.addAction(APLConstants.APL_UPDATED_PROXY_CONFIGURATION);
		ifilt.addAction(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK);

		ifilt.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		ifilt.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		ifilt.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		ifilt.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//		ifilt.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

		// ifilt.addAction(Constants.PROXY_REFRESH_UI);
		registerReceiver(changeStatusReceiver, ifilt);

		ViewServer.get(this).setFocusedWindow(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// Stop the registered status receivers
		unregisterReceiver(changeStatusReceiver);
	}

	static boolean active = false;

	@Override
	public void onStart()
	{
		super.onStart();
		active = true;
	}

	@Override
	public void onStop()
	{
		super.onStop();
		active = false;
	}
}
