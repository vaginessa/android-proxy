package com.lechucksoftware.proxy.proxysettings.fragments;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.adapters.TagsListAdapter;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;
import com.lechucksoftware.proxy.proxysettings.fragments.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.loaders.TagsDBTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 17/05/13.
 */
public class TagsListSelectorFragment extends BaseDialogFragment implements LoaderManager.LoaderCallbacks<List<DBTag>>
{
    private static final String TAG = TagsListSelectorFragment.class.getSimpleName();
    private static TagsListSelectorFragment instance;
    private TextView emptyText;
    private RelativeLayout progress;
    private static final int LOADER_TAGSDB = 1;
    private Loader<List<DBTag>> loader;
    private ListView listView;
    private TagsListAdapter tagsListAdapter;

    public static TagsListSelectorFragment getInstance()
    {
        if (instance == null)
            instance = new TagsListSelectorFragment();

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.standard_list, container, false);

        progress = (RelativeLayout) v.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        emptyText = (TextView) v.findViewById(android.R.id.empty);
        listView = (ListView) v.findViewById(android.R.id.list);

        if (tagsListAdapter == null)
        {
            tagsListAdapter = new TagsListAdapter(getActivity());
        }

        listView.setAdapter(tagsListAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setItemChecked(0,true);
        listView.setItemChecked(1,true);

        loader = getLoaderManager().initLoader(LOADER_TAGSDB, new Bundle(), this);
        loader.forceLoad();

        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getActivity().getResources().getString(R.string.proxy_configurations));
    }

    /**
     * LoaderManager Interface methods
     */

    @Override
    public Loader<List<DBTag>> onCreateLoader(int i, Bundle bundle)
    {
        TagsDBTaskLoader tagsDBTaskLoader = new TagsDBTaskLoader(getActivity());
        return tagsDBTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<DBTag>> listLoader, List<DBTag> dbTags)
    {
        if (dbTags != null && dbTags.size() > 0)
        {
            tagsListAdapter.setData(dbTags);
            emptyText.setVisibility(View.GONE);
        }
        else
        {
            tagsListAdapter.setData(new ArrayList<DBTag>());
            emptyText.setText(getResources().getString(R.string.tags_empty_list));
            emptyText.setVisibility(View.VISIBLE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<DBTag>> listLoader)
    {

    }

}
