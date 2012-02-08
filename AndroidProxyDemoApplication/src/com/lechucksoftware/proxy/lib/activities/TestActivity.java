/**
 * 
 */
package com.lechucksoftware.proxy.lib.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lechucksoftware.proxy.lib.ProxyConfiguration;
import com.lechucksoftware.proxy.lib.ProxySettings;

public class TestActivity extends Activity
{
	public static final String TAG = "TestActivity";
	static final int DIALOG_ID_PROXY = 0;

	private ListView listview;
	private ArrayList<ProxyConfiguration> mListItem;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listview = (ListView) findViewById(R.id.list_view);
		mListItem = (ArrayList<ProxyConfiguration>) ProxySettings.getProxiesConfigurations(getApplicationContext());
		listview.setAdapter(new ListAdapter(TestActivity.this, R.id.list_view,
				mListItem));
	}

	private class ListAdapter extends ArrayAdapter<ProxyConfiguration>
	{ 
		private ArrayList<ProxyConfiguration> mList; // --CloneChangeRequired
		private Context mContext;

		public ListAdapter(Context context, int textViewResourceId,	ArrayList<ProxyConfiguration>  list) 
		{ 
			super(context, textViewResourceId, list);
			this.mList = list;
			this.mContext = context;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			try 
			{
				if (view == null) 
				{
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.list_item, null); 
				}

				final ProxyConfiguration listItem = (ProxyConfiguration) mList.get(position);
				
				if (listItem != null) 
				{
					((TextView) view.findViewById(R.id.list_item_ap_name)).setText(listItem.wifiConfiguration.SSID);
					
					if (listItem.proxy != null)
					{
						((TextView) view.findViewById(R.id.list_item_ap_description)).setText(listItem.proxy.toHostString());
					}
				}
				
			} 
			catch (Exception e) 
			{
				Log.i(TestActivity.ListAdapter.class.toString(), e.getMessage());
			}
			return view;
		}
	}
}