package com.lechucksoftware.proxy.proxysettings.activities;

import com.lechucksoftware.proxy.proxysettings.Constants.ProxyCheckStatus;
import com.lechucksoftware.proxy.proxysettings.Globals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ValidationPreference;
import com.lechucksoftware.proxy.proxysettings.ValidationPreference.ValidationStatus;
import com.lechucksoftware.proxy.proxysettings.activities.help.HelpFragmentActivity;
import com.lechucksoftware.proxy.proxysettings.services.DownloadService;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.ProxyUtils;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Proxy;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class ProxyPreferencesActivity extends PreferenceActivity
{
	public static ProxyPreferencesActivity instance;
	
	public static String TAG = "ProxyPreferencesActivity";
	static final int SELECT_PROXY_REQUEST = 0;
	
	
//	static final int URL_DOWNLOADER_DIALOG = 0;
//	static final int URL_BROWSER_DIALOG = 1;

	SharedPreferences sharedPref;

	CheckBoxPreference notificationEnabled;
	CheckBoxPreference authenticationEnabled;
	EditTextPreference userPref;
	EditTextPreference passwordPref;
//	Preference proxyHostPortPref;
	DialogPreference proxySelector;
	Preference proxyTestPref;
	PreferenceScreen proxyAuthentication;

	ValidationPreference proxyEnabledPref;
	ValidationPreference proxyAddressPref;
	ValidationPreference proxyReachablePref;
	ValidationPreference proxyWebReachablePref;
	
//	Preference webBrowserUrlOpener;
//	Preference urlDownloader;
	
	Preference helpPref;
	Preference aboutPref;
	
	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;

	// static Preference appsFeedbackPref;
	
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

		instance = this;
		
		addPreferencesFromResource(R.xml.preferences);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		loadUIComponents();
		setListenersToUI();

		refreshUIComponents();
	}
	
//	@Override
//	@Deprecated
//	protected Dialog onCreateDialog(int id)
//	{
//		switch (id)
//		{
//			case URL_DOWNLOADER_DIALOG:
//				UrlDownloaderDialog.newInstance(this).show();
//				break;
//			case URL_BROWSER_DIALOG:
//				UrlBrowserDialog.newInstance(this).show();
//				break;
//		}
//		return super.onCreateDialog(id);
//	}

	/**
	 * 
	 */
	public void loadUIComponents()
	{
		notificationEnabled = (CheckBoxPreference) findPreference("preference_notification_enabled");
		
		proxyAuthentication = (PreferenceScreen) findPreference("pref_key_proxy_settings_authentication_screen");
		authenticationEnabled = (CheckBoxPreference) findPreference("preference_authentication_enabled");
		userPref = (EditTextPreference) findPreference("preference_authentication_user");
		passwordPref = (EditTextPreference) findPreference("preference_authentication_password");
		
		getPreferenceScreen().removePreference(proxyAuthentication); // Disable authentication for now
		
//		proxyHostPortPref = findPreference("preference_proxy_host_port");
		proxySelector = (DialogPreference) findPreference("preference_proxy_selector");
		proxyEnabledPref = (ValidationPreference) findPreference("validation_proxy_enabled");
		proxyAddressPref = (ValidationPreference) findPreference("validation_proxy_valid_address");
		proxyReachablePref = (ValidationPreference) findPreference("validation_proxy_reachable");
		proxyWebReachablePref = (ValidationPreference) findPreference("validation_web_reachable");

		proxyTestPref = findPreference("preference_test_proxy_configuration");
		
//		webBrowserUrlOpener = findPreference("preference_test_proxy_webview");
//		urlDownloader = findPreference("preference_test_proxy_urlretriever");
		
		helpPref = findPreference("preference_help");
		aboutPref = findPreference("preference_about");
		
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(ProxyPreferencesActivity.this);
		mProgressDialog.setMessage(getResources().getText(R.string.preference_test_proxy_urlretriever_dialog_status));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		// appsFeedbackPref =
		// findPreference("preference_applications_feedback");
	}

	private BroadcastReceiver changeStatusReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals("com.lechucksoftware.proxy.proxysettings.UPDATE_PROXY"))
			{
				Log.d(TAG, "Received broadcast for Updated proxy configuration");
				refreshUIComponents();
			}
		}
	};
	
	public void setListenersToUI()
	{
		notificationEnabled.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				// FIX: call only the refresh of the notification!
				Log.d(TAG, "Sending broadcast intent UPDATE_NOTIFICATION");
				Intent intent = new Intent("com.lechucksoftware.proxy.proxysettings.UPDATE_NOTIFICATION");
				sendBroadcast(intent);
				return checkNotificationPref(newValue);
			}
		});

		authenticationEnabled.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				return checkAuthenticationPref(newValue);
			}
		});

//		proxyHostPortPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
//		{
//			public boolean onPreferenceClick(Preference preference)
//			{
//				Intent proxyIntent = ProxyUtils.getProxyIntent();
//				Log.d(TAG, "Starting Android's ProxySelector Activity and waiting for result");
//				startActivityForResult(proxyIntent, SELECT_PROXY_REQUEST);
//				return true;
//			}
//		});

		proxyTestPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				proxyTestPref.setEnabled(false);
				// TODO: Change summary status to request time
				sendBroadcast(new Intent(Proxy.PROXY_CHANGE_ACTION));
				return true;
			}
		});

		// appsFeedbackPref.setOnPreferenceClickListener(new
		// OnPreferenceClickListener()
		// {
		// public boolean onPreferenceClick(Preference preference)
		// {
		// Intent feedbackIntent = new Intent(getApplicationContext(),
		// ApplicationsFeedbacksActivity.class);
		// startActivity(feedbackIntent);
		// return true;
		// }
		// });

		userPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				return checkUsernamePref(newValue);
			}
		});

		passwordPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{

			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				return checkPasswordPref(newValue);
			}
		});
		
//		webBrowserUrlOpener.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			
//			public boolean onPreferenceClick(Preference preference)
//			{
//				showDialog(URL_BROWSER_DIALOG);
//				return false;
//			}
//		});
		
		
//		urlDownloader.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			
//			@TargetApi(9)
//			public boolean onPreferenceClick(Preference preference)
//			{
//				showDialog(URL_DOWNLOADER_DIALOG);
//				return false;
//			}
//		});
		
		helpPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference)
			{
				startActivity(new Intent(getApplicationContext(), HelpFragmentActivity.class));
				return true;
			}
		});
		
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference)
			{
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	public boolean checkUsernamePref(Object newValue)
	{
		Log.d(TAG, "checkUsernamePref : " + (String) newValue);

		userPref.setSummary((String) newValue);
		return true;
	}

	public boolean checkNotificationPref(Object newValue)
	{
		Log.d(TAG, "checkNotificationPref : " + (Boolean) newValue);

		if ((Boolean) newValue)
		{
			notificationEnabled.setSummary(getApplicationContext().getText(R.string.preferences_statusbar_notification_description_enabled));
		}
		else
		{
			notificationEnabled.setSummary(getApplicationContext().getText(R.string.preferences_statusbar_notification_description_disabled));
		}
		return true;
	}

	public boolean checkAuthenticationPref(Object newValue)
	{
		Log.d(TAG, "checkAuthenticationPref : " + (Boolean) newValue);

		if ((Boolean) newValue)
		{
			userPref.setEnabled(true);
			passwordPref.setEnabled(true);
			authenticationEnabled.setSummary(getApplicationContext().getText(R.string.preference_proxy_authentication_summary_enabled));
		}
		else
		{
			userPref.setEnabled(false);
			passwordPref.setEnabled(false);
			authenticationEnabled.setSummary(getApplicationContext().getText(R.string.preference_proxy_authentication_summary_disabled));
		}

		return true;
	}

	public boolean checkPasswordPref(Object newValue)
	{
		Log.d(TAG, "checkPasswordPref : " + (String) newValue);

		String pwd = newValue.toString();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < pwd.length(); i++)
		{
			sb.append('*');
		}

		passwordPref.setSummary(sb.toString());
		return true;
	}

	private void refreshUIComponents()
	{
		checkNotificationPref(sharedPref.getBoolean("preference_notification_enabled", false));
		checkAuthenticationPref(sharedPref.getBoolean("preference_authentication_enabled", false));
		checkUsernamePref(sharedPref.getString("preference_authentication_user", ""));
		checkPasswordPref(sharedPref.getString("preference_authentication_password", ""));
		
//		proxyHostPortPref.setSummary(UIUtils.GetStatusSummary(getApplicationContext()));
		proxySelector.setSummary(UIUtils.GetStatusSummary(getApplicationContext()));

		if (Globals.getInstance().proxyCheckStatus == ProxyCheckStatus.CHECKING)
		{
			// Checking
			proxyTestPref.setEnabled(false);

			proxyEnabledPref.SetStatus(ValidationStatus.Checking);
			proxyEnabledPref.setSummary(getResources().getString(R.string.validation_proxy_summary_checking));

			proxyAddressPref.SetStatus(ValidationStatus.Checking);
			proxyAddressPref.setSummary(getResources().getString(R.string.validation_proxy_summary_checking));

			proxyReachablePref.SetStatus(ValidationStatus.Checking);
			proxyReachablePref.setSummary(getResources().getString(R.string.validation_proxy_summary_checking));

			proxyWebReachablePref.SetStatus(ValidationStatus.Checking);
			proxyWebReachablePref.setSummary(getResources().getString(R.string.validation_proxy_summary_checking));
		}
		else
		{
			// Checked
			proxyTestPref.setEnabled(true);

			if (Globals.getInstance().proxyConf.status.getEnabled())
			{
				proxyEnabledPref.SetStatus(ValidationStatus.Valid);
				proxyEnabledPref.setSummary(getResources().getString(R.string.validation_proxy_enabled_summary_ok));
			}
			else
			{
				proxyEnabledPref.SetStatus(ValidationStatus.Error);
				proxyEnabledPref.setSummary(getResources().getString(R.string.validation_proxy_enabled_summary_nok));
			}

			if (Globals.getInstance().proxyConf.status.getValid_address())
			{
				proxyAddressPref.SetStatus(ValidationStatus.Valid);
				proxyAddressPref.setSummary(getResources().getString(R.string.validation_proxy_address_summary_ok));
			}
			else
			{
				proxyAddressPref.SetStatus(ValidationStatus.Error);
				proxyAddressPref.setSummary(getResources().getString(R.string.validation_proxy_address_summary_nok));
			}

			if (Globals.getInstance().proxyConf.status.getProxy_reachable())
			{
				proxyReachablePref.SetStatus(ValidationStatus.Valid);
				proxyReachablePref.setSummary(getResources().getString(R.string.validation_proxy_reachable_summary_ok));
			}
			else
			{
				proxyReachablePref.SetStatus(ValidationStatus.Error);
				proxyReachablePref.setSummary(getResources().getString(R.string.validation_proxy_reachable_summary_nok));
			}

			if (Globals.getInstance().proxyConf.status.getWeb_reachable())
			{
				proxyWebReachablePref.SetStatus(ValidationStatus.Valid);
				proxyWebReachablePref.setSummary(getResources().getString(R.string.validation_proxy_web_reachable_summary_ok));
			}
			else
			{
				proxyWebReachablePref.SetStatus(ValidationStatus.Error);
				proxyWebReachablePref.setSummary(getResources().getString(R.string.validation_proxy_web_reachable_summary_nok));
			}
		}
	}

	// private void openFeedbacks()
	// {
	// Intent test = new Intent(getApplicationContext(),
	// ApplicationsFeedbacksActivity.class);
	// startActivity(test);
	// }

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data)
//	{
//		if (requestCode == SELECT_PROXY_REQUEST)
//		{
//			refreshUIComponents();
//		}
//	}

	@Override
	protected void onStart()
	{
		super.onStart();
		Log.d(TAG, "Start");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		IntentFilter ifilt = new IntentFilter("com.lechucksoftware.proxy.proxysettings.UPDATE_PROXY");
		registerReceiver(changeStatusReceiver, ifilt); // Start register the
													   // status receiver
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(changeStatusReceiver); // Stop the registerd status
												  // receiver
	}

	@Override
	protected void onStop()
	{
		Log.d(TAG, "Stop");
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
