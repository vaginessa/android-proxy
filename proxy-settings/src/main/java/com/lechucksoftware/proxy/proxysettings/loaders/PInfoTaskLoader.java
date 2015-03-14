package com.lechucksoftware.proxy.proxysettings.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.lechucksoftware.proxy.proxysettings.feedbackutils.PInfo;
import com.lechucksoftware.proxy.proxysettings.feedbackutils.PackagesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 04/10/13.
 */
public class PInfoTaskLoader extends AsyncTaskLoader<List<PInfo>>
{
    private final Context ctx;

    public PInfoTaskLoader(Context context)
    {
        super(context);
        ctx = context;
    }

    @Override
    public List<PInfo> loadInBackground()
    {
        return (ArrayList<PInfo>) PackagesUtils.getPackages(ctx);
    }
}
