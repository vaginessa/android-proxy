package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.FragmentMode;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProxySelectorFragment extends BaseFragment
{
    private static final String SELECTED_WIFI_NETWORK = "SELECTED_WIFI_NETWORK";

    private APLNetworkId wifiAplNetworkId;
    private WiFiApConfig selectedConfig;

    @InjectView(R.id.tabs) PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager) ViewPager pager;

    private MyPagerAdapter adapter;

    public static ProxySelectorFragment newInstance(APLNetworkId wifiAplNetworkId)
    {
        ProxySelectorFragment fragment = new ProxySelectorFragment();

        Bundle args = new Bundle();
        
        args.putParcelable(SELECTED_WIFI_NETWORK, wifiAplNetworkId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        wifiAplNetworkId = getArguments().getParcelable(SELECTED_WIFI_NETWORK);
        selectedConfig = App.getWifiNetworksManager().getConfiguration(wifiAplNetworkId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v;

        v = inflater.inflate(R.layout.proxy_selector_dialog, container, false);
        ButterKnife.inject(this, v);

        adapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (selectedConfig.getProxySetting() == ProxySetting.STATIC)
        {
            pager.setCurrentItem(0);
        }
        else if(selectedConfig.getProxySetting() == ProxySetting.PAC)
        {
            pager.setCurrentItem(1);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.empty, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter
    {
        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            if (position == 0)
            {
                return getString(R.string.static_proxies);
            }
            else
            {
                return getString(R.string.pac_proxies);
            }
        }

        @Override
        public int getCount()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                return 2;
            }
            else
            {
                return 1;
            }
        }

        @Override
        public Fragment getItem(int position)
        {
            if (position == 0)
            {
                return ProxyListFragment.newInstance(0, FragmentMode.DIALOG, wifiAplNetworkId);
            }
            else
            {
                return PacListFragment.newInstance(0, FragmentMode.DIALOG, wifiAplNetworkId);
            }
        }
    }
}
