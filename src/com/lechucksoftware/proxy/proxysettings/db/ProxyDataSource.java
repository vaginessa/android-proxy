package com.lechucksoftware.proxy.proxysettings.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/09/13.
 */
public class ProxyDataSource
{
    // Database fields
    public static String TAG = ProxyDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ProxySQLiteOpenHelper dbHelper;
    private String[] allColumns = {
            ProxySQLiteOpenHelper.COLUMN_ID,
            ProxySQLiteOpenHelper.COLUMN_PROXY_HOST,
            ProxySQLiteOpenHelper.COLUMN_PROXY_PORT,
            ProxySQLiteOpenHelper.COLUMN_PROXY_EXCLUSION,
            ProxySQLiteOpenHelper.COLUMN_PROXY_DESCRIPTION,
    };

    public ProxyDataSource(Context context)
    {
        dbHelper = new ProxySQLiteOpenHelper(context);
    }

    public void openWritable() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void openReadable() throws SQLException
    {
        database = dbHelper.getReadableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public ProxyData upsertProxy(ProxyData proxyData)
    {
        String query = "SELECT " + ProxySQLiteOpenHelper.COLUMN_ID
                       + " FROM " + ProxySQLiteOpenHelper.TABLE_PROXIES
                       + " WHERE " + ProxySQLiteOpenHelper.COLUMN_PROXY_HOST + " =?"
                       + " AND " + ProxySQLiteOpenHelper.COLUMN_PROXY_PORT + "=?";

        Cursor cursor = database.rawQuery(query, new String[]{proxyData.host, Integer.toString(proxyData.port)});
//        Cursor cursor = database.query(ProxySQLiteOpenHelper.TABLE_PROXIES, allColumns, )

        cursor.moveToFirst();
        if (cursor.isAfterLast())
        {
            // Insert
            LogWrapper.d(TAG,"Insert new Proxy: " + proxyData);
            createProxy(proxyData);
        }
        else
        {
            // Update
            long proxyId = cursor.getLong(0);
            LogWrapper.d(TAG,"Update Proxy: " + proxyData);
            updateProxy(proxyData, proxyId);
        }
        cursor.close();
        return null;
    }

    public ProxyData createProxy(ProxyData proxyData)
    {
        ContentValues values = new ContentValues();
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_HOST, proxyData.host);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_PORT,  proxyData.port);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, proxyData.exclusion);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_DESCRIPTION, proxyData.description);

        Long insertId = database.insert(ProxySQLiteOpenHelper.TABLE_PROXIES, null, values);

        Cursor cursor = database.query(ProxySQLiteOpenHelper.TABLE_PROXIES,
                allColumns, ProxySQLiteOpenHelper.COLUMN_ID + "=?", new String[]{insertId.toString()},
                null, null, null);

        cursor.moveToFirst();
        ProxyData newProxy = cursorToProxy(cursor);
        cursor.close();
        return newProxy;
    }

    public ProxyData updateProxy(ProxyData proxyData, Long proxyId)
    {
        ContentValues values = new ContentValues();
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_HOST, proxyData.host);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_PORT,  proxyData.port);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_EXCLUSION,  proxyData.exclusion);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_DESCRIPTION,  proxyData.description);

        long insertId = database.update(ProxySQLiteOpenHelper.TABLE_PROXIES, values, ProxySQLiteOpenHelper.COLUMN_ID + " =?", new String[] {proxyId.toString()});

        Cursor cursor = database.query(ProxySQLiteOpenHelper.TABLE_PROXIES,
                allColumns, ProxySQLiteOpenHelper.COLUMN_ID + "=?", new String[]{Long.toString(insertId)},
                null, null, null);
        cursor.moveToFirst();
        ProxyData newProxy = cursorToProxy(cursor);
        cursor.close();
        return newProxy;
    }

    public void deleteProxy(ProxyData proxy)
    {
        long id = proxy.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(ProxySQLiteOpenHelper.TABLE_PROXIES, ProxySQLiteOpenHelper.COLUMN_ID + " = " + id, null);
    }

    public List<ProxyData> getAllProxies()
    {
        List<ProxyData> proxies = new ArrayList<ProxyData>();

        Cursor cursor = database.query(ProxySQLiteOpenHelper.TABLE_PROXIES, allColumns, null, null, null, null, null);

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
        proxy.description = cursor.getString(4);
        return proxy;
    }
}
