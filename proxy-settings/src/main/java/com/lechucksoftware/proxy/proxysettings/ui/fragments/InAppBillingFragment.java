package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.IABManager;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.ui.activities.InAppBillingActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InAppBillingFragment extends BaseFragment
{
    // Implementing In App Billing GuideL http://www.techotopia.com/index.php/An_Android_Studio_Google_Play_In-app_Billing_Tutorial
    @InjectView(R.id.in_app_billing_test) Button inAppBillingTestBtn;

    public static InAppBillingFragment newInstance()
    {
        InAppBillingFragment fragment = new InAppBillingFragment();
        return fragment;
    }

    public InAppBillingFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.in_app_billing_fragment, container, false);
        ButterKnife.inject(this, v);

        return v;
    }

    @OnClick(R.id.in_app_billing_test)
    void inAppBillingTest()
    {
        Activity activity = getActivity();
        if (activity != null && activity instanceof InAppBillingActivity)
        {
            ((InAppBillingActivity) activity).launchPurchase();
        }
    }
}
