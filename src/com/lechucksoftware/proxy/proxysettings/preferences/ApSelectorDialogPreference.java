package com.lechucksoftware.proxy.proxysettings.preferences;

import java.util.ArrayList;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.utils.UrlManager;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;

public class ApSelectorDialogPreference extends DialogPreference
{
	AutoCompleteTextView input = null;
	private ListView listview;
	private ArrayList<ProxyConfiguration> mListItem;

	
	public ApSelectorDialogPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setPersistent(false);
		setDialogLayoutResource(R.layout.ap_selector_dialog);	
		setDialogTitle(context.getResources().getString(R.string.preference_test_proxy_urlbrowser_dialog_title));
	}

	@Override
	protected View onCreateDialogView()
	{
		View root  = super.onCreateDialogView();
		
		listview = (ListView) root.findViewById(R.id.ap_selector_listview);
		
		ArrayList<ProxyConfiguration> confs = (ArrayList<ProxyConfiguration>) ProxySettings.getProxiesConfigurations(getContext());
		
		listview.setAdapter(new ListAdapter(ApSelectorDialogPreference.this.getContext(), R.id.list_view, confs));
		listview.setOnItemClickListener(new OnItemClickListener()
		{
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		    {
//		    	showDialog(mListItem.get(position));
		    }
		});
		
		return root;
	}
	
	private class ListAdapter extends ArrayAdapter<ProxyConfiguration>
	{
		private ArrayList<ProxyConfiguration> mList; // --CloneChangeRequired

		public ListAdapter(Context context, int textViewResourceId, ArrayList<ProxyConfiguration> list)
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
					LayoutInflater vi = (LayoutInflater) ApSelectorDialogPreference.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.ap_list_item, null);
				}

				final ProxyConfiguration listItem = (ProxyConfiguration) mList.get(position);

				if (listItem != null)
				{
//					((ImageView) view.findViewById(R.id.list_item_ap_icon)).setImageDrawable(listItem.);
					((TextView) view.findViewById(R.id.list_item_ap_name)).setText(listItem.wifiConfiguration.SSID);
					((TextView) view.findViewById(R.id.list_item_ap_description)).setText(listItem.wifiConfiguration.toString());
				}
			}
			catch (Exception e)
			{
//				LogWrapper.i(TAG, e.getMessage());
			}
			return view;
		}
	}

}
