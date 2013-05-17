package com.lechucksoftware.proxy.proxysettings.utils;

import java.util.ArrayList;

import android.content.Context;
import android.os.Debug;
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
        notifyDataSetChanged();
	}

	static class ApViewHolder
	{
		ImageView signal;
		TextView ssid;
		TextView status;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ApViewHolder viewHolder;
		View view = convertView;

		if (view == null)
		{
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.ap_list_item, null);

			viewHolder = new ApViewHolder();
			viewHolder.signal = (ImageView) view.findViewById(R.id.list_item_ap_icon);
			viewHolder.ssid = (TextView) view.findViewById(R.id.list_item_ap_name);
			viewHolder.status = (TextView) view.findViewById(R.id.list_item_ap_status);

			view.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ApViewHolder) view.getTag();
		}

		final ProxyConfiguration listItem = (ProxyConfiguration) mList.get(position);

		if (listItem != null)
		{
			if (listItem.ap.mRssi == Integer.MAX_VALUE)
			{
				viewHolder.signal.setImageDrawable(null);
			}
			else
			{
				viewHolder.signal.setImageLevel(listItem.ap.getLevel());
				viewHolder.signal.setImageResource(R.drawable.wifi_signal);
				viewHolder.signal.setImageState((listItem.ap.security != AccessPoint.SECURITY_NONE) ? AccessPoint.STATE_SECURED : AccessPoint.STATE_NONE, true);
			}

			viewHolder.ssid.setText(ProxyUtils.cleanUpSSID(listItem.getSSID()));
			
//			viewHolder.status.setText(String.format("%s - %s", listItem.toShortString(), listItem.getAPStatus()));
			viewHolder.status.setText(listItem.getAPStatus());
		}
		return view;
	}
}
