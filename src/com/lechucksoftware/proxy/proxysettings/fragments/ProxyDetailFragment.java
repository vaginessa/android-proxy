package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.InputExclusionList;
import com.lechucksoftware.proxy.proxysettings.components.InputField;
import com.lechucksoftware.proxy.proxysettings.components.InputTags;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.lechucksoftware.proxy.proxysettings.utils.NavigationUtils;

import java.lang.reflect.Proxy;


public class ProxyDetailFragment extends DialogFragment implements IBaseFragment
{
    public static ProxyDetailFragment instance;
    public static final String TAG = ProxyDetailFragment.class.getSimpleName();

    // Arguments
    private static final String SELECTED_PROXY_ARG = "SELECTED_PROXY_ARG";

    private InputField proxyHost;
    private InputField proxyPort;
    private InputExclusionList proxyBypass;
    private InputTags proxyTags;
    private Long selectedProxyID;
    private ProxyEntity selectedProxy;

    /**
     * Create a new instance of WiFiApDetailFragment
     */
    public static ProxyDetailFragment newInstance(ProxyEntity selectedProxy)
    {
        ProxyDetailFragment instance = new ProxyDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(SELECTED_PROXY_ARG, selectedProxy.getId());
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        selectedProxyID = (Long) getArguments().getSerializable(SELECTED_PROXY_ARG);
        selectedProxy = ApplicationGlobals.getDBManager().getProxy(selectedProxyID);

        instance = this;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//    {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.proxy_details_menu, menu);
//    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu)
//    {
//        super.onPrepareOptionsMenu(menu);
//    }

//    private void createCancelSaveActionBar()
//    {
//        final ActionBar actionBar = getActivity().getActionBar();
//        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        final View customActionBarView = inflater.inflate(R.layout.save, null);
//        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
//                new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        // "Done"
//                        saveConfiguration();
//                        NavigationUtils.GoToProxiesList(getFragmentManager());
//                    }
//                });
//
//        // Show the custom action bar view and hide the normal Home icon and title.
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
//                                    ActionBar.DISPLAY_SHOW_CUSTOM |
//                                    ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
//
//        actionBar.setCustomView(customActionBarView);
//
//    }

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
        proxyBypass = (InputExclusionList) v.findViewById(R.id.proxy_bypass);
        proxyTags = (InputTags) v.findViewById(R.id.proxy_tags);
        proxyTags.setTagsViewOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TagsListFragment tagsListSelectorFragment = TagsListFragment.newInstance(selectedProxyID);
                tagsListSelectorFragment.show(getFragmentManager(), TAG);
            }
        });
    }

    private void saveConfiguration()
    {
        try
        {
            ProxyEntity newProxy = ApplicationGlobals.getDBManager().getProxy(selectedProxyID);
            newProxy.host = proxyHost.getValue();
            newProxy.port = Integer.parseInt(proxyPort.getValue());
            newProxy.exclusion = proxyBypass.getExclusionList();

            ApplicationGlobals.getDBManager().updateProxy(selectedProxyID, newProxy);
//            ApplicationGlobals.getProxyManager().updateWifiConfiguration(selectedProxy, newProxy);
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
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
            proxyHost.setValue(selectedProxy.host);
            proxyHost.setHint(getText(R.string.proxy_hostname_hint));

            if (selectedProxy.port != null && selectedProxy.port != 0)
            {
                proxyPort.setValue(selectedProxy.port);
            }
            proxyPort.setHint(getText(R.string.proxy_port_hint));

            proxyBypass.setExclusionString(selectedProxy.exclusion);
            proxyTags.setTags(selectedProxy.getTags());
        }
    }

    public void refreshUI()
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (selectedProxyID == null)
        {
            NavigationUtils.GoToAccessPointListFragment(getFragmentManager());
        }
    }
}
