package com.lechucksoftware.proxy.proxysettings.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

/**
 * Created by Marco on 13/09/13.
 */
public class ProxySQLiteOpenHelper extends SQLiteOpenHelper
{
    public static final String TABLE_PROXIES = "proxies";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROXY_HOST = "host";
    public static final String COLUMN_PROXY_PORT = "port";
    public static final String COLUMN_PROXY_EXCLUSION = "exclusion";
    public static final String COLUMN_PROXY_DESCRIPTION = "description";
    public static final String COLUMN_PROXY_CREATION_DATE = "creationDate";
    public static final String COLUMN_PROXY_MODIFIED_DATE = "modifiedDate";

    public static final String DATABASE_NAME = "proxysettings.db";
    public static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PROXIES
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PROXY_HOST + " text not null,"
            + COLUMN_PROXY_PORT + " integer not null,"
            + COLUMN_PROXY_EXCLUSION + " text not null,"
            + COLUMN_PROXY_DESCRIPTION + " text,"
            + COLUMN_PROXY_CREATION_DATE + " integer not null,"
            + COLUMN_PROXY_MODIFIED_DATE + " integer not null"
            + ");";


    public ProxySQLiteOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        LogWrapper.w(ProxySQLiteOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROXIES);
        onCreate(db);
    }
}
