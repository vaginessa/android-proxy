package com.lechucksoftware.proxy.proxysettings.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

/**
 * Created by Marco on 13/09/13.
 */
public class DatabaseSQLiteOpenHelper extends SQLiteOpenHelper
{
    public static final String TABLE_PROXIES = "proxies";
    public static final String TABLE_TAGS = "tags";
    public static final String TABLE_TAGGEDPROXIES = "taggedproxies";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROXY_HOST = "host";
    public static final String COLUMN_PROXY_PORT = "port";
    public static final String COLUMN_PROXY_EXCLUSION = "exclusion";
    public static final String COLUMN_PROXY_CREATION_DATE = "creationDate";
    public static final String COLUMN_PROXY_MODIFIED_DATE = "modifiedDate";

    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_TAG_COLOR = "color";

    public static final String COLUMN_PROXY_ID = "proxyId";
    public static final String COLUMN_TAG_ID = "tagId";

    public static final String DATABASE_NAME = "proxysettings.db";
    public static final int DATABASE_VERSION = 2;

    // Database creation sql statement

    private static final String CREATE_TABLE_PROXIES = "create table "
            + TABLE_PROXIES
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PROXY_HOST + " text not null,"
            + COLUMN_PROXY_PORT + " integer not null,"
            + COLUMN_PROXY_EXCLUSION + " text not null,"
            + COLUMN_PROXY_CREATION_DATE + " integer not null,"
            + COLUMN_PROXY_MODIFIED_DATE + " integer not null"
            + ");";

    private static final String CREATE_TABLE_TAGS = "create table "
            + TABLE_TAGS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TAG + " text not null,"
            + COLUMN_TAG_COLOR + " integer not null"
            + ");";

    private static final String CREATE_TABLE_TAGGED_PROXIES = "create table "
            + TABLE_TAGGEDPROXIES
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TAG_ID + " integer not null,"
            + COLUMN_PROXY_ID + " integer not null"
            + ");";

    private static DatabaseSQLiteOpenHelper instance;


    public static synchronized DatabaseSQLiteOpenHelper getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseSQLiteOpenHelper(context);
        }

        return instance;
    }

    private DatabaseSQLiteOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        createDB(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        LogWrapper.w(DatabaseSQLiteOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        dropDB(db);
        createDB(db);
    }

    public void createDB(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_PROXIES);
        db.execSQL(CREATE_TABLE_TAGS);
        db.execSQL(CREATE_TABLE_TAGGED_PROXIES);
    }

    public void dropDB(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROXIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGGEDPROXIES);
    }
}
