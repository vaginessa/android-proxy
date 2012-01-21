/**
 * 
 */
package com.lechucksoftware.proxy.lib;

import org.apache.http.HttpHost;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author marco
 * 
 */
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

			HttpHost currentProxy = ProxySettings
					.getProxyConfiguration(getApplicationContext());
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
					.setMessage(
							"Proxy: " + currentProxy.getHostName() + ":"
									+ currentProxy.getPort());

			AlertDialog alert = builder.create();
			dialog = alert;
			break;

		default:
			dialog = null;
		}

		return dialog;
	}
}