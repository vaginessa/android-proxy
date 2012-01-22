/**
 * 
 */
package com.lechucksoftware.proxy.lib.activities;

import org.apache.http.HttpHost;

import com.lechucksoftware.proxy.lib.ProxySettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


public class TestActivity extends Activity
{
	public static final String TAG = "TestActivity";
	static final int DIALOG_ID_PROXY = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		showDialog(DIALOG_ID_PROXY);
	}

	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog;
		switch (id) {
		case DIALOG_ID_PROXY:

			HttpHost currentProxy = ProxySettings.getProxiesConfigurations(getApplicationContext()).get(0);
			String msg = null;
			if (currentProxy != null)
				msg = "Proxy: " + currentProxy.getHostName() + ":" + currentProxy.getPort();
			else
				msg = "Proxy not set";
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Proxy Info:")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt)
								{
									finish();
								}
							})
					.setMessage(msg);

			AlertDialog alert = builder.create();
			dialog = alert;
			break;

		default:
			dialog = null;
		}

		return dialog;
	}
}