package com.lechucksoftware.proxy.proxysettings.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.utils.DBUtils;

import timber.log.Timber;

/**
 * Created by Marco on 13/09/13.
 */
public class DatabaseSQLiteOpenHelper extends SQLiteOpenHelper
{
    private static final String TAG = DatabaseSQLiteOpenHelper.class.getSimpleName();

    public static final String TABLE_WIFI_AP = "wifiap";
    public static final String TABLE_PAC = "pac";
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
    public static final String COLUMN_WIFI_PAC_ID = "pacid";

    public static final String COLUMN_PAC_URL_FILE = "pacUrlFile";
    public static final String COLUMN_PAC_IN_USE = "pacUsed";

    public static final String DATABASE_NAME = "proxysettings.db";
    public static final int DATABASE_VERSION = 4;

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

    public static final String [] TABLE_PROXIES_COLUMNS = new String[] {
            TABLE_PROXIES + "." + COLUMN_ID,
            TABLE_PROXIES + "." + COLUMN_PROXY_HOST,
            TABLE_PROXIES + "." + COLUMN_PROXY_PORT,
            TABLE_PROXIES + "." + COLUMN_PROXY_EXCLUSION,
            TABLE_PROXIES + "." + COLUMN_PROXY_COUNTRY_CODE,
            TABLE_PROXIES + "." + COLUMN_PROXY_IN_USE,
            TABLE_PROXIES + "." + COLUMN_CREATION_DATE,
            TABLE_PROXIES + "." + COLUMN_MODIFIED_DATE};

    public static final String TABLE_PROXIES_COLUMNS_STRING = TextUtils.join(", ", TABLE_PROXIES_COLUMNS);

    private static final String CREATE_TABLE_TAGS = "create table "
            + TABLE_TAGS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TAG + " text not null, "
            + COLUMN_TAG_COLOR + " integer not null, "
            + COLUMN_CREATION_DATE + " integer not null, "
            + COLUMN_MODIFIED_DATE + " integer not null"
            + ");";

    public static final String [] TABLE_TAGS_COLUMNS = new String[] {
            TABLE_TAGS + "." + COLUMN_ID,
            TABLE_TAGS + "." + COLUMN_TAG,
            TABLE_TAGS + "." + COLUMN_TAG_COLOR,
            TABLE_TAGS + "." + COLUMN_CREATION_DATE,
            TABLE_TAGS + "." + COLUMN_MODIFIED_DATE};

    public static final String TABLE_TAGS_COLUMNS_STRING = TextUtils.join(", ", TABLE_TAGS_COLUMNS);

    private static final String CREATE_TABLE_TAGGED_PROXIES = "create table "
            + TABLE_PROXY_TAG_LINKS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PROXY_ID + " integer not null, "
            + COLUMN_TAG_ID + " integer not null, "
            + COLUMN_CREATION_DATE + " integer not null, "
            + COLUMN_MODIFIED_DATE + " integer not null"
            + ");";

    public static final String [] TABLE_TAGGED_PROXIES_COLUMNS = new String[] {
            TABLE_PROXY_TAG_LINKS + "." + COLUMN_ID,
            TABLE_PROXY_TAG_LINKS + "." + COLUMN_PROXY_ID,
            TABLE_PROXY_TAG_LINKS + "." + COLUMN_TAG_ID,
            TABLE_PROXY_TAG_LINKS + "." + COLUMN_CREATION_DATE,
            TABLE_PROXY_TAG_LINKS + "." + COLUMN_MODIFIED_DATE};

    public static final String TABLE_TAGGED_PROXIES_COLUMNS_STRING = TextUtils.join(", ", TABLE_TAGGED_PROXIES_COLUMNS);

    private static final String CREATE_TABLE_WIFI_AP = "create table "
            + TABLE_WIFI_AP
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_WIFI_SSID + " text not null, "
            + COLUMN_WIFI_SECURITY_TYPE + " text not null, "
            + COLUMN_WIFI_PROXY_SETTING + " text not null, "
            + COLUMN_WIFI_PROXY_ID + " integer not null, "
            + COLUMN_WIFI_PAC_ID + " integer not null, "
            + COLUMN_CREATION_DATE + " integer not null, "
            + COLUMN_MODIFIED_DATE + " integer not null"
            + ");";

    public static final String [] TABLE_WIFI_AP_COLUMNS = new String[] {
            TABLE_WIFI_AP + "." + COLUMN_ID,
            TABLE_WIFI_AP + "." + COLUMN_WIFI_SSID,
            TABLE_WIFI_AP + "." + COLUMN_WIFI_SECURITY_TYPE,
            TABLE_WIFI_AP + "." + COLUMN_WIFI_PROXY_SETTING,
            TABLE_WIFI_AP + "." + COLUMN_WIFI_PROXY_ID,
            TABLE_WIFI_AP + "." + COLUMN_WIFI_PAC_ID,
            TABLE_WIFI_AP + "." + COLUMN_CREATION_DATE,
            TABLE_WIFI_AP + "." + COLUMN_MODIFIED_DATE};

    public static final String TABLE_WIFI_AP_COLUMNS_STRING = TextUtils.join(", ", TABLE_WIFI_AP_COLUMNS);

    private static final String CREATE_TABLE_PAC = "create table "
            + TABLE_PAC
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PAC_URL_FILE + " text not null, "
            + COLUMN_PAC_IN_USE + " integer not null, "
            + COLUMN_CREATION_DATE + " integer not null, "
            + COLUMN_MODIFIED_DATE + " integer not null"
            + ");";

    public static final String [] TABLE_PAC_COLUMNS = new String[] {
            TABLE_PAC + "." + COLUMN_ID,
            TABLE_PAC + "." + COLUMN_PAC_URL_FILE,
            TABLE_PAC + "." + COLUMN_PAC_IN_USE,
            TABLE_PAC + "." + COLUMN_CREATION_DATE,
            TABLE_PAC + "." + COLUMN_MODIFIED_DATE};

    public static final String TABLE_PAC_COLUMNS_STRING = TextUtils.join(", ", TABLE_PAC_COLUMNS);

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
            return;
        }

        // Se example of upgrade planning here: http://grepcode.com/file_/repository.grepcode.com/java/ext/com.google.android/android-apps/4.0.1_r1/com/android/providers/calendar/CalendarDatabaseHelper.java/?v=source

        if (oldVersion == 2)
        {
            // Do something for v3
            upgradeToVersion3(db);
            oldVersion = 3; // Remember to increment so that next upgrade phase is called
        }

        if (oldVersion == 3)
        {
            // Do something for v3
            upgradeToVersion4(db);
            oldVersion = 4;
        }

//
//        if (oldVersion == 4)
//        {
//            // Do something for v4
//        }
    }

    public void upgradeToVersion3(SQLiteDatabase db)
    {
        /**
         * Changes from version 2 to version 3:
         *
         * - Added TABLE_WIFI_AP (Wi-Fi access points table)
         * */
        DBUtils.execSQL(db, CREATE_TABLE_WIFI_AP);
    }

    public void upgradeToVersion4(SQLiteDatabase db)
    {
        /**
         * Changes from version 3 to version 4:
         *
         * - Added PACId column to TABLE_WIFI_AP
         * - Added TABLE_PAC (Proxy PAC configurations)
         * */

        DBUtils.execSQL(db, "ALTER TABLE " + TABLE_WIFI_AP + " ADD COLUMN " + COLUMN_WIFI_PAC_ID + " int");
        DBUtils.execSQL(db, CREATE_TABLE_PAC);
    }

    public void createDB(SQLiteDatabase db)
    {
        App.getTraceUtils().startTrace(TAG, "CREATE DATABASE", Log.DEBUG);

        DBUtils.execSQL(db, CREATE_TABLE_PROXIES);
        DBUtils.execSQL(db, CREATE_TABLE_TAGS);
        DBUtils.execSQL(db, CREATE_TABLE_TAGGED_PROXIES);
        DBUtils.execSQL(db, CREATE_TABLE_WIFI_AP);
        DBUtils.execSQL(db, CREATE_TABLE_PAC);

        App.getTraceUtils().stopTrace(TAG, "CREATE DATABASE", Log.DEBUG);
    }

    public void dropDB(SQLiteDatabase db)
    {
        App.getTraceUtils().startTrace(TAG, "DROP DATABASE", Log.DEBUG);

        DBUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_PROXIES);
        DBUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_TAGS);
        DBUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_PROXY_TAG_LINKS);
        DBUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_WIFI_AP);
        DBUtils.execSQL(db, "DROP TABLE IF EXISTS " + TABLE_PAC);

        App.getTraceUtils().stopTrace(TAG, "DROP DATABASE", Log.DEBUG);
    }
}
