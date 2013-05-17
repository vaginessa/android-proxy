package com.lechucksoftware.proxy.proxysettings.preferences;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.ProxyDetailsFragment;
import com.lechucksoftware.proxy.proxysettings.utils.ProxySelectorListAdapter;
import com.shouldit.proxy.lib.ProxyConfiguration;

public class ApSelectorDialogPreference extends DialogPreference
{
	AutoCompleteTextView input = null;
	private ListView listview;
	
	public ApSelectorDialogPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		setPersistent(false);
		setDialogLayoutResource(R.layout.ap_selector_dialog);	
		setDialogTitle(context.getResources().getString(R.string.ap_selection_title));
	}

	@Override
	protected View onCreateDialogView()
	{
		View root  = super.onCreateDialogView();
		
//		Debug.startMethodTracing();
		
		listview = (ListView) root.findViewById(R.id.ap_selector_listview);
		
		
		final ArrayList<ProxyConfiguration> confsList = (ArrayList<ProxyConfiguration>) ApplicationGlobals.getConfigurationsList();
		Collections.sort(confsList);
				
		listview.setAdapter(new ProxySelectorListAdapter(ApSelectorDialogPreference.this.getContext(), R.id.list_view, confsList));
		listview.setOnItemClickListener(new OnItemClickListener()
		{
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		    {		    	
		    	ProxyDetailsFragment.instance.selectAP(confsList.get(position));
		    	ApSelectorDialogPreference.this.getDialog().dismiss();
		    }
		});
		
//		Debug.stopMethodTracing();
		
		return root;
	}	
}
