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
            ProxySQLiteOpenHelper.COLUMN_PROXY_CREATION_DATE,
            ProxySQLiteOpenHelper.COLUMN_PROXY_MODIFIED_DATE };

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
        ProxyData persistedProxy = findProxy(proxyData);

        if (persistedProxy == null)
        {
//            LogWrapper.d(TAG,"Insert new Proxy: " + proxyData);
            return createProxy(proxyData);
        }
        else
        {
            // Update
//            LogWrapper.d(TAG,"Update Proxy: " + proxyData);
            return updateProxy(proxyData, persistedProxy);
        }
    }

    public ProxyData findProxy(ProxyData proxyData)
    {
        String query = "SELECT * "
                       + " FROM " + ProxySQLiteOpenHelper.TABLE_PROXIES
                       + " WHERE " + ProxySQLiteOpenHelper.COLUMN_PROXY_HOST + " =?"
                       + " AND " + ProxySQLiteOpenHelper.COLUMN_PROXY_PORT + "=?";

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
        ContentValues values = new ContentValues();
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_HOST, proxyData.host);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_PORT,  proxyData.port);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, proxyData.exclusion);

        if (proxyData.description == null || proxyData.description.equals(""))
            proxyData.description = ProxyData.getAutomaticDescription(proxyData);

        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_DESCRIPTION, proxyData.description);

        long currentDate = System.currentTimeMillis();
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_CREATION_DATE, currentDate);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_MODIFIED_DATE, currentDate);

        Long insertId = database.insert(ProxySQLiteOpenHelper.TABLE_PROXIES, null, values);

        Cursor cursor = database.query(ProxySQLiteOpenHelper.TABLE_PROXIES,
                allColumns, ProxySQLiteOpenHelper.COLUMN_ID + "=?", new String[]{insertId.toString()},
                null, null, null);

        cursor.moveToFirst();
        ProxyData newProxy = cursorToProxy(cursor);
        cursor.close();
        return newProxy;
    }

    public ProxyData updateProxy(ProxyData newData, ProxyData persistedProxy)
    {
        ContentValues values = new ContentValues();
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_HOST, newData.host);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_PORT,  newData.port);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_EXCLUSION,  newData.exclusion);

        if (newData.description != null)
        {
            values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_DESCRIPTION,  newData.description);
        }

        long currentDate = System.currentTimeMillis();
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_MODIFIED_DATE, currentDate);

        long insertId = database.update(ProxySQLiteOpenHelper.TABLE_PROXIES, values, ProxySQLiteOpenHelper.COLUMN_ID + " =?", new String[] {persistedProxy.getId().toString()});

        Cursor updatedCursor = database.query(ProxySQLiteOpenHelper.TABLE_PROXIES,
                allColumns, ProxySQLiteOpenHelper.COLUMN_ID + "=?", new String[]{Long.toString(insertId)},
                null, null, null);

        updatedCursor.moveToFirst();
        ProxyData newProxy = cursorToProxy(updatedCursor);
        updatedCursor.close();
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
        proxy.setCreationDate(cursor.getLong(5));
        proxy.setModifiedDate(cursor.getLong(6));

        proxy.isPersisted = true;

        return proxy;
    }
}
