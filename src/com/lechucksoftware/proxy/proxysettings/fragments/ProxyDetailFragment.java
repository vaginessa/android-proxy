package com.lechucksoftware.proxy.proxysettings.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.components.InputExclusionList;
import com.lechucksoftware.proxy.proxysettings.components.InputField;
import com.lechucksoftware.proxy.proxysettings.components.InputTags;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.fragments.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.fragments.base.IBaseFragment;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;
import com.shouldit.proxy.lib.log.LogWrapper;

import java.util.UUID;


public class ProxyDetailFragment extends BaseDialogFragment implements IBaseFragment
{
    public static ProxyDetailFragment instance;
    public static final String TAG = ProxyDetailFragment.class.getSimpleName();

    // Arguments
    private static final String SELECTED_PROXY_ARG = "SELECTED_PROXY_ARG";

    private InputField proxyHost;
    private InputField proxyPort;
    private InputExclusionList proxyBypass;
    private InputTags proxyTags;
    private ProxyEntity selectedProxy;
    private UUID cachedObjId;
    private UIHandler uiHandler;

    public static ProxyDetailFragment newInstance(UUID cachedObjId)
    {
        ProxyDetailFragment instance = new ProxyDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(SELECTED_PROXY_ARG, cachedObjId);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        uiHandler = new UIHandler();

        if (args != null && args.containsKey(SELECTED_PROXY_ARG))
        {
            cachedObjId = (UUID) getArguments().getSerializable(SELECTED_PROXY_ARG);
            selectedProxy = (ProxyEntity) ApplicationGlobals.getCacheManager().get(cachedObjId);
        }
        else
        {
            // TODO: Add handling here
            EventReportingUtils.sendException(new Exception("NO PROXY RECEIVED"));
        }

        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.proxy_preferences, container, false);

        getUIComponents(v);
        uiHandler.refreshUI();

        return v;
    }

    private void getUIComponents(View v)
    {
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
                selectedProxy.host = editable.toString();
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
                selectedProxy.port = Integer.parseInt(editable.toString());
            }
        });

        proxyBypass = (InputExclusionList) v.findViewById(R.id.proxy_bypass);

        proxyTags = (InputTags) v.findViewById(R.id.proxy_tags);
        proxyTags.setTagsViewOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TagsListFragment tagsListSelectorFragment = TagsListFragment.newInstance(cachedObjId);
                tagsListSelectorFragment.show(getFragmentManager(), TAG);
            }
        });
    }

    @Override
    public void refreshUI()
    {
        uiHandler.callRefreshUI();
    }

    private class UIHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            Bundle b = message.getData();

            LogWrapper.w(TAG, "handleMessage: " + b.toString());

            refreshUI();
        }

        public void callRefreshUI()
        {
            sendEmptyMessage(0);
        }

        private void refreshUI()
        {
            if (selectedProxy != null)
            {
                proxyHost.setValue(selectedProxy.host);
                if (selectedProxy.port != null && selectedProxy.port != 0)
                {
                    proxyPort.setValue(selectedProxy.port);
                }

                proxyBypass.setExclusionString(selectedProxy.exclusion);
                proxyTags.setTags(selectedProxy.getTags());
            }
        }
    }
}