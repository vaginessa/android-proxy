package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.components.TagModel;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 04/10/13.
 */
public class TagsTaskLoader extends AsyncTaskLoader<List<TagModel>>
{
    private final Context ctx;
    private final DBProxy proxy;

    public TagsTaskLoader(Context context, DBProxy p)
    {
        super(context);
        ctx = context;
        proxy = p;
    }

    @Override
    public List<TagModel> loadInBackground()
    {
        List<DBTag> dbTags = ApplicationGlobals.getDBManager().getAllTags();
        List<TagModel> models = new ArrayList<TagModel>();

        List<DBTag> tags = null;
        if (proxy != null)
            tags = proxy.getTags();

        for(DBTag tag: dbTags)
        {
            boolean contained = false;
            if (tags != null)
            {
                contained = tags.contains(tag);
            }
            models.add(new TagModel(tag, contained));
        }

        return models;
    }
}
