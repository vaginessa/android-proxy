package com.lechucksoftware.proxy.proxysettings.utils;

import android.database.sqlite.SQLiteDatabase;

import com.lechucksoftware.proxy.proxysettings.App;


/**
 * Created by Marco on 17/08/14.
 */
public class DatabaseUtils
{
    private static final String TAG = DatabaseUtils.class.getSimpleName();

    public static void execSQL(SQLiteDatabase db, String sql)
    {
        try
        {
            App.getLogger().d(TAG, "EXEC SQL: " + sql);
            db.execSQL(sql);
        }
        catch (Exception e)
        {
            App.getEventsReporter().sendException(e);
        }
    }
}
