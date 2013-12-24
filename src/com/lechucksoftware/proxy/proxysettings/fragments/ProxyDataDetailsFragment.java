package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.InputBypass;
import com.lechucksoftware.proxy.proxysettings.components.InputField;
import com.lechucksoftware.proxy.proxysettings.components.InputTags;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.BugReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;
import com.shouldit.proxy.lib.log.LogWrapper;


public class ProxyDataDetailsFragment extends DialogFragment implements IBaseFragment
{
    public static ProxyDataDetailsFragment instance;
    public static final String TAG = ProxyDataDetailsFragment.class.getSimpleName();

    // Arguments
    private static final String SELECTED_PROXY_ARG = "SELECTED_PROXY_ARG";
    private ProxyEntity selectedProxy;
    private InputField proxyHost;
    private InputField proxyPort;
    private InputBypass proxyBypass;
    private InputTags proxyTags;

    /**
     * Create a new instance of WifiAPDetailsFragment
     */
    public static ProxyDataDetailsFragment newInstance(ProxyEntity selectedProxy)
    {
        ProxyDataDetailsFragment instance = new ProxyDataDetailsFragment();

        Bundle args = new Bundle();
        args.putSerializable(SELECTED_PROXY_ARG, selectedProxy);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        selectedProxy = (ProxyEntity) getArguments().getSerializable(SELECTED_PROXY_ARG);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.proxy_preferences, container, false);

        getUIComponents(v);

        initUI();
        refreshUI();

        return v;
    }

    private void getUIComponents(View v)
    {
        proxyHost = (InputField) v.findViewById(R.id.proxy_host);
        proxyPort = (InputField) v.findViewById(R.id.proxy_port);
        proxyBypass = (InputBypass) v.findViewById(R.id.proxy_bypass);
        proxyTags = (InputTags) v.findViewById(R.id.proxy_tags);
    }

    private void saveConfiguration()
    {
        try
        {
//            ApplicationGlobals.getDBManager().up
        }
        catch (Exception e)
        {
            BugReportingUtils.sendException(e);
            showError(R.string.exception_apl_writeconfig_error_message);
        }
    }

    protected void showError(int error)
    {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.proxy_error)
                .setMessage(error)
                .setPositiveButton(R.string.proxy_error_dismiss, null)
                .show();
    }

    public void initUI()
    {
        if (selectedProxy != null)
        {
            if (selectedProxy.host == null || selectedProxy.host.length() == 0)
            {
                proxyHost.setValue(getText(R.string.proxy_hostname_hint));
            }
            else
            {
                proxyHost.setValue(selectedProxy.host);
            }

            if (selectedProxy.port == null || selectedProxy.port == 0)
            {
                proxyPort.setValue(getText(R.string.proxy_port_hint));
            }
            else
            {
                proxyPort.setValue(selectedProxy.port);
            }

            proxyBypass.setExclusionString(selectedProxy.exclusion);
            proxyTags.setTags(selectedProxy.getTags());
        }
    }

    public void refreshUI()
    {

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // Only persistant preferences
        LogWrapper.d(TAG, "Changed preference: " + key);

//		if (key == "pref name bla bla")
//		{}
    }


    @Override
    public void onResume()
    {
        super.onResume();

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        if (selectedProxy != null)
        {
//            actionBar.setTitle(selconf.description);
//            ActionManager.getInstance().refreshUI();
        }
        else
        {
            NavigationUtils.GoToAccessPointListFragment(getFragmentManager());
        }
    }
}
