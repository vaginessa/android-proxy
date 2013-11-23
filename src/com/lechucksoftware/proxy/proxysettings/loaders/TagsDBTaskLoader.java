package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.db.DBProxy;
import com.lechucksoftware.proxy.proxysettings.db.DBTag;

import java.util.List;

/**
 * Created by marco on 04/10/13.
 */
public class TagsDBTaskLoader extends AsyncTaskLoader<List<DBTag>>
{
    private final Context ctx;

    public TagsDBTaskLoader(Context context)
    {
        super(context);
        ctx = context;
    }

    @Override
    public List<DBTag> loadInBackground()
    {
        return ApplicationGlobals.getDBManager().getAllTags();
    }
}
