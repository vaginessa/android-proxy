package com.lechucksoftware.proxy.proxysettings.preferences;

import java.util.ArrayList;
import java.util.Collections;

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

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.MainAPPrefsFragment;
import com.lechucksoftware.proxy.proxysettings.utils.UrlManager;
import com.lechucksoftware.proxy.proxysettings.utils.Utils;
import com.shouldit.proxy.lib.AccessPoint;
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
		
		
		final ArrayList<ProxyConfiguration> confsList = (ArrayList<ProxyConfiguration>) ApplicationGlobals.getConfigurationsList();
		Collections.sort(confsList);
				
		listview.setAdapter(new ListAdapter(ApSelectorDialogPreference.this.getContext(), R.id.list_view, confsList));
		listview.setOnItemClickListener(new OnItemClickListener()
		{
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		    {		    	
		    	MainAPPrefsFragment.instance.selectAP(confsList.get(position));
		    	ApSelectorDialogPreference.this.getDialog().dismiss();
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
			        ImageView signal = (ImageView) view.findViewById(R.id.list_item_ap_icon);
			        
			        if (listItem.ap.mRssi == Integer.MAX_VALUE) 
			        {
			            signal.setImageDrawable(null);
			        } 
			        else 
			        {
			            signal.setImageLevel(listItem.ap.getLevel());
			            signal.setImageResource(R.drawable.wifi_signal);
			            signal.setImageState((listItem.ap.security != AccessPoint.SECURITY_NONE) ? AccessPoint.STATE_SECURED : AccessPoint.STATE_NONE, true);
			        }
					
					
					((TextView) view.findViewById(R.id.list_item_ap_name)).setText(Utils.cleanUpSSID(listItem.getSSID()));
					((TextView) view.findViewById(R.id.list_item_ap_description)).setText(listItem.toShortString());
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
