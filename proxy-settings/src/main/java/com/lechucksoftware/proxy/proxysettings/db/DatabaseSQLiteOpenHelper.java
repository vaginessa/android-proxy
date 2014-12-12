package com.lechucksoftware.proxy.proxysettings.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.utils.DatabaseUtils;

import timber.log.Timber;

/**
 * Created by Marco on 13/09/13.
 */
public class DatabaseSQLiteOpenHelper extends SQLiteOpenHelper
{
    public static final String TABLE_WIFI_AP = "wifiap";
    public static final String TABLE_PROXIES = "proxies";
    public static final String TABLE_TAGS = "tags";
    public static final String TABLE_PROXY_TAG_LINKS = "taggedproxies";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREATION_DATE = "creationDate";
    public static final String COLUMN_MODIFIED_DATE = "modifiedDate";

    public static final String COLUMN_PROXY_HOST = "host";
    public static final String COLUMN_PROXY_PORT = "port";
    public static final String COLUMN_PROXY_EXCLUSION = "exclusion";
    public static final String COLUMN_PROXY_COUNTRY_CODE = "country";
    public static final String COLUMN_PROXY_IN_USE = "used";

    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_TAG_COLOR = "color";

    public static final String COLUMN_PROXY_ID = "proxyId";
    public static final String COLUMN_TAG_ID = "tagId";

    public static final String COLUMN_WIFI_SSID = "ssid";
    public static final String COLUMN_WIFI_SECURITY_TYPE = "securitytype";
    public static final String COLUMN_WIFI_PROXY_SETTING = "proxysetting";
    public static final String COLUMN_WIFI_PROXY_ID = "proxyid";

    public static final String DATABASE_NAME = "proxysettings.db";
    public static final int DATABASE_VERSION = 3;

    // Database creation sql statement

    private static final String CREATE_TABLE_PROXIES = "create table "
            + TABLE_PROXIES
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PROXY_HOST + " text not null, "
            + COLUMN_PROXY_PORT + " integer not null, "
            + COLUMN_PROXY_EXCLUSION + " text not null, "
            + COLUMN_PROXY_COUNTRY_CODE + " text, "
            + COLUMN_PROXY_IN_USE + " integer not null, "
            + COLUMN_CREATION_DATE + " integer not null, "
            + COLUMN_MODIFIED_DATE + " integer not null"
            + ");";

    private static final String CREATE_TABLE_TAGS = "create table "
            + TABLE_TAGS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TAG + " text not null, "
            + COLUMN_TAG_COLOR + " integer not null, "
            + COLUMN_CREATION_DATE + " integer not null, "
            + COLUMN_MODIFIED_DATE + " integer not null"
            + ");";

    private static final String CREATE_TABLE_TAGGED_PROXIES = "create table "
            + TABLE_PROXY_TAG_LINKS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PROXY_ID + " integer not null, "
            + COLUMN_TAG_ID + " integer not null, "
            + COLUMN_CREATION_DATE + " integer not null, "
            + COLUMN_MODIFIED_DATE + " integer not null"
            + ");";

    private static final String TAG = DatabaseSQLiteOpenHelper.class.getSimpleName();
    private static final String CREATE_TABLE_WIFI_AP = "create table "
            + TABLE_WIFI_AP
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_WIFI_SSID + " text not null, "
            + COLUMN_WIFI_SECURITY_TYPE + " text not null, "
            + COLUMN_WIFI_PROXY_SETTING + " text not null, "
            + COLUMN_WIFI_PROXY_ID + " integer not null, "
            + COLUMN_CREATION_DATE + " integer not null, "
            + COLUMN_MODIFIED_DATE + " integer not null"
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
        Timber.d("DB - onUpgrade: %d -> %d", oldVersion, newVersion);

        if (oldVersion < 2)
        {
            /**
             * First released version is v2
             * - previous versions doesn't need official upgrade plan
             * */

            dropDB(db);
            createDB(db);
        }

        // Se example of upgrade planning here: http://grepcode.com/file_/repository.grepcode.com/java/ext/com.google.android/android-apps/4.0.1_r1/com/android/providers/calendar/CalendarDatabaseHelper.java/?v=source
        if (oldVersion == 2)
        {
            // Do something for v3
            upgradeToVersion3(db);
        }
//
//        if (oldVersion == 4)
//        {
//            // Do something for v4
//        }
    }

    private void upgradeToVersion3(SQLiteDatabase db)
    {
        /**
         * Changes from version 2 to version 3:
         *
         * - Added TABLE_WIFI_AP (Wi-Fi access points table)
         * */
        db.execSQL(CREATE_TABLE_WIFI_AP);
     }

    public void createDB(SQLiteDatabase db)
    {
        App.getLogutils().startTrace(TAG, "CREATE DATABASE", Log.DEBUG);

        DatabaseUtils.execSQL(db, CREATE_TABLE_PROXIES);
        DatabaseUtils.execSQL(db, CREATE_TABLE_TAGS);
        DatabaseUtils.execSQL(db, CREATE_TABLE_TAGGED_PROXIES);
        DatabaseUtils.execSQL(db, CREATE_TABLE_WIFI_AP);

        App.getLogutils().stopTrace(TAG, "CREATE DATABASE", Log.DEBUG);
    }

    public void dropDB(SQLiteDatabase db)
    {
        App.getLogutils().startTrace(TAG, "DROP DATABASE", Log.DEBUG);

        DatabaseUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_PROXIES);
        DatabaseUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_TAGS);
        DatabaseUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_PROXY_TAG_LINKS);
        DatabaseUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_WIFI_AP);

        App.getLogutils().stopTrace(TAG, "DROP DATABASE", Log.DEBUG);
    }
}
