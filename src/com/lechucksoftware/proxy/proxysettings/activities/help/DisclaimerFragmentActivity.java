package com.lechucksoftware.proxy.proxysettings.activities.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.fragments.help.DisclaimerEndFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.help.DisclaimerIntroFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.help.HelpProblemDescriptionFragment0;
import com.lechucksoftware.proxy.proxysettings.fragments.help.HelpProblemDescriptionFragment1;
import com.lechucksoftware.proxy.proxysettings.fragments.help.HelpProblemDescriptionFragment2;
import com.lechucksoftware.proxy.proxysettings.fragments.help.HelpProblemDescriptionFragment3;
import com.lechucksoftware.proxy.proxysettings.fragments.help.HelpProblemDescriptionFragment4;
import com.lechucksoftware.proxy.proxysettings.fragments.help.HelpProblemDescriptionFragment5;

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
	public CharSequence getFragmentTitle(int position)
	{
		CharSequence title = null;
		switch (position)
		{
			case 0:
				title = getApplicationContext().getResources().getString(R.string.disclaimer_intro_title);
				break;
			case 1:
				title = getApplicationContext().getResources().getString(R.string.help_text_0_title);
				break;
			case 2:
				title = getApplicationContext().getResources().getString(R.string.help_text_1_title);
				break;
			case 3:
				title = getApplicationContext().getResources().getString(R.string.help_text_2_title);
				break;
			case 4:
				title = getApplicationContext().getResources().getString(R.string.help_text_3_title);
				break;
			case 5:
				title = getApplicationContext().getResources().getString(R.string.help_text_4_title);
				break;
			case 6:
				title = getApplicationContext().getResources().getString(R.string.help_text_5_title);
				break;
			case 7:
				title = getApplicationContext().getResources().getString(R.string.disclaimer_end_title);
				break;
		}
		return title;
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
