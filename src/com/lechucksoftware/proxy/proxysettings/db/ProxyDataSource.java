package com.lechucksoftware.proxy.proxysettings.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/09/13.
 */
public class ProxyDataSource
{

    // Database fields
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

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public ProxyData createProxy(String host, int port, String exclusion, String description)
    {
        ContentValues values = new ContentValues();
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_HOST, host);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_PORT, port);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, exclusion);
        values.put(ProxySQLiteOpenHelper.COLUMN_PROXY_DESCRIPTION, description);

        long insertId = database.insert(ProxySQLiteOpenHelper.TABLE_PROXIES, null, values);

        Cursor cursor = database.query(ProxySQLiteOpenHelper.TABLE_PROXIES,
                allColumns, ProxySQLiteOpenHelper.COLUMN_ID + " = " + insertId, null,
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

    public List<ProxyData> getAllComments()
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
