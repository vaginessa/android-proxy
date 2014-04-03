package com.lechucksoftware.proxy.proxysettings.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.utils.EventReportingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.shouldit.proxy.lib.ProxyConfiguration;

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
            DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE,
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
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        DatabaseSQLiteOpenHelper.getInstance(context).dropDB(database);
        DatabaseSQLiteOpenHelper.getInstance(context).createDB(database);

        notifyDBReset();
    }

    public ProxyEntity upsertProxy(ProxyEntity proxyData)
    {
        long proxyId = -1;
        if (proxyData.isPersisted)
        {
            proxyId = proxyData.getId();
        }
        else
        {
            proxyId = findProxy(proxyData);
        }

        ProxyEntity result = null;

        if (proxyId == -1)
        {
//            LogWrapper.d(TAG,"Insert new Proxy: " + proxyData);
            result = createProxy(proxyData);
        }
        else
        {
            // Update
//            LogWrapper.d(TAG,"Update Proxy: " + proxyData);
            result = updateProxy(proxyId, proxyData);
        }

        return result;
    }

    public TagEntity upsertTag(TagEntity tag)
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

    public ProxyEntity getRandomProxy()
    {
        App.getLogger().startTrace(TAG, "getRandomProxy", Log.INFO);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " ORDER BY Random() LIMIT 1";

        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        ProxyEntity proxyData = null;
        if (!cursor.isAfterLast())
        {
            proxyData = cursorToProxy(cursor);
        }

        cursor.close();

        if (proxyData == null)
            return null;
        else
        {
            proxyData.setTags(getTagsForProxy(proxyData.getId()));
            App.getLogger().stopTrace(TAG, "getRandomProxy", proxyData.toString(), Log.INFO);
            return proxyData;
        }
    }

    public ProxyEntity getProxy(long proxyId)
    {
        App.getLogger().startTrace(TAG, "getProxy", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(proxyId)});
        cursor.moveToFirst();
        ProxyEntity proxyData = null;
        if (!cursor.isAfterLast())
        {
            proxyData = cursorToProxy(cursor);
        }

        cursor.close();

        proxyData.setTags(getTagsForProxy(proxyId));
        App.getLogger().stopTrace(TAG, "getProxy", proxyData.toString(), Log.DEBUG);
        return proxyData;
    }

    public TagEntity getRandomTag()
    {
        App.getLogger().startTrace(TAG, "getTag", Log.INFO);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_TAG + " != 'IN USE'"
                + " ORDER BY Random() LIMIT 1";

        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        TagEntity tag = null;
        if (!cursor.isAfterLast())
        {
            tag = cursorToTag(cursor);
        }

        cursor.close();

        if (tag == null)
        {
            return null;
        }
        else
        {
            App.getLogger().stopTrace(TAG, "getTag", tag.toString(), Log.INFO);
            return tag;
        }
    }

    public TagEntity getTag(long tagId)
    {
//        LogWrapper.startTrace(TAG, "getTag", Log.INFO);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(tagId)});
        cursor.moveToFirst();
        TagEntity tag = null;
        if (!cursor.isAfterLast())
        {
            tag = cursorToTag(cursor);
        }

        cursor.close();
        if (tag == null)
        {
            return null;
        }
        else
        {
//            LogWrapper.stopTrace(TAG, "getTag", tag.toString(), Log.INFO);
            return tag;
        }
    }

    public ProxyTagLinkEntity getProxyTagLink(long linkId)
    {
        App.getLogger().startTrace(TAG, "getProxyTagLink", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * "
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(linkId)});
        cursor.moveToFirst();
        ProxyTagLinkEntity link = null;
        if (!cursor.isAfterLast())
        {
            link = cursorToProxyTagLink(cursor);
        }

        cursor.close();

        if (link == null)
        {
            App.getLogger().stopTrace(TAG, "getProxyTagLink", link.toString(), Log.DEBUG);
            return null;
        }
        else
        {
            App.getLogger().stopTrace(TAG, "getProxyTagLink", link.toString(), Log.DEBUG);
            return link;
        }
    }

    public long findProxy(ProxyConfiguration configuration)
    {
        long result = -1;

        if (configuration != null)
        {
            if (configuration.isValidProxyConfiguration())
            {
                ProxyEntity proxy = new ProxyEntity();
                proxy.host = configuration.getProxyHost();
                proxy.port = configuration.getProxyPort();
                proxy.exclusion = configuration.getProxyExclusionList();

                result = findProxy(proxy);
            }
        }

        return result;
    }

    public long findProxy(String proxyHost, Integer proxyPort, String proxyExclusion)
    {
        if (proxyHost != null && proxyPort != null)
        {
            ProxyEntity proxy = new ProxyEntity();
            proxy.host = proxyHost;
            proxy.port = proxyPort;
            proxy.exclusion = proxyExclusion;

            return findProxy(proxy);
        }
        else
            return -1;
    }

    public List<Long> findDuplicatedProxy(String proxyHost, Integer proxyPort)
    {
        App.getLogger().startTrace(TAG, "findDuplicatedProxy", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<Long> duplicatedProxiesID = new ArrayList<Long>();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST + " =?"
                + " AND " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT + "=?";

        String[] selectionArgs = {proxyHost, Integer.toString(proxyPort)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        long proxyId = -1;
        if (!cursor.isAfterLast())
        {
            proxyId = cursor.getLong(0);
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Long id = cursor.getLong(0);
            duplicatedProxiesID.add(id);
            cursor.moveToNext();
        }
        cursor.close();

        cursor.close();
        App.getLogger().stopTrace(TAG, "findDuplicatedProxy", Log.DEBUG);
        return duplicatedProxiesID;
    }

    public long findProxy(ProxyEntity proxyData)
    {
        App.getLogger().startTrace(TAG, "findProxy", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST + " =?"
                + " AND " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT + "=?"
                + " AND " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION + "=?";

        String[] selectionArgs = {proxyData.host, Integer.toString(proxyData.port), proxyData.exclusion};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        cursor.moveToFirst();
        long proxyId = -1;
        if (!cursor.isAfterLast())
        {
            proxyId = cursor.getLong(0);
        }

        cursor.close();
        App.getLogger().stopTrace(TAG, "findProxy", Log.DEBUG);
        return proxyId;
    }

    public long findTag(String tagName)
    {
        App.getLogger().startTrace(TAG, "findTag", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

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
        App.getLogger().stopTrace(TAG, "findTag", Log.DEBUG);
        return tagId;
    }

    public ProxyEntity createProxy(ProxyEntity proxyData)
    {
        App.getLogger().startTrace(TAG, "createProxy", Log.DEBUG, true);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST, proxyData.host);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT, proxyData.port);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, proxyData.exclusion);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE, proxyData.getCountryCode());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, proxyData.getInUse());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_PROXIES, null, values);
        ProxyEntity newProxy = getProxy(insertId);

        // Update or add all the TAGS listed into the ProxyEntity object
        for (TagEntity tag : proxyData.getTags())
        {
            createProxyTagLink(newProxy.getId(), tag.getId());
        }

        App.getLogger().stopTrace(TAG, "createProxy", Log.DEBUG);

        notifyProxyChange();

        return newProxy;
    }

    public TagEntity createTag(TagEntity tag)
    {
        App.getLogger().startTrace(TAG, "createTag", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG, tag.tag);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_COLOR, tag.tagColor);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_TAGS, null, values);

        TagEntity newTag = getTag(insertId);
        App.getLogger().stopTrace(TAG, "createTag", Log.DEBUG);
        return newTag;
    }

    public ProxyTagLinkEntity createProxyTagLink(long proxyId, long tagId)
    {
        App.getLogger().startTrace(TAG, "createProxyTagLink", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_ID, proxyId);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_ID, tagId);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS, null, values);

        ProxyTagLinkEntity newLink = getProxyTagLink(insertId);
        App.getLogger().stopTrace(TAG, "createProxyTagLink", Log.DEBUG);
        return newLink;
    }

    public ProxyEntity updateProxy(long proxyId, ProxyEntity newData)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST, newData.host);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT, newData.port);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, newData.exclusion);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE, newData.getCountryCode());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, newData.getInUse());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long updatedRows = database.update(DatabaseSQLiteOpenHelper.TABLE_PROXIES, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[]{String.valueOf(proxyId)});

        // TODO: Stupid implementation, delete all links, and add the newer ones
        deleteProxyTagLinksForProxy(proxyId);

//        List<TagEntity> currentTags = getTagsForProxy(proxyId);

        for (TagEntity newTag : newData.getTags())
        {
            createProxyTagLink(proxyId, newTag.getId());
        }

        ProxyEntity updatedProxy = getProxy(proxyId);

        notifyProxyChange();

        return updatedProxy;
    }

    public void clearInUseFlag(Long... proxyIDs)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, false);

        long updatedRows = 0;
        if (proxyIDs != null && proxyIDs.length > 0)
        {
            String query = "UPDATE " + DatabaseSQLiteOpenHelper.TABLE_PROXIES + " SET " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE + " = 0 WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " IN (" + TextUtils.join(",", proxyIDs) + ")";

            Cursor cursor = database.rawQuery(query, null);
            updatedRows = cursor.getCount();
            cursor.close();
        }
        else
        {
            updatedRows = database.update(DatabaseSQLiteOpenHelper.TABLE_PROXIES, values, null, null);
        }

        App.getLogger().d(TAG, "Cleared in use flag for : " + updatedRows + " proxies");
    }

    public void setInUseFlag(Long... inUseProxies)
    {
        try
        {
            SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
            database.beginTransaction();

            try
            {
                clearInUseFlag();

                ContentValues values = new ContentValues();
                values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, false);

                long updatedRows = 0;
                if (inUseProxies != null && inUseProxies.length > 0)
                {
                    String query = "UPDATE " + DatabaseSQLiteOpenHelper.TABLE_PROXIES + " SET " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE + " = 1 WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " IN (" + TextUtils.join(",", inUseProxies) + ")";

                    Cursor cursor = database.rawQuery(query, null);
                    updatedRows = cursor.getCount();
                    cursor.close();
                }
                App.getLogger().d(TAG, "Set in use flag for : " + updatedRows + " proxies");
                database.setTransactionSuccessful();
            }
            catch (Exception e)
            {
                EventReportingUtils.sendException(e);
            }
            finally
            {
                database.endTransaction();
            }
        }
        catch (Exception e)
        {
            EventReportingUtils.sendException(e);
        }
    }

    private void notifyProxyChange()
    {
        context.sendBroadcast(new Intent(Intents.PROXY_REFRESH_UI));
        context.sendBroadcast(new Intent(Intents.PROXY_SAVED));
    }

    private void notifyDBReset()
    {
        context.sendBroadcast(new Intent(Intents.PROXY_SETTINGS_STARTED));
        context.sendBroadcast(new Intent(Intents.PROXY_REFRESH_UI));
    }

    public TagEntity updateTag(long tagId, TagEntity newData)
    {
        TagEntity persistedTag = getTag(tagId);

        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG, newData.tag);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_COLOR, newData.tagColor);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long updatedRows = database.update(DatabaseSQLiteOpenHelper.TABLE_TAGS, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[]{persistedTag.getId().toString()});

        TagEntity updatedTag = getTag(persistedTag.getId());
        return updatedTag;
    }

    public void deleteProxy(long proxyId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        database.delete(DatabaseSQLiteOpenHelper.TABLE_PROXIES, DatabaseSQLiteOpenHelper.COLUMN_ID + "=?", new String[]{String.valueOf(proxyId)});
    }

    public void deleteProxyTagLink(long linkId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        database.delete(DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS, DatabaseSQLiteOpenHelper.COLUMN_ID + "=?", new String[]{String.valueOf(linkId)});
    }

    public void deleteProxyTagLinksForProxy(long proxyId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        database.delete(DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS, DatabaseSQLiteOpenHelper.COLUMN_PROXY_ID + "=?", new String[]{String.valueOf(proxyId)});
    }

    public void deleteProxyTagLink(long proxyId, long tagId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        database.delete(DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS, DatabaseSQLiteOpenHelper.COLUMN_PROXY_ID + "=? AND " + DatabaseSQLiteOpenHelper.COLUMN_TAG_ID + "=?", new String[]{String.valueOf(proxyId), String.valueOf(tagId)});
    }

    public void deleteTag(long tagId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
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

    public Map<Long, ProxyEntity> getAllProxiesWithTAGs()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        Map<Long, ProxyEntity> proxies = new HashMap<Long, ProxyEntity>();

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_PROXIES, proxyTableColumns, null, null, null, null, DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            ProxyEntity proxy = cursorToProxy(cursor);
            proxies.put(proxy.getId(), proxy);
            cursor.moveToNext();
        }
        cursor.close();

        for (long proxyId : proxies.keySet())
        {
            ProxyEntity proxy = proxies.get(proxyId);
            proxy.setTags(getTagsForProxy(proxy.getId()));
        }

        return proxies;
    }

    public List<ProxyEntity> getProxyWithEmptyCountryCode()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<ProxyEntity> proxies = new ArrayList<ProxyEntity>();

        String query = "SELECT *"
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{""});

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            ProxyEntity proxy = cursorToProxy(cursor);
            proxy.setTags(getTagsForProxy(proxy.getId()));
            proxies.add(proxy);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();

        return proxies;
    }

    public List<TagEntity> getAllTags()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<TagEntity> proxies = new ArrayList<TagEntity>();

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_TAGS, tagsTableColumns, null, null, null, null, DatabaseSQLiteOpenHelper.COLUMN_TAG + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            TagEntity proxy = cursorToTag(cursor);
            proxies.add(proxy);
            cursor.moveToNext();
        }
        cursor.close();

        return proxies;
    }

    public List<TagEntity> getTagsForProxy(long proxyId)
    {
//        LogWrapper.startTrace(TAG, "getTagsForProxy", Log.DEBUG);
        List<ProxyTagLinkEntity> links = getProxyTagLinkForProxy(proxyId);
        List<TagEntity> tags = new ArrayList<TagEntity>();
        for (ProxyTagLinkEntity link : links)
        {
            tags.add(getTag(link.tagId));
        }

//        LogWrapper.stopTrace(TAG, "getTagsForProxy", String.valueOf(proxyId), Log.DEBUG);
        return tags;
    }

    private List<ProxyTagLinkEntity> getProxyTagLinkForProxy(long proxyId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT *"
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_ID + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(proxyId)});
        cursor.moveToFirst();

        List<ProxyTagLinkEntity> links = new ArrayList<ProxyTagLinkEntity>();

        while (!cursor.isAfterLast())
        {
            ProxyTagLinkEntity link = cursorToProxyTagLink(cursor);
            links.add(link);
            cursor.moveToNext();
        }

        cursor.close();

        return links;
    }

    private ProxyTagLinkEntity cursorToProxyTagLink(Cursor cursor)
    {
        ProxyTagLinkEntity link = new ProxyTagLinkEntity();
        link.setId(cursor.getLong(0));
        link.proxyId = cursor.getLong(1);
        link.tagId = cursor.getLong(2);

        link.setCreationDate(cursor.getLong(3));
        link.setModifiedDate(cursor.getLong(4));

        link.isPersisted = true;

        return link;
    }

    private ProxyEntity cursorToProxy(Cursor cursor)
    {
        ProxyEntity proxy = new ProxyEntity();
        proxy.setId(cursor.getLong(0));
        proxy.host = cursor.getString(1);
        proxy.port = cursor.getInt(2);
        proxy.exclusion = cursor.getString(3);
        proxy.setCountryCode(cursor.getString(4));
        proxy.setInUse(cursor.getInt(5) > 0);
        proxy.setCreationDate(cursor.getLong(6));
        proxy.setModifiedDate(cursor.getLong(7));

        proxy.isPersisted = true;

        return proxy;
    }

    private TagEntity cursorToTag(Cursor cursor)
    {
        TagEntity tag = new TagEntity();
        tag.setId(cursor.getLong(0));
        tag.tag = cursor.getString(1);
        tag.tagColor = cursor.getInt(2);
        tag.setCreationDate(cursor.getLong(3));
        tag.setModifiedDate(cursor.getLong(4));

        tag.isPersisted = true;

        return tag;
    }
}
