package com.lechucksoftware.proxy.proxysettings.activities.help;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.lechucksoftware.proxy.proxysettings.R;

public class HelpFragmentActivity extends FragmentActivity
{
	public static final String TAG = "HelpFragmentActivity";

	private ViewPager _mViewPager;
	private ViewPagerAdapter _adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		setUpView();
		setTab();
	}

	private void setUpView()
	{
		_mViewPager = (ViewPager) findViewById(R.id.viewPager);
		_adapter = new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
		_mViewPager.setAdapter(_adapter);
		_mViewPager.setCurrentItem(0);
	}

	private void setTab()
	{
		_mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int position)
			{
				// switch(position){
				// case 0:
				// findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
				// findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
				// break;
				//
				// case 1:
				// findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
				// findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
				// break;
				// }
			}

			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// TODO Auto-generated method stub

			}

			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub

			}
		});
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
			}
			return f;
		}

		@Override
		public int getCount()
		{
			return 3;
		}

	}

}
