package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;
import com.lechucksoftware.proxy.proxysettings.db.TagEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 04/10/13.
 */
public class TagsTaskLoader extends AsyncTaskLoader<List<TagEntity>>
{
    private final Context ctx;
    private final ProxyEntity selectedProxy;

    public TagsTaskLoader(Context context, ProxyEntity proxy)
    {
        super(context);
        ctx = context;
        selectedProxy = proxy;
    }

    @Override
    public List<TagEntity> loadInBackground()
    {
        List<TagEntity> dbTags = ApplicationGlobals.getDBManager().getAllTags();

        List<TagEntity> tags = null;

        if (selectedProxy.isPersisted)
        {
            tags = ApplicationGlobals.getDBManager().getTagsForProxy(selectedProxy.getId());
        }

        for(TagEntity tag: dbTags)
        {
            if (tags != null)
            {
                tag.isSelected = tags.contains(tag);
            }
        }

        return dbTags;
    }
}
