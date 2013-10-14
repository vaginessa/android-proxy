package com.lechucksoftware.proxy.proxysettings.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/09/13.
 */
public class DataSource
{
    // Database fields
    public static String TAG = DataSource.class.getSimpleName();
    private final Context context;

    private String[] allColumns = {
            DatabaseSQLiteOpenHelper.COLUMN_ID,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_CREATION_DATE,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_MODIFIED_DATE };

    public DataSource(Context ctx)
    {
        context = ctx;
    }

    public void close()
    {
        DatabaseSQLiteOpenHelper.getInstance(context).close();
    }

    public void resetDB()
    {
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        DatabaseSQLiteOpenHelper.getInstance(context).dropDB(database);
        DatabaseSQLiteOpenHelper.getInstance(context).createDB(database);
    }

    public ProxyData upsertProxy(ProxyData proxyData)
    {
        ProxyData persistedProxy = findProxy(proxyData);

        if (persistedProxy == null)
        {
            LogWrapper.d(TAG,"Insert new Proxy: " + proxyData);
            return createProxy(proxyData);
        }
        else
        {
            // Update
            LogWrapper.d(TAG,"Update Proxy: " + proxyData);
            return updateProxy(proxyData, persistedProxy);
        }
    }

    public ProxyData findProxy(ProxyData proxyData)
    {
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                       + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                       + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST + " =?"
                       + " AND " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT + "=?";

        Cursor cursor = database.rawQuery(query, new String[]{proxyData.host, Integer.toString(proxyData.port)});

        cursor.moveToFirst();
        ProxyData persisted = null;
        if (!cursor.isAfterLast())
        {
            persisted = cursorToProxy(cursor);
        }

        cursor.close();

        return persisted;
    }

    public ProxyData createProxy(ProxyData proxyData)
    {
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST, proxyData.host);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT,  proxyData.port);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, proxyData.exclusion);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_MODIFIED_DATE, currentDate);

        Long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_PROXIES, null, values);

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_PROXIES,
                allColumns, DatabaseSQLiteOpenHelper.COLUMN_ID + "=?", new String[]{insertId.toString()},
                null, null, null);

        cursor.moveToFirst();
        ProxyData newProxy = cursorToProxy(cursor);

        cursor.close();
        return newProxy;
    }

    public ProxyData updateProxy(ProxyData newData, ProxyData persistedProxy)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST, newData.host);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT,  newData.port);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION,  newData.exclusion);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_MODIFIED_DATE, currentDate);

        long insertId = database.update(DatabaseSQLiteOpenHelper.TABLE_PROXIES, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[] {persistedProxy.getId().toString()});

        Cursor updatedCursor = database.query(DatabaseSQLiteOpenHelper.TABLE_PROXIES,
                allColumns, DatabaseSQLiteOpenHelper.COLUMN_ID + "=?", new String[]{Long.toString(insertId)},
                null, null, null);

        updatedCursor.moveToFirst();
        ProxyData newProxy = cursorToProxy(updatedCursor);

        updatedCursor.close();
        return newProxy;
    }

    public void deleteProxy(ProxyData proxy)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        long id = proxy.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseSQLiteOpenHelper.TABLE_PROXIES, DatabaseSQLiteOpenHelper.COLUMN_ID + " = " + id, null);
    }

    public int getProxiesCount()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<ProxyData> proxies = new ArrayList<ProxyData>();

        String query = "SELECT COUNT(*)"
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES;

        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        int result = cursor.getInt(0);

        // Make sure to close the cursor
        cursor.close();

        return result;
    }

    public List<ProxyData> getAllProxies()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<ProxyData> proxies = new ArrayList<ProxyData>();

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_PROXIES, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            ProxyData proxy = cursorToProxy(cursor);
            proxies.add(proxy);
            cursor.moveToNext();
        }
        // Make sure to close the cursor

        cursor.close();
        return proxies;
    }

    private ProxyData cursorToProxy(Cursor cursor)
    {
        ProxyData proxy = new ProxyData();
        proxy.setId(cursor.getLong(0));
        proxy.host = cursor.getString(1);
        proxy.port = cursor.getInt(2);
        proxy.exclusion = cursor.getString(3);
        proxy.setCreationDate(cursor.getLong(4));
        proxy.setModifiedDate(cursor.getLong(5));

        proxy.isPersisted = true;

        return proxy;
    }

    private ProxyTag cursorToTag(Cursor cursor)
    {
        ProxyTag proxy = new ProxyTag();
        proxy.setId(cursor.getLong(0));
        proxy.tag = cursor.getString(1);
        proxy.tagColor = cursor.getInt(2);
        proxy.isPersisted = true;
        return proxy;
    }
}
