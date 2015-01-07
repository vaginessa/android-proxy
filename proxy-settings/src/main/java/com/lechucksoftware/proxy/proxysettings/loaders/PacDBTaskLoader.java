package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.db.PacEntity;
import com.lechucksoftware.proxy.proxysettings.db.ProxyEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by marco on 04/10/13.
 */
public class PacDBTaskLoader extends AsyncTaskLoader<List<PacEntity>>
{
    private final Context ctx;

    public PacDBTaskLoader(Context context)
    {
        super(context);
        ctx = context;
    }

    @Override
    public List<PacEntity> loadInBackground()
    {
        Map<Long, PacEntity> savedPac = App.getDBManager().getAllPac();
        List<PacEntity> pacEntityList = new ArrayList<PacEntity>(savedPac.values());
        Collections.sort(pacEntityList);
        return pacEntityList;
    }
}
