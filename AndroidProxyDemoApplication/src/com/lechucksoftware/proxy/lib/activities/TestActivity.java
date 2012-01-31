/**
 * 
 */
package com.lechucksoftware.proxy.lib.activities;

import java.util.ArrayList;

import com.lechucksoftware.proxy.lib.ProxyConfiguration;
import com.lechucksoftware.proxy.lib.ProxySettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TestActivity extends Activity
{
	public static final String TAG = "TestActivity";
	static final int DIALOG_ID_PROXY = 0;

	private ListView listview;
	private ArrayList mListItem;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listview = (ListView) findViewById(R.id.list_view);
		mListItem = (ArrayList) ProxySettings.getProxiesConfigurations(getApplicationContext());
		listview.setAdapter(new ListAdapter(TestActivity.this, R.id.list_view,
				mListItem));

		// showDialog(DIALOG_ID_PROXY);

	}

	private class ListAdapter extends ArrayAdapter
	{ // --CloneChangeRequired
		private ArrayList mList; // --CloneChangeRequired
		private Context mContext;

		public ListAdapter(Context context, int textViewResourceId,
				ArrayList list) { // --CloneChangeRequired
			super(context, textViewResourceId, list);
			this.mList = list;
			this.mContext = context;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			try {
				if (view == null) {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.list_item, null); // --CloneChangeRequired(list_item)
				}

				final ProxyConfiguration listItem = (ProxyConfiguration) mList.get(position); // --CloneChangeRequired
				
				if (listItem != null) {
					// setting list_item views
					((TextView) view.findViewById(R.id.tv_name))
							.setText(listItem.wifiConfiguration.SSID);
				}
				
			} catch (Exception e) {
				Log.i(TestActivity.ListAdapter.class.toString(), e.getMessage());
			}
			return view;
		}
	}

	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog;
		switch (id) {
		case DIALOG_ID_PROXY:

			ProxyConfiguration currentProxy = ProxySettings
					.getCurrentProxyConfiguration(getApplicationContext());

			String msg = null;
			if (currentProxy != null)
				msg = "Current Proxy: " + currentProxy.toString();
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
							}).setMessage(msg);

			AlertDialog alert = builder.create();
			dialog = alert;
			break;

		default:
			dialog = null;
		}

		return dialog;
	}
}