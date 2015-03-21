package com.lechucksoftware.proxy.proxysettings.ui.fragments;

import android.app.Dialog;


import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.lechucksoftware.proxy.proxysettings.R;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseActivity;
import com.lechucksoftware.proxy.proxysettings.ui.adapters.TagsListAdapter;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;
import com.lechucksoftware.proxy.proxysettings.ui.base.BaseDialogFragment;
import com.lechucksoftware.proxy.proxysettings.loaders.TagsTaskLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by marco on 17/05/13.
 */
public class TagsListFragment extends BaseDialogFragment implements LoaderManager.LoaderCallbacks<List<TagEntity>>
{
    private static final String TAG = TagsListFragment.class.getSimpleName();
    public static final String DBPROXY_ARG = "DBPROXY_ARG";
    private static TagsListFragment instance;
    private TextView emptyText;
    private RelativeLayout progress;
    private static final int LOADER_TAGSDB = 1;
    private Loader<List<TagEntity>> loader;
    private ListView listView;
    private TagsListAdapter tagsListAdapter;
    private Button okButton;
    private Dialog dialog;
    private ProxyEntity selectedProxy;
    private UUID cachedObjId;

    public TagsListFragment()
    {}

    public static TagsListFragment newInstance(UUID cachedProxyUUID)
    {
        if (instance == null)
            instance = new TagsListFragment();

        Bundle args = new Bundle();
        args.putSerializable(DBPROXY_ARG,cachedProxyUUID);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

//        if (args != null && args.containsKey(DBPROXY_ARG))
//        {
//            cachedObjId = (UUID) getArguments().getSerializable(DBPROXY_ARG);
//            selectedProxy = (ProxyEntity) App.getCacheManager().get(cachedObjId);
//        }
//        else
//        {
//            // TODO: Add handling here
//            App.getEventsReporter().sendException(new Exception("NO PROXY RECEIVED"));
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        dialog = getDialog();
        if (dialog != null)
        {
            dialog.setTitle("Select Proxy TAGs");
        }

        View v = inflater.inflate(R.layout.tags_dialog_list, container, false);

        progress = (RelativeLayout) v.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        emptyText = (TextView) v.findViewById(android.R.id.empty);
        listView = (ListView) v.findViewById(android.R.id.list);
        okButton = (Button) v.findViewById(R.id.dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                for (int i = 0; i<tagsListAdapter.getCount(); i++)
                {
                    TagEntity t = tagsListAdapter.getItem(i);
                    if (t.isSelected())
                    {
                        selectedProxy.addTag(t);
                    }
                    else
                    {
                        selectedProxy.removeTag(t);
                    }
                }

                ((BaseActivity) getActivity()).refreshUI();
                dialog.dismiss();
            }
        });

        if (tagsListAdapter == null)
        {
            tagsListAdapter = new TagsListAdapter(getActivity());
        }

        listView.setAdapter(tagsListAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        loader = getLoaderManager().initLoader(LOADER_TAGSDB, new Bundle(), this);
        loader.forceLoad();

        return v;
    }

    /**
     * LoaderManager Interface methods
     */

    @Override
    public Loader<List<TagEntity>> onCreateLoader(int i, Bundle bundle)
    {
        TagsTaskLoader tagsDBTaskLoader = new TagsTaskLoader(getActivity(), cachedObjId);
        return tagsDBTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<TagEntity>> listLoader, List<TagEntity> tagModels)
    {
        if (tagModels != null && tagModels.size() > 0)
        {
            tagsListAdapter.setData(tagModels);
            listView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
        else
        {
            tagsListAdapter.setData(new ArrayList<TagEntity>());
            listView.setVisibility(View.GONE);
            emptyText.setText(getString(R.string.tags_empty_list));
            emptyText.setVisibility(View.VISIBLE);
        }

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<TagEntity>> listLoader)
    {

    }

}
