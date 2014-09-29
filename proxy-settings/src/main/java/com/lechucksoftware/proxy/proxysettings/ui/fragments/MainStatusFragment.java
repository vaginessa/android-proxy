package com.lechucksoftware.proxy.proxysettings.ui.fragments;

/**
 * Created by mpagliar on 29/09/2014.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MainActivity;

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
        FragmentManager fragmentManager = ((MainActivity) getActivity()).getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, ProxyListFragment.newInstance())
                .commit();
    }

    @OnClick(R.id.main_see_wifi_list)
    public void openWiFiApList()
    {
        FragmentManager fragmentManager = ((MainActivity) getActivity()).getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, WiFiApListFragment.getInstance())
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
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

        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}