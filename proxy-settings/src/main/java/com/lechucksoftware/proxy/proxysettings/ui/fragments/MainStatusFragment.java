package com.lechucksoftware.proxy.proxysettings.ui.fragments;

/**
 * Created by mpagliar on 29/09/2014.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainStatusFragment extends Fragment
{
    @InjectView(R.id.main_see_wifi_list) Button seeWifiListBtn;
    @InjectView(R.id.main_see_proxies_list) Button seeProxiesListBtn;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainStatusFragment newInstance(int sectionNumber)
    {
        MainStatusFragment fragment = new MainStatusFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainStatusFragment()
    {

    }

    @OnClick(R.id.main_see_proxies_list)
    public void openProxiesList()
    {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ProxyListFragment.newInstance(1))
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.main_see_wifi_list)
    public void openWiFiApList()
    {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, WiFiApListFragment.newInstance(2))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        ((MasterActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        MasterActivity master = (MasterActivity) getActivity();

        if (master != null && !master.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            inflater.inflate(R.menu.main, menu);
            master.restoreActionBar();
        }
    }
}