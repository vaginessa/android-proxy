package com.lechucksoftware.proxy.proxysettings.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.lechucksoftware.proxy.proxysettings.constants.Constants;
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

    private String[] proxyTableColumns = {
            DatabaseSQLiteOpenHelper.COLUMN_ID,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION,
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE,
            DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE,
            DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE};

    private String[] tagsTableColumns = {
            DatabaseSQLiteOpenHelper.COLUMN_ID,
            DatabaseSQLiteOpenHelper.COLUMN_TAG,
            DatabaseSQLiteOpenHelper.COLUMN_TAG_COLOR,
            DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE,
            DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE};

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

    public DBProxy upsertProxy(DBProxy proxyData)
    {
        long proxyId = findProxy(proxyData);

        if (proxyId == -1)
        {
//            LogWrapper.d(TAG,"Insert new Proxy: " + proxyData);
            return createProxy(proxyData);
        }
        else
        {
            // Update
//            LogWrapper.d(TAG,"Update Proxy: " + proxyData);
            return updateProxy(proxyId, proxyData);
        }
    }

    public DBTag upsertTag(DBTag tag)
    {
        long tagId = findTag(tag.tag);

        if (tagId == -1)
        {
//            LogWrapper.d(TAG,"Insert new TAG: " + tag);
            return createTag(tag);
        }
        else
        {
            // Update
//            LogWrapper.d(TAG,"Update TAG: " + tag);
            return updateTag(tagId, tag);
        }
    }

    public DBProxy getRandomProxy()
    {
        LogWrapper.startTrace(TAG,"getRandomProxy", Log.INFO);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                        + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                        + " ORDER BY Random() LIMIT 1";

        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        DBProxy proxyData = null;
        if (!cursor.isAfterLast())
        {
            proxyData = cursorToProxy(cursor);
        }

        cursor.close();

        proxyData.tags = getTagsForProxy(proxyData.getId());
        LogWrapper.stopTrace(TAG, "getRandomProxy", proxyData.toString(), Log.INFO);
        return proxyData;
    }

    public DBProxy getProxy(long proxyId)
    {
        LogWrapper.startTrace(TAG,"getProxy", Log.INFO);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(proxyId)});
        cursor.moveToFirst();
        DBProxy proxyData = null;
        if (!cursor.isAfterLast())
        {
            proxyData = cursorToProxy(cursor);
        }

        cursor.close();

        proxyData.tags = getTagsForProxy(proxyId);
        LogWrapper.stopTrace(TAG, "getProxy", proxyData.toString(), Log.INFO);
        return proxyData;
    }

    public DBTag getRandomTag()
    {
        LogWrapper.startTrace(TAG,"getTag", Log.INFO);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                        + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS
                        + " ORDER BY Random() LIMIT 1";

        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        DBTag tag = null;
        if (!cursor.isAfterLast())
        {
            tag = cursorToTag(cursor);
        }

        cursor.close();
        LogWrapper.stopTrace(TAG,"getTag", tag.toString(), Log.INFO);
        return tag;
    }

    public DBTag getTag(long tagId)
    {
        LogWrapper.startTrace(TAG,"getTag", Log.INFO);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(tagId)});
        cursor.moveToFirst();
        DBTag tag = null;
        if (!cursor.isAfterLast())
        {
            tag = cursorToTag(cursor);
        }

        cursor.close();
        LogWrapper.stopTrace(TAG,"getTag", tag.toString(), Log.INFO);
        return tag;
    }

    public DBProxyTagLink getProxyTagLink(long linkId)
    {
        LogWrapper.startTrace(TAG,"getProxyTagLink", Log.INFO);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                        + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS
                        + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(linkId)});
        cursor.moveToFirst();
        DBProxyTagLink link = null;
        if (!cursor.isAfterLast())
        {
            link = cursorToProxyTagLink(cursor);
        }

        cursor.close();
        LogWrapper.stopTrace(TAG, "getProxyTagLink", link.toString(), Log.INFO);
        return link;
    }

    public long findProxy(DBProxy proxyData)
    {
        LogWrapper.startTrace(TAG,"findProxy", Log.ASSERT);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                       + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                       + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST + " =?"
                       + " AND " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT + "=?";

        Cursor cursor = database.rawQuery(query, new String[]{proxyData.host, Integer.toString(proxyData.port)});

        cursor.moveToFirst();
        long proxyId = -1;
        if (!cursor.isAfterLast())
        {
            proxyId = cursor.getLong(0);
        }

        cursor.close();
        LogWrapper.stopTrace(TAG, "findProxy", Log.ASSERT);
        return proxyId;
    }

    public long findTag(String tagName)
    {
        LogWrapper.startTrace(TAG,"findTag", Log.ASSERT);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_TAG + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{tagName});

        cursor.moveToFirst();
        long tagId = -1;
        if (!cursor.isAfterLast())
        {
            tagId = cursor.getLong(0);
        }

        cursor.close();
        LogWrapper.stopTrace(TAG, "findTag", Log.ASSERT);
        return tagId;
    }

    public DBProxy createProxy(DBProxy proxyData)
    {
        LogWrapper.startTrace(TAG,"createProxy", Log.DEBUG, true);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST, proxyData.host);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT,  proxyData.port);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, proxyData.exclusion);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE, proxyData.getCountryCode());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_PROXIES, null, values);
        DBProxy newProxy = getProxy(insertId);

        // Update or add all the TAGS listed into the DBProxy object
        for(DBTag tag:proxyData.tags)
        {
            DBTag updatedTag = upsertTag(tag);
            createProxyTagLink(newProxy.getId(), updatedTag.getId());
        }

        LogWrapper.stopTrace(TAG, "createProxy", Log.DEBUG);

        context.sendBroadcast(new Intent(Constants.PROXY_SAVED));

        return newProxy;
    }

    public DBTag createTag(DBTag tag)
    {
        LogWrapper.startTrace(TAG,"createTag", Log.DEBUG);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG, tag.tag);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_COLOR,  tag.tagColor);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_TAGS, null, values);

        DBTag newTag = getTag(insertId);
        LogWrapper.stopTrace(TAG, "createTag", Log.DEBUG);
        return newTag;
    }

    public DBProxyTagLink createProxyTagLink(long proxyId, long tagId)
    {
        LogWrapper.startTrace(TAG,"createProxyTagLink", Log.DEBUG);
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_ID, proxyId);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_ID, tagId);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS, null, values);

        DBProxyTagLink newTag = getProxyTagLink(insertId);
        LogWrapper.stopTrace(TAG, "createProxyTagLink", Log.DEBUG);
        return newTag;
    }

    public DBProxy updateProxy(long proxyId, DBProxy newData)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST, newData.host);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT,  newData.port);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION,  newData.exclusion);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE,  newData.getCountryCode());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long updatedId = database.update(DatabaseSQLiteOpenHelper.TABLE_PROXIES, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[] {String.valueOf(proxyId)});

        DBProxy updatedProxy = getProxy(updatedId);

        context.sendBroadcast(new Intent(Constants.PROXY_SAVED));

        return updatedProxy;
    }

    public DBTag updateTag(long tagId, DBTag newData)
    {
        DBTag persistedTag = getTag(tagId);

        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG, newData.tag);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_COLOR,  newData.tagColor);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long updateId = database.update(DatabaseSQLiteOpenHelper.TABLE_TAGS, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[] {persistedTag.getId().toString()});

        DBTag updatedTag = getTag(updateId);
        return updatedTag;
    }

    public void deleteProxy(long proxyId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        System.out.println("Comment deleted with id: " + proxyId);
        database.delete(DatabaseSQLiteOpenHelper.TABLE_PROXIES, DatabaseSQLiteOpenHelper.COLUMN_ID + " = " + proxyId, null);
    }

    public void deleteTag(long tagId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        System.out.println("Comment deleted with id: " + tagId);
        database.delete(DatabaseSQLiteOpenHelper.TABLE_TAGS, DatabaseSQLiteOpenHelper.COLUMN_ID + " = " + tagId, null);
    }

    public long getProxiesCount()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT COUNT(*)"
                        + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES;

        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);

        // Make sure to close the cursor
        cursor.close();

        return result;
    }

    public long getTagsCount()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT COUNT(*)"
                        + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS;

        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);

        // Make sure to close the cursor
        cursor.close();

        return result;
    }

    public List<DBProxy> getAllProxiesWithTAGs()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<DBProxy> proxies = new ArrayList<DBProxy>();

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_PROXIES, proxyTableColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            DBProxy proxy = cursorToProxy(cursor);
            proxies.add(proxy);
            cursor.moveToNext();
        }
        cursor.close();

        for(DBProxy proxy:proxies)
        {
            proxy.tags = getTagsForProxy(proxy.getId());
        }

        return proxies;
    }

    public List<DBProxy> getProxyWithEmptyCountryCode()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<DBProxy> proxies = new ArrayList<DBProxy>();

        String query = "SELECT *"
                        + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                        + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{""});

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            DBProxy proxy = cursorToProxy(cursor);
            proxies.add(proxy);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();

        return proxies;
    }

    public List<DBTag> getAllTags()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<DBTag> proxies = new ArrayList<DBTag>();

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_TAGS, tagsTableColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            DBTag proxy = cursorToTag(cursor);
            proxies.add(proxy);
            cursor.moveToNext();
        }
        cursor.close();

        return proxies;
    }

    public List<DBTag> getTagsForProxy(long proxyId)
    {
        LogWrapper.startTrace(TAG, "getTagsForProxy", Log.DEBUG);
        List<DBProxyTagLink> links = getProxyTagLinkForProxy(proxyId);
        List<DBTag> tags = new ArrayList<DBTag>();
        for(DBProxyTagLink link:links)
        {
            tags.add(getTag(link.tagId));
        }

        LogWrapper.stopTrace(TAG, "getTagsForProxy", String.valueOf(proxyId), Log.DEBUG);
        return tags;
    }

    private List<DBProxyTagLink> getProxyTagLinkForProxy(long proxyId)
    {
        SQLiteDatabase database =  DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT *"
                        + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS
                        + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_ID + " =?";

        Cursor cursor = database.rawQuery(query, new String [] {String.valueOf(proxyId)});
        cursor.moveToFirst();

        List<DBProxyTagLink> links = new ArrayList<DBProxyTagLink>();

        while (!cursor.isAfterLast())
        {
            DBProxyTagLink link  = cursorToProxyTagLink(cursor);
            links.add(link);
            cursor.moveToNext();
        }

        cursor.close();

        return links;
    }

    private DBProxyTagLink cursorToProxyTagLink(Cursor cursor)
    {
        DBProxyTagLink link = new DBProxyTagLink();
        link.setId(cursor.getLong(0));
        link.proxyId = cursor.getLong(1);
        link.tagId = cursor.getLong(2);

        link.setCreationDate(cursor.getLong(3));
        link.setModifiedDate(cursor.getLong(4));

        link.isPersisted = true;

        return link;
    }

    private DBProxy cursorToProxy(Cursor cursor)
    {
        DBProxy proxy = new DBProxy();
        proxy.setId(cursor.getLong(0));
        proxy.host = cursor.getString(1);
        proxy.port = cursor.getInt(2);
        proxy.exclusion = cursor.getString(3);
        proxy.setCountryCode(cursor.getString(4));
        proxy.setCreationDate(cursor.getLong(5));
        proxy.setModifiedDate(cursor.getLong(6));

        proxy.isPersisted = true;

        return proxy;
    }

    private DBTag cursorToTag(Cursor cursor)
    {
        DBTag tag = new DBTag();
        tag.setId(cursor.getLong(0));
        tag.tag = cursor.getString(1);
        tag.tagColor = cursor.getInt(2);
        tag.setCreationDate(cursor.getLong(3));
        tag.setModifiedDate(cursor.getLong(4));

        tag.isPersisted = true;

        return tag;
    }
}
