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

public class HelpFragmentActivity extends FragmentActivity
{
	public static final String TAG = "HelpFragmentActivity";

	private ViewPager _mViewPager;
	private ViewPagerAdapter _adapter;
	
	private Button _nextButton;
	private Button _previousButton;

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
		_previousButton = (Button)findViewById(R.id.help_previous_button);
		_nextButton = (Button)findViewById(R.id.help_next_button);
		_previousButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{
				_mViewPager.setCurrentItem(_mViewPager.getCurrentItem() - 1);	
			}
		});
		
		_nextButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{
				_mViewPager.setCurrentItem(_mViewPager.getCurrentItem() + 1);	
			}
		});
		
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
				case 3:
					f = HelpProblemDescriptionFragment3.newInstance(_context);
					break;
			}
			return f;
		}

		@Override
		public int getCount()
		{
			return 4;
		}

	}

}
