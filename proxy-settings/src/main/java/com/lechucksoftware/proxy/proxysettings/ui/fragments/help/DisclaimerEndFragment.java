package com.lechucksoftware.proxy.proxysettings.ui.fragments.help;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;

import timber.log.Timber;

public class DisclaimerEndFragment extends Fragment
{
	public static final String TAG = DisclaimerEndFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.disclaimer_end, container, false);

		final Button accept = (Button) view.findViewById(R.id.disclaimer_accept_button);
		Button cancel = (Button) view.findViewById(R.id.disclaimer_cancel_button);
		CheckBox check = (CheckBox) view.findViewById(R.id.disclaimer_accept_check);

		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				accept.setEnabled(isChecked);
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				getActivity().finish();
			}
		});

		accept.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_MULTI_PROCESS);
				Editor editor = settings.edit();
				editor.putBoolean(Constants.PREFERENCES_ACCEPTED_DISCLAIMER, true);
				editor.commit();

				Intent i = new Intent(getActivity().getApplicationContext(), MasterActivity.class);
                Timber.d("Starting MasterActivity activity");
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
