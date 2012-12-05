package com.lechucksoftware.proxy.proxysettings.activities.help;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.R;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class HelpFragmentActivity extends FragmentActivity
{
	public static final String TAG = "HelpFragmentActivity";

	private ViewPager _mViewPager;
	private ViewPagerAdapter _adapter;
	
	private PageIndicator _pageIndicator;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		setUpView();
	}

	private void setUpView()
	{
		_mViewPager = (ViewPager) findViewById(R.id.viewPager);
		_adapter = new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
		_mViewPager.setAdapter(_adapter);
		_mViewPager.setCurrentItem(0);
		
		_pageIndicator = (LinePageIndicator)findViewById(R.id.indicator);
		_pageIndicator.setViewPager(_mViewPager);
	}

	public class ViewPagerAdapter extends FragmentPagerAdapter
	{
		private Context _context;

		public ViewPagerAdapter(Context context, FragmentManager fm)
		{
			super(fm);
			_context = context;

		}

		@Override
		public Fragment getItem(int position)
		{
			Fragment f = new Fragment();
			switch (position)
			{
				case 0:
					f = HelpProblemDescriptionFragment0.newInstance(_context);
					break;
				case 1:
					f = HelpProblemDescriptionFragment1.newInstance(_context);
					break;
				case 2:
					f = HelpProblemDescriptionFragment2.newInstance(_context);
					break;
				case 3:
					f = HelpProblemDescriptionFragment3.newInstance(_context);
					break;
				case 4:
					f = HelpProblemDescriptionFragment4.newInstance(_context);
					break;
				case 5:
					f = HelpProblemDescriptionFragment5.newInstance(_context);
					break;
			}
			return f;
		}

		@Override
		public int getCount()
		{
			return 6;
		}

	}

}
