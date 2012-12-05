package com.lechucksoftware.proxy.proxysettings.activities.help;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.activities.ProxySettingsCallerActivity;

public class DisclaimerEndFragment extends Fragment
{
	public static final String TAG = "DisclaimerEndFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.disclaimer_end, container, false);
		
		Button accept =  (Button) view.findViewById(R.id.disclaimer_accept_button);
		Button cancel = (Button) view.findViewById(R.id.disclaimer_cancel_button);
		
		cancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				getActivity().finish();				
			}
		});
		
		accept.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFERENCES_FILENAME, 0);
	        	Editor editor = settings.edit(); 
	            editor.putBoolean(Constants.PREFERENCES_ACCEPTED_DISCLAIMER, true);
	            editor.commit();
	            
	            Intent i = new Intent(getActivity().getApplicationContext(), ProxySettingsCallerActivity.class);
	            Log.d(TAG,"Starting ProxySettingsCallerActivity activity");
	            startActivity(i);
	            getActivity().finish();
			}
		});
		
		return view;
	}

	public static Fragment newInstance(Context _context)
	{
		return new DisclaimerEndFragment();
	}
}
