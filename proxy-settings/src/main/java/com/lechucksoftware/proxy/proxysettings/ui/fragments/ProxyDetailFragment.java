package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.constants.Requests;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncDeleteProxy;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncSaveProxy;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncUpdateLinkedWiFiAP;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.activities.ProxyDetailActivity;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputExclusionList;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputField;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.UpdateLinkedWifiAPAlertDialog;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.ProxyStatusItem;
import be.shouldit.proxy.lib.enums.ProxyStatusProperties;
import be.shouldit.proxy.lib.utils.ProxyUtils;

public class ProxyDetailFragment extends BaseDialogFragment
{
    public static ProxyDetailFragment instance;
    public static final String TAG = ProxyDetailFragment.class.getSimpleName();

    // Arguments
    private static final String SELECTED_PROXY_ARG = "SELECTED_PROXY_ARG";

    private boolean saveEnabled;
    private boolean deleteEnabled;

    private InputField proxyHost;
    private InputField proxyPort;
    private InputExclusionList proxyBypass;
//    private InputTags proxyTags;

    private Long selectedProxyId;
    private ProxyEntity selectedProxy;

    private UIHandler uiHandler;
    private RelativeLayout proxyInUseBanner;
    private RelativeLayout proxyDuplicatedBanner;

    private ScrollView proxyScrollView;
    private Map<ProxyStatusProperties,CharSequence> validationErrors;

    public static ProxyDetailFragment newInstance(Long proxyId)
    {
        ProxyDetailFragment instance = new ProxyDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(SELECTED_PROXY_ARG, proxyId);
        instance.setArguments(args);

        return instance;
    }

    public static ProxyDetailFragment newInstance()
    {
        ProxyDetailFragment instance = new ProxyDetailFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.proxy_preferences, container, false);

        setHasOptionsMenu(true);

        getUIComponents(v);
        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        uiHandler = new UIHandler();
        validationErrors = new HashMap<ProxyStatusProperties, CharSequence>();

        if (args != null && args.containsKey(SELECTED_PROXY_ARG))
        {
            selectedProxyId = (Long) getArguments().getSerializable(SELECTED_PROXY_ARG);
            selectedProxy = (ProxyEntity) App.getDBManager().getProxy(selectedProxyId);
            deleteEnabled = true;
        }

        if (selectedProxy == null)
        {
            selectedProxy = new ProxyEntity();
            deleteEnabled = false;
        }

        uiHandler.callRefreshUI();
    }

    private void getUIComponents(View v)
    {
        proxyScrollView = (ScrollView) v.findViewById(R.id.proxy_scrollview);
        proxyInUseBanner = (RelativeLayout) v.findViewById(R.id.proxy_in_use_banner);
        proxyDuplicatedBanner = (RelativeLayout) v.findViewById(R.id.proxy_duplicated_banner);

        proxyHost = (InputField) v.findViewById(R.id.proxy_host);
        proxyHost.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {  }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) { }

            @Override
            public void afterTextChanged(Editable editable)
            {
                checkValidation();
            }
        });

        proxyPort = (InputField) v.findViewById(R.id.proxy_port);
        proxyPort.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                checkValidation();
            }
        });

        proxyBypass = (InputExclusionList) v.findViewById(R.id.proxy_bypass);
        proxyBypass.addValueChangedListener(new InputExclusionList.ValueChangedListener()
        {
            @Override
            public void onExclusionListChanged(String result)
            {

                // TODO: Improve scrolling to focused bypass item -> Temporary disabled
//                proxyScrollView.scrollTo(0,proxyScrollView.getBottom());

                checkValidation();
            }
        });

//        proxyTags = (InputTags) v.findViewById(R.id.proxy_tags);
//        proxyTags.setTagsViewOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                TagsListFragment tagsListSelectorFragment = TagsListFragment.newInstance(cachedObjId);
//                tagsListSelectorFragment.show(getFragmentManager(), TAG);
//            }
//        });
    }

    private boolean validateBypass()
    {
        String value = proxyBypass.getExclusionString();
        App.getLogger().d(TAG, "Exclusion list updated: " + value);

        ProxyStatusItem item = ProxyUtils.isProxyValidExclusionList(value);
        validationErrors.remove(item.statusCode);
        if (!item.result)
        {
            validationErrors.put(item.statusCode,item.message);
            return false;
        }
        else
        {
            selectedProxy.setExclusion(value);
            return true;
        }
    }

    private boolean validateHost()
    {
        String value = proxyHost.getValue();

        proxyHost.setError(null);
        ProxyStatusItem item = ProxyUtils.isProxyValidHostname(value);
        validationErrors.remove(item.statusCode);

        if (!item.result)
        {
            proxyHost.setError(item.message);
            validationErrors.put(item.statusCode, item.message);
            return false;
        }
        else
        {
            selectedProxy.setHost(value);
            return true;
        }
    }

    private boolean validatePort()
    {
        Integer value = null;

        try
        {
            value = Integer.parseInt(proxyPort.getValue());
        }
        catch (NumberFormatException e)
        {
            value = Integer.MAX_VALUE;
        }

        ProxyStatusItem item = ProxyUtils.isProxyValidPort(value);
        validationErrors.remove(item.statusCode);

        proxyPort.setError(null);
        if (!item.result)
        {
            proxyPort.setError(item.message);
            validationErrors.put(item.statusCode, item.message);
            return false;
        }
        else
        {
            selectedProxy.setPort(value);
            return true;
        }
    }

    private void checkValidation()
    {
        if (
            validateHost() &&
            validatePort() &&
            validateBypass())
        {
            enableSave();
        }
        else
        {
            disableSave();
        }

        // TODO: Add check for duplicated configuration to Async handler
        proxyDuplicatedBanner.setVisibility(View.GONE);
        String host = selectedProxy.getHost();
        Integer port = selectedProxy.getPort();
        if (host != null && port != null)
        {
            List<Long> duplicatedIDs = App.getDBManager().findDuplicatedProxy(host, port);
            if (selectedProxy.isPersisted())
            {
                proxyDuplicatedBanner.setVisibility(UIUtils.booleanToVisibility(duplicatedIDs.size() > 1));
            }
            else
            {
                proxyDuplicatedBanner.setVisibility(UIUtils.booleanToVisibility(duplicatedIDs.size() > 0));
            }
        }
    }

    private void refreshUI()
    {
        if (selectedProxy != null)
        {
            proxyInUseBanner.setVisibility(UIUtils.booleanToVisibility(selectedProxy.getInUse()));

            proxyHost.setValue(selectedProxy.getHost());
            if (selectedProxy.getPort() != null && selectedProxy.getPort() != 0)
            {
                proxyPort.setValue(selectedProxy.getPort());
            }

            proxyBypass.setExclusionString(selectedProxy.getExclusion());
//                proxyTags.setTags(selectedProxy.getTags());

            checkValidation();
        }
        else
        {
            // TODO: Add handling here
            App.getEventsReporter().sendException(new Exception("NO PROXY SELECTED"));
        }
    }

    private class UIHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            Bundle b = message.getData();

            App.getLogger().w(TAG, "handleMessage: " + b.toString());

            refreshUI();
        }

        public void callRefreshUI()
        {
            sendEmptyMessage(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.proxy_details, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem saveMenuItem = menu.findItem(R.id.menu_save);
        if (saveMenuItem != null)
        {
            saveMenuItem.setVisible(saveEnabled);
        }

        MenuItem deleteMenuItem = menu.findItem(R.id.menu_delete);
        if (deleteMenuItem != null)
        {
            deleteMenuItem.setVisible(deleteEnabled);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent mainIntent = new Intent(getActivity(), MasterActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                return true;

            case R.id.menu_save:
                saveProxy();
                return true;

            case R.id.menu_delete:
                deleteProxy();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveProxy()
    {
        try
        {
            if (selectedProxy.getInUse())
            {
                UpdateLinkedWifiAPAlertDialog updateDialog = UpdateLinkedWifiAPAlertDialog.newInstance();
                updateDialog.setTargetFragment(this, Requests.UPDATE_LINKED_WIFI_AP);
                updateDialog.show(getFragmentManager(), "UpdateLinkedWifiAPAlertDialog");
            }
            else
            {
                AsyncSaveProxy asyncSaveProxy = new AsyncSaveProxy(this,selectedProxy);
                asyncSaveProxy.execute();
                getActivity().finish();
            }
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }

    private void deleteProxy()
    {
        try
        {
            if (selectedProxy.getInUse())
            {
                UIUtils.showError(getActivity(), R.string.proxy_in_use_cannot_delete);
            }
            else
            {
                AsyncDeleteProxy asyncDeleteProxy = new AsyncDeleteProxy(this,selectedProxy);
                asyncDeleteProxy.execute();
                getActivity().finish();
            }
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }

    public void enableSave()
    {
        saveEnabled = true;
        getActivity().invalidateOptionsMenu();
    }

    public void disableSave()
    {
        saveEnabled = false;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Requests.UPDATE_LINKED_WIFI_AP)
        {
            ProxyEntity persistedProxy = App.getDBManager().getProxy(selectedProxy.getId());

            AsyncUpdateLinkedWiFiAP asyncUpdateLinkedWiFiAP = new AsyncUpdateLinkedWiFiAP(getActivity(), persistedProxy, selectedProxy);
            asyncUpdateLinkedWiFiAP.execute();

            App.getDBManager().upsertProxy(selectedProxy);
        }
    }
}
