package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lechucksoftware.proxy.proxysettings.BuildConfig;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseFragment;
import com.lechucksoftware.proxy.proxysettings.ui.components.WifiAp;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabHelper;
import com.lechucksoftware.proxy.proxysettings.utils.billing.IabResult;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class InAppBillingFragment extends BaseFragment
{

    // Implementing In App Billing GuideL http://www.techotopia.com/index.php/An_Android_Studio_Google_Play_In-app_Billing_Tutorial

    @InjectView(R.id.in_app_billing_test)
    Button inAppBillingTestBtn;

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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.in_app_billing_fragment, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @OnClick(R.id.in_app_billing_test)
    void inAppBillingTest()
    {
        IabHelper mHelper = new IabHelper(getActivity(), BuildConfig.PLAY_IN_APP_BILLING_PUBLIC_KEY);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
                                   {
                                       public void onIabSetupFinished(IabResult result)
                                       {
                                           if (!result.isSuccess())
                                           {
                                               Timber.d("In-app Billing setup failed: " + result);
                                           }
                                           else
                                           {
                                               Timber.d("In-app Billing is set up OK");
                                           }
                                       }
                                   });
    }
}
