package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.content.Intent;
import android.net.Uri;
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
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncDeleteProxy;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncSaveProxy;
import com.lechucksoftware.proxy.proxysettings.tasks.AsyncUpdateLinkedWiFiAP;
import com.lechucksoftware.proxy.proxysettings.ui.activities.MasterActivity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.ui.components.InputField;
import com.lechucksoftware.proxy.proxysettings.ui.dialogs.UpdateLinkedWifiAPAlertDialog;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.ProxyStatusItem;
import be.shouldit.proxy.lib.enums.ProxyStatusProperties;
import be.shouldit.proxy.lib.utils.ProxyUtils;
import timber.log.Timber;

public class PacDetailFragment extends BaseDialogFragment
{
    public static PacDetailFragment instance;
    public static final String TAG = PacDetailFragment.class.getSimpleName();

    // Arguments
    private static final String SELECTED_PAC_ARG = "SELECTED_PAC_ARG";

    private boolean saveEnabled;
    private boolean deleteEnabled;

    private InputField pacUrlFile;

    private Long selectedPacId;
    private PacEntity selectedPac;

    private UIHandler uiHandler;
    private RelativeLayout proxyInUseBanner;
    private RelativeLayout proxyDuplicatedBanner;

    private ScrollView proxyScrollView;
    private Map<ProxyStatusProperties,CharSequence> validationErrors;

    public static PacDetailFragment newInstance(Long pacId)
    {
        PacDetailFragment instance = new PacDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(SELECTED_PAC_ARG, pacId);
        instance.setArguments(args);

        return instance;
    }

    public static PacDetailFragment newInstance()
    {
        PacDetailFragment instance = new PacDetailFragment();
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

        if (args != null && args.containsKey(SELECTED_PAC_ARG))
        {
            selectedPacId = (Long) getArguments().getSerializable(SELECTED_PAC_ARG);
            selectedPac = (PacEntity) App.getDBManager().getPac(selectedPacId);
            deleteEnabled = true;
        }

        if (selectedPac == null)
        {
            selectedPac = new PacEntity();
            deleteEnabled = false;
        }

        uiHandler.callRefreshUI();
    }

    private void getUIComponents(View v)
    {
        proxyScrollView = (ScrollView) v.findViewById(R.id.proxy_scrollview);
        proxyInUseBanner = (RelativeLayout) v.findViewById(R.id.proxy_in_use_banner);
        proxyDuplicatedBanner = (RelativeLayout) v.findViewById(R.id.proxy_duplicated_banner);

        pacUrlFile = (InputField) v.findViewById(R.id.proxy_host);
        pacUrlFile.addTextChangedListener(new TextWatcher()
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
    }

    private boolean validatePacUrlFile()
    {
        String value = pacUrlFile.getValue();

        pacUrlFile.setError(null);
        ProxyStatusItem item = ProxyUtils.isPACValidURI(value);
        validationErrors.remove(item.statusCode);

        if (!item.result)
        {
            pacUrlFile.setError(item.message);
            validationErrors.put(item.statusCode, item.message);
            return false;
        }
        else
        {
            selectedPac.setPacUrlFile(value);
            return true;
        }
    }

    private void checkValidation()
    {
        if (
            validatePacUrlFile())
        {
            enableSave();
        }
        else
        {
            disableSave();
        }

        // TODO: Add check for duplicated configuration to Async handler
        proxyDuplicatedBanner.setVisibility(View.GONE);
        Uri urlFile = selectedPac.getPacUriFile();
        if (urlFile != null)
        {
            List<Long> duplicatedIDs = App.getDBManager().findDuplicatedPac(urlFile.toString());
            if (selectedPac.isPersisted())
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
        if (selectedPac != null)
        {
            proxyInUseBanner.setVisibility(UIUtils.booleanToVisibility(selectedPac.getInUse()));

            pacUrlFile.setValue(selectedPac.getPacUriFile());

            checkValidation();
        }
        else
        {
            // TODO: Add handling here
            Timber.e(new Exception(),"NO PROXY SELECTED");
        }
    }

    private class UIHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            Bundle b = message.getData();

            Timber.w("handleMessage: " + b.toString());

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
            if (selectedPac.getInUse())
            {
                UpdateLinkedWifiAPAlertDialog updateDialog = UpdateLinkedWifiAPAlertDialog.newInstance();
                updateDialog.setTargetFragment(this, Requests.UPDATE_LINKED_WIFI_AP);
                updateDialog.show(getFragmentManager(), "UpdateLinkedWifiAPAlertDialog");
            }
            else
            {
                AsyncSaveProxy asyncSaveProxy = new AsyncSaveProxy(this, selectedPac);
                asyncSaveProxy.execute();
                getActivity().finish();
            }
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception saving proxy");
        }
    }

    private void deleteProxy()
    {
        try
        {
            if (selectedPac.getInUse())
            {
                UIUtils.showError(getActivity(), R.string.proxy_in_use_cannot_delete);
            }
            else
            {
                AsyncDeleteProxy asyncDeleteProxy = new AsyncDeleteProxy(this, selectedPac);
                asyncDeleteProxy.execute();
                getActivity().finish();
            }
        }
        catch (Exception e)
        {
            Timber.e(e, "Exception deleting proxy");
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
            PacEntity persistedPac = App.getDBManager().getPac(selectedPac.getId());

            AsyncUpdateLinkedWiFiAP asyncUpdateLinkedWiFiAP = new AsyncUpdateLinkedWiFiAP(getActivity(), persistedPac, selectedPac);
            asyncUpdateLinkedWiFiAP.execute();

            App.getDBManager().upsertPac(selectedPac);
        }
    }
}
