package com.lechucksoftware.proxy.proxysettings.activities;

import java.net.URI;
import java.util.ArrayList;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PInfo;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PackagesUtils;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;
import com.shouldit.proxy.lib.ProxyUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ApplicationsFeedbacksActivity extends Activity
{
	public static final String TAG = "ApplicationsFeedbacksActivity";
	static final int DIALOG_ID_PROXY = 0;

	private ListView listview;
	private ArrayList<PInfo> mListItem;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applications_list);

		listview = (ListView) findViewById(R.id.list_view);

		LoadInstalledPackagesTask task = new LoadInstalledPackagesTask();
		task.execute();
	}

	private class LoadInstalledPackagesTask extends AsyncTask<Void, Void, ArrayList<PInfo>>
	{
		private final ProgressDialog dialog = new ProgressDialog(ApplicationsFeedbacksActivity.this);

		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage(getApplicationContext().getResources().getString(R.string.application_list_loading_dialog_description));
			this.dialog.show();
		}

		@Override
		protected ArrayList<PInfo> doInBackground(Void... paramArrayOfParams)
		{
			mListItem = (ArrayList<PInfo>) PackagesUtils.getPackages(getApplicationContext());
			return mListItem;
		}

		@Override
		protected void onPostExecute(ArrayList<PInfo> result)
		{
			if (this.dialog.isShowing())
				this.dialog.dismiss();
			
			listview.setAdapter(new ListAdapter(ApplicationsFeedbacksActivity.this, R.id.list_view, result));
		}
	}

	private class ListAdapter extends ArrayAdapter<PInfo>
	{
		private ArrayList<PInfo> mList; // --CloneChangeRequired

		public ListAdapter(Context context, int textViewResourceId, ArrayList<PInfo> list)
		{
			super(context, textViewResourceId, list);
			this.mList = list;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			try
			{
				if (view == null)
				{
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.application_list_item, null);
				}

				final PInfo listItem = (PInfo) mList.get(position);

				if (listItem != null)
				{
					((ImageView) view.findViewById(R.id.list_item_app_icon)).setImageDrawable(listItem.icon);
					((TextView) view.findViewById(R.id.list_item_app_name)).setText(listItem.appname);
					((TextView) view.findViewById(R.id.list_item_app_description)).setText(listItem.pname);
					((TextView) view.findViewById(R.id.list_item_app_version)).setText(listItem.versionName);
				}

			}
			catch (Exception e)
			{
				Log.i(TAG, e.getMessage());
			}
			return view;
		}
	}
}