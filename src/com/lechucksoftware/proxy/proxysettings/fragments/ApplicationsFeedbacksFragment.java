package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.adapters.PInfoAdapter;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.ApplicationFeedbacksConfirmDialog;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PInfo;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.PInfoTaskLoader;

import java.util.List;

public class ApplicationsFeedbacksFragment extends EnhancedListFragment implements LoaderManager.LoaderCallbacks<List<PInfo>>
{
    public static final String TAG = "ApplicationsFeedbacksFragment";
    //	static final int DIALOG_ID_PROXY = 0;
    private static ApplicationsFeedbacksFragment instance;
    private TextView emptyText;

    public static final int LOADER_TEST = 1;
    private PInfoAdapter apListAdapter;
    ProgressDialog progressDialog;

    public static ApplicationsFeedbacksFragment getInstance()
    {
        if (instance == null)
            instance = new ApplicationsFeedbacksFragment();

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Toast.makeText(getActivity(), "CREATEVIEW", Toast.LENGTH_SHORT).show();

        View v = inflater.inflate(R.layout.base_list_fragment, container, false);

        emptyText = (TextView) v.findViewById(android.R.id.empty);

        Loader<List<PInfo>> loader = getLoaderManager().initLoader(LOADER_TEST, new Bundle(), this);
        loader.forceLoad();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing... Please Wait...");
        progressDialog.show();

        return v;
    }

//    void showDialog(PInfo pInfo)
//    {
//        ApplicationFeedbacksConfirmDialog newFragment = ApplicationFeedbacksConfirmDialog.newInstance(pInfo);
//        newFragment.show(getSupportFragmentManager(), TAG);
//    }
//
//    public void doPositiveClick()
//    {
//        // Do stuff here.
//        LogWrapper.i("FragmentAlertDialog", "Positive click!");
//    }
//
//    public void doNegativeClick()
//    {
//        // Do stuff here.
//        LogWrapper.i("FragmentAlertDialog", "Negative click!");
//    }


    @Override
    public Loader<List<PInfo>> onCreateLoader(int id, Bundle args)
    {
        return new PInfoTaskLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<PInfo>> loader, List<PInfo> data)
    {
        apListAdapter = new PInfoAdapter(getActivity());
        setListAdapter(apListAdapter);
        apListAdapter.setData(data);

        progressDialog.dismiss();
//        Toast.makeText(getActivity(), "LOADED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<List<PInfo>> loader)
    {
        Toast.makeText(getActivity(), "LOADRESET", Toast.LENGTH_SHORT).show();
    }
}