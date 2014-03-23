package com.lechucksoftware.proxy.proxysettings.ui.help;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.fragments.help.HelpProblemDescriptionFragment0;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HelpFragmentActivity extends FragmentActivity
{
	public static final String TAG = "HelpFragmentActivity";
	public static final int FRAGMENTS_COUNT = 6;

	private ViewPager _mViewPager;
	private ViewPagerAdapter _adapter;
	
	private PageIndicator _pageIndicator;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		setUpView(FRAGMENTS_COUNT);
	}

	public void setUpView(int fc)
	{
		_mViewPager = (ViewPager) findViewById(R.id.viewPager);
		_adapter = new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager(), fc);
		_mViewPager.setAdapter(_adapter);
		_mViewPager.setCurrentItem(0);
		
		_pageIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
		_pageIndicator.setViewPager(_mViewPager);
	}
	
	public Fragment getHelpFragment(int position)
	{
		Fragment f = new Fragment();
		switch (position)
		{
			case 0:
				f = HelpProblemDescriptionFragment0.newInstance(getApplicationContext());
				break;
//			case 1:
//				f = HelpProblemDescriptionFragment1.newInstance(getApplicationContext());
//				break;
//			case 2:
//				f = HelpProblemDescriptionFragment2.newInstance(getApplicationContext());
//				break;
//			case 3:
//				f = HelpProblemDescriptionFragment3.newInstance(getApplicationContext());
//				break;
//			case 4:
//				f = HelpProblemDescriptionFragment4.newInstance(getApplicationContext());
//				break;
//			case 5:
//				f = HelpProblemDescriptionFragment5.newInstance(getApplicationContext());
//				break;
		}
		return f;
	}
	
	public CharSequence getFragmentTitle(int position)
	{
		CharSequence title = null;
		switch (position)
		{
			case 0:
				title = getApplicationContext().getResources().getString(R.string.help_text_0_title);
				break;
			case 1:
				title = getApplicationContext().getResources().getString(R.string.help_text_1_title);
				break;
			case 2:
				title = getApplicationContext().getResources().getString(R.string.help_text_2_title);
				break;
			case 3:
				title = getApplicationContext().getResources().getString(R.string.help_text_3_title);
				break;
			case 4:
				title = getApplicationContext().getResources().getString(R.string.help_text_4_title);
				break;
			case 5:
				title = getApplicationContext().getResources().getString(R.string.help_text_0_title);
				break;
		}
		return title;
	}


	public class ViewPagerAdapter extends FragmentPagerAdapter
	{
		int _fc;
		
		public ViewPagerAdapter(Context context, FragmentManager fm, int fc)
		{
			super(fm);
			_fc = fc;
		}

		@Override
		public Fragment getItem(int position)
		{
			Fragment f = getHelpFragment(position);
			return f;
		}

		@Override
		public int getCount()
		{
			return _fc;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return getFragmentTitle(position);
		}
	}

}
