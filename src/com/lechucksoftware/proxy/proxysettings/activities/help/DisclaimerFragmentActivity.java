package com.lechucksoftware.proxy.proxysettings.activities.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lechucksoftware.proxy.proxysettings.R;

public class DisclaimerFragmentActivity extends HelpFragmentActivity
{
	public static final String TAG = "DisclaimerFragmentActivity";
	public static final int FRAGMENTS_COUNT = 8;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		super.setUpView(FRAGMENTS_COUNT);
	}

	@Override
	public Fragment getHelpFragment(int position)
	{
		Fragment f = new Fragment();
		switch (position)
		{
			case 0:
				f = DisclaimerIntroFragment.newInstance(getApplicationContext());
				break;
			
			case 1:
				f = HelpProblemDescriptionFragment0.newInstance(getApplicationContext());
				break;
			case 2:
				f = HelpProblemDescriptionFragment1.newInstance(getApplicationContext());
				break;
			case 3:
				f = HelpProblemDescriptionFragment2.newInstance(getApplicationContext());
				break;
			case 4:
				f = HelpProblemDescriptionFragment3.newInstance(getApplicationContext());
				break;
			case 5:
				f = HelpProblemDescriptionFragment4.newInstance(getApplicationContext());
				break;
			case 6:
				f = HelpProblemDescriptionFragment5.newInstance(getApplicationContext());
				break;
				
			case 7:
				f = DisclaimerEndFragment.newInstance(getApplicationContext());
				break;
		}
		return f;
	}
}
