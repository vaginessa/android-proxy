package com.lechucksoftware.proxy.proxysettings.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;
import android.content.Loader;
import android.widget.*;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.ApplicationFeedbacksConfirmDialog;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PInfo;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PackagesUtils;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import com.lechucksoftware.proxy.proxysettings.utils.PInfoAdapter;
import com.lechucksoftware.proxy.proxysettings.utils.PInfoTaskLoader;
import com.lechucksoftware.proxy.proxysettings.utils.WifiAPSelectorListAdapter;

public class ApplicationsFeedbacksFragment extends EnhancedListFragment implements LoaderManager.LoaderCallbacks<List<PInfo>>
{
	public static final String TAG = "ApplicationsFeedbacksFragment";
//	static final int DIALOG_ID_PROXY = 0;
    private static ApplicationsFeedbacksFragment instance;

//    private ListView listview;
//	private ArrayList<PInfo> mListItem;
    private TextView emptyText;

    public static final int LOADER_TEST = 1;
    private PInfoAdapter apListAdapter;

    public static ApplicationsFeedbacksFragment getInstance()
    {
        if (instance == null)
            instance = new ApplicationsFeedbacksFragment();

        return instance;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.applications_list);
//
//		listview = (ListView) findViewById(R.id.list_view);
//        LoadInstalledPackagesTask task = new LoadInstalledPackagesTask();
//        task.execute();

        View v = inflater.inflate(R.layout.base_list_fragment, container, false);

        emptyText = (TextView) v.findViewById(android.R.id.empty);

        Loader<List<PInfo>> loader = getLoaderManager().initLoader(LOADER_TEST, new Bundle(), this);
        loader.forceLoad();
//        setListShown(false);

        return v;
	}

//	private class LoadInstalledPackagesTask extends AsyncTask<Void, Void, ArrayList<PInfo>>
//	{
//		@Override
//		protected void onPreExecute()
//		{
//
//		}
//
//		@Override
//		protected ArrayList<PInfo> doInBackground(Void... paramArrayOfParams)
//		{
//			mListItem = (ArrayList<PInfo>) PackagesUtils.getPackages(getActivity());
//			return mListItem;
//		}
//
//		@Override
//		protected void onPostExecute(ArrayList<PInfo> result)
//		{
////			final FragmentManager fm = getFragmentManager();
//
//			listview.setAdapter(new PInfoAdapter(getActivity(), R.id.list_view, result));
//
//			listview.setOnItemClickListener(new OnItemClickListener()
//			{
//			    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//			    {
//			    	showDialog(mListItem.get(position));
//			    }
//			});
//		}
//	}
	
    void showDialog(PInfo pInfo) 
    {
//    	ApplicationFeedbacksConfirmDialog newFragment = ApplicationFeedbacksConfirmDialog.newInstance(pInfo);
//    	newFragment.show(getSupportFragmentManager(),TAG);
    }
    
    public void doPositiveClick() {
        // Do stuff here.
        LogWrapper.i("FragmentAlertDialog", "Positive click!");
    }
    
    public void doNegativeClick() {
        // Do stuff here.
        LogWrapper.i("FragmentAlertDialog", "Negative click!");
    }


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
//        setListShown(true);

        Toast.makeText(getActivity(), "LOADED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<List<PInfo>> loader)
    {
        Toast.makeText(getActivity(), "LOADRESET", Toast.LENGTH_SHORT).show();
    }
}