package com.lechucksoftware.proxy.proxysettings.utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechucksoftware.proxy.proxysettings.R;
import com.shouldit.proxy.lib.AccessPoint;
import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxyUtils;

public class ProxySelectorListAdapter extends ArrayAdapter<ProxyConfiguration>
{
	private ArrayList<ProxyConfiguration> mList; // --CloneChangeRequired
	private Context ctx;

	public ProxySelectorListAdapter(Context context, int textViewResourceId, ArrayList<ProxyConfiguration> list)
	{
		super(context, textViewResourceId, list);
		ctx = context;
		this.mList = list;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		try
		{
			if (view == null)
			{
				LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				
				
				((TextView) view.findViewById(R.id.list_item_ap_name)).setText(ProxyUtils.cleanUpSSID(listItem.getSSID()));
				((TextView) view.findViewById(R.id.list_item_ap_description)).setText(listItem.ap.getSecurityString(ctx, false));
				((TextView) view.findViewById(R.id.list_item_ap_proxy_description)).setText(listItem.toShortString());
			}
		}
		catch (Exception e)
		{
//			LogWrapper.i(TAG, e.getMessage());
		}
		return view;
	}
}