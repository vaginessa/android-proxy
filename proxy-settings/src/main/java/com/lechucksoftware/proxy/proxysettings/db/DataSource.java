package com.lechucksoftware.proxy.proxysettings.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.lechucksoftware.proxy.proxysettings.App;
import com.lechucksoftware.proxy.proxysettings.constants.Intents;
import com.lechucksoftware.proxy.proxysettings.utils.DBUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import be.shouldit.proxy.lib.APLNetworkId;
import be.shouldit.proxy.lib.WiFiApConfig;
import be.shouldit.proxy.lib.enums.SecurityType;
import be.shouldit.proxy.lib.reflection.android.ProxySetting;
import timber.log.Timber;

/**
 * Created by Marco on 13/09/13.
 */
public class DataSource
{
    // Database fields
    public static String TAG = DataSource.class.getSimpleName();
    private final Context context;
    private final boolean DUMP_CURSOR_TOSTRING = false;

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

    public PacEntity upsertPac(PacEntity pacData)
    {
        long pacId = -1;
        if (pacData.isPersisted())
        {
            pacId = pacData.getId();
        }
        else
        {
            pacId = findPac(pacData);
        }

        PacEntity result = null;

        if (pacId == -1)
        {
            result = createPac(pacData);
        }
        else
        {
            result = updatePac(pacId, pacData);
        }

        return result;
    }

    public ProxyEntity upsertProxy(ProxyEntity proxyData)
    {
        long proxyId = -1;
        if (proxyData.isPersisted())
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
            result = createProxy(proxyData);
        }
        else
        {
            result = updateProxy(proxyId, proxyData);
        }

        return result;
    }

    public TagEntity upsertTag(TagEntity tag)
    {
        long tagId = findTag(tag.getTag());

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

    public WiFiAPEntity upsertWifiAP(WiFiApConfig config)
    {
        WiFiAPEntity result = null;

        if (config != null)
        {
            Timber.d("Upserting Wi-fi configuration: '%s'", config.toShortString());

            WiFiAPEntity wiFiAPEntity = new WiFiAPEntity();
            wiFiAPEntity.setSsid(config.getSSID());
            wiFiAPEntity.setSecurityType(config.getSecurityType());
            wiFiAPEntity.setProxySetting(config.getProxySetting());

            wiFiAPEntity.setProxyId(-1L);
            wiFiAPEntity.setPACId(-1L);

            if (wiFiAPEntity.getProxySetting() == ProxySetting.STATIC)
            {
                if (config.isValidProxyConfiguration())
                {
                    ProxyEntity proxy = new ProxyEntity();
                    proxy.setHost(config.getProxyHost());
                    proxy.setPort(config.getProxyPort());
                    proxy.setExclusion(config.getProxyExclusionList());

                    wiFiAPEntity.upsertProxy(proxy);
                    wiFiAPEntity.setPACId(-1L);
                }
            }
            else if (wiFiAPEntity.getProxySetting() == ProxySetting.PAC)
            {
                if (config.isValidProxyConfiguration())
                {
                    PacEntity pac = new PacEntity();
                    pac.setPacUrlFile(config.getPacFileUri().toString());

                    wiFiAPEntity.upsertProxyPAC(pac);
                    wiFiAPEntity.setProxyId(-1L);
                }
            }

            Timber.d("Upsert Wi-Fi entity to DB: '%s'", wiFiAPEntity.toString());
            result = upsertWifiAP(wiFiAPEntity);
        }

        return result;
    }

    public WiFiAPEntity upsertWifiAP(WiFiAPEntity wiFiAPEntity)
    {
        long wifiApId = -1;
        if (wiFiAPEntity.isPersisted())
        {
            wifiApId = wiFiAPEntity.getId();
        }
        else
        {
            wifiApId = findWifiAp(wiFiAPEntity);
        }

        WiFiAPEntity result = null;

        if (wifiApId == -1)
        {
            // Insert
            Timber.d("Insert WifiAp: %s", wiFiAPEntity);
            result = createWifiAp(wiFiAPEntity);
        }
        else
        {
            // Update
            Timber.d("Update WifiAp: %s", wiFiAPEntity);
            result = updateWifiAP(wifiApId, wiFiAPEntity);
        }

        return result;
    }

    public ProxyEntity getRandomProxy()
    {
        App.getTraceUtils().startTrace(TAG, "getRandomProxy", Log.INFO);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_PROXIES_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " ORDER BY Random() LIMIT 1";

        Cursor cursor = DBUtils.rawQuery(database, query, null);
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
            App.getTraceUtils().stopTrace(TAG, "getRandomProxy", proxyData.toString(), Log.INFO);
            return proxyData;
        }
    }

    public PacEntity getRandomPac()
    {
        App.getTraceUtils().startTrace(TAG, "getRandomPac", Log.INFO);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_PAC_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PAC
                + " ORDER BY Random() LIMIT 1";

        Cursor cursor = DBUtils.rawQuery(database, query, null);
        cursor.moveToFirst();
        PacEntity pacData = null;
        if (!cursor.isAfterLast())
        {
            pacData = cursorToPAC(cursor);
        }

        cursor.close();

        if (pacData == null)
            return null;
        else
        {
            App.getTraceUtils().stopTrace(TAG, "getRandomPac", pacData.toString(), Log.INFO);
            return pacData;
        }
    }

    public WiFiAPEntity getRandomWifiAp()
    {
        App.getTraceUtils().startTrace(TAG, "getRandomWifiAp", Log.INFO);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP_COLUMNS_STRING
                + " , " + DatabaseSQLiteOpenHelper.TABLE_PROXIES_COLUMNS_STRING
                + " , " + DatabaseSQLiteOpenHelper.TABLE_PAC_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP
                + " LEFT JOIN " + DatabaseSQLiteOpenHelper.TABLE_PROXIES + " ON "
                        + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP + "." + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID + " = "
                        + DatabaseSQLiteOpenHelper.TABLE_PROXIES + "." + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " LEFT JOIN " + DatabaseSQLiteOpenHelper.TABLE_PAC + " ON "
                        + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP + "." + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID + " = "
                        + DatabaseSQLiteOpenHelper.TABLE_PAC + "." + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " ORDER BY Random() LIMIT 1";

        Cursor cursor = DBUtils.rawQuery(database, query, null);

        cursor.moveToFirst();
        WiFiAPEntity wiFiAPEntity = null;
        if (!cursor.isAfterLast())
        {
            wiFiAPEntity = cursorToWifiAP(cursor);
        }

        cursor.close();

        if (wiFiAPEntity == null)
            return null;
        else
        {
            App.getTraceUtils().stopTrace(TAG, "getRandomWifiAp", wiFiAPEntity.toString(), Log.INFO);
            return wiFiAPEntity;
        }
    }

    public WiFiAPEntity getWifiAP(long wifiId)
    {
        App.getTraceUtils().startTrace(TAG, "getWifiAP", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP_COLUMNS_STRING
                + " , " + DatabaseSQLiteOpenHelper.TABLE_PROXIES_COLUMNS_STRING
                + " , " + DatabaseSQLiteOpenHelper.TABLE_PAC_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP
                + " LEFT JOIN " + DatabaseSQLiteOpenHelper.TABLE_PROXIES + " ON "
                    + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP + "." + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID + " = "
                    + DatabaseSQLiteOpenHelper.TABLE_PROXIES + "." + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " LEFT JOIN " + DatabaseSQLiteOpenHelper.TABLE_PAC + " ON "
                    + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP + "." + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID + " = "
                    + DatabaseSQLiteOpenHelper.TABLE_PAC + "." + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " WHERE " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP + "." + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = DBUtils.rawQuery(database, query, new String[]{String.valueOf(wifiId)});

        cursor.moveToFirst();
        WiFiAPEntity wiFiAPEntity = null;
        if (!cursor.isAfterLast())
        {
            wiFiAPEntity = cursorToWifiAP(cursor);
        }

        cursor.close();

        if (wiFiAPEntity == null)
        {
            App.getTraceUtils().stopTrace(TAG, "getWifiAP", String.format("Cannot get Wi-Fi AP: '%d'", wifiId), Log.ERROR);
            return null;
        }
        else
        {
            App.getTraceUtils().stopTrace(TAG, "getWifiAP", wiFiAPEntity.toString(), Log.DEBUG);
            return wiFiAPEntity;
        }
    }

    public ProxyEntity getProxy(long proxyId)
    {
        App.getTraceUtils().startTrace(TAG, "getProxy", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_PROXIES_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = DBUtils.rawQuery(database, query, new String[]{String.valueOf(proxyId)});

        cursor.moveToFirst();
        ProxyEntity proxyData = null;
        if (!cursor.isAfterLast())
        {
            proxyData = cursorToProxy(cursor);
        }

        cursor.close();

        if (proxyData == null)
        {
            App.getTraceUtils().stopTrace(TAG, "getProxy", String.format("Cannot get STATIC proxy: '%d'", proxyId), Log.ERROR);
            return null;
        }
        else
        {
            proxyData.setTags(getTagsForProxy(proxyId));
            App.getTraceUtils().stopTrace(TAG, "getProxy", proxyData.toString(), Log.DEBUG);
            return proxyData;
        }
    }

    public PacEntity getPac(Long pacId)
    {
        App.getTraceUtils().startTrace(TAG, "getPac", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_PAC_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PAC
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = DBUtils.rawQuery(database, query, new String[]{String.valueOf(pacId)});

        cursor.moveToFirst();
        PacEntity pacData = null;
        if (!cursor.isAfterLast())
        {
            pacData = cursorToPAC(cursor);
        }

        cursor.close();

        if (pacData == null)
        {
            App.getTraceUtils().stopTrace(TAG, "getPac", String.format("Cannot get PAC: '%d'", pacId), Log.ERROR);
            return null;
        }
        else
        {
            App.getTraceUtils().stopTrace(TAG, "getPac", pacData.toString(), Log.DEBUG);
            return pacData;
        }
    }

    public TagEntity getRandomTag()
    {
        App.getTraceUtils().startTrace(TAG, "getTag", Log.INFO);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_TAGS_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_TAG + " != 'IN USE'"
                + " ORDER BY Random() LIMIT 1";

        Cursor cursor = DBUtils.rawQuery(database, query, null);

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
            App.getTraceUtils().stopTrace(TAG, "getTag", tag.toString(), Log.INFO);
            return tag;
        }
    }

    public TagEntity getTag(long tagId)
    {
//        LogWrapper.startTrace(TAG, "getTag", Log.INFO);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_TAGS_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = DBUtils.rawQuery(database, query, new String[]{String.valueOf(tagId)});

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
        App.getTraceUtils().startTrace(TAG, "getProxyTagLink", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " +  DatabaseSQLiteOpenHelper.TABLE_TAGGED_PROXIES_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_ID + " =?";

        Cursor cursor = DBUtils.rawQuery(database, query, new String[]{String.valueOf(linkId)});

        cursor.moveToFirst();
        ProxyTagLinkEntity link = null;
        if (!cursor.isAfterLast())
        {
            link = cursorToProxyTagLink(cursor);
        }

        cursor.close();

        if (link == null)
        {
            App.getTraceUtils().stopTrace(TAG, "getProxyTagLink", link.toString(), Log.DEBUG);
            return null;
        }
        else
        {
            App.getTraceUtils().stopTrace(TAG, "getProxyTagLink", link.toString(), Log.DEBUG);
            return link;
        }
    }

    public long findWifiAp(WiFiApConfig configuration)
    {
        long result = -1;

        if (configuration != null)
        {
            if (configuration.getAPLNetworkId() != null)
            {
                WiFiAPEntity wiFiAPEntity = new WiFiAPEntity();
                wiFiAPEntity.setSsid(configuration.getAPLNetworkId().SSID);
                wiFiAPEntity.setSecurityType(configuration.getAPLNetworkId().Security);

                result = findWifiAp(wiFiAPEntity);
            }
        }

        return result;
    }

    public long findWifiAp(WiFiAPEntity wiFiAPEntity)
    {
        return findWifiAp(new APLNetworkId(wiFiAPEntity.getSsid(), wiFiAPEntity.getSecurityType()));
    }

    public long findWifiAp(APLNetworkId aplNetworkId)
    {
        App.getTraceUtils().startTrace(TAG, "findWifiAp", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_WIFI_SSID + " =?"
                + " AND " + DatabaseSQLiteOpenHelper.COLUMN_WIFI_SECURITY_TYPE + "=?";

        String[] selectionArgs = { aplNetworkId.SSID, aplNetworkId.Security.toString()};
        Cursor cursor = DBUtils.rawQuery(database, query, selectionArgs);

        cursor.moveToFirst();
        long wifiId = -1;
        if (!cursor.isAfterLast())
        {
            wifiId = cursor.getLong(0);
        }

        cursor.close();
        App.getTraceUtils().stopTrace(TAG, "findWifiAp", String.format("Found Wi-Fi Id: '%d'", wifiId) , Log.DEBUG);
        return wifiId;
    }

    public long findPac(WiFiApConfig configuration)
    {
        long result = -1;

        if (configuration != null)
        {
            if (configuration.getProxySetting() == ProxySetting.PAC && configuration.isValidProxyConfiguration())
            {
                PacEntity pacEntity = new PacEntity();
                pacEntity.setPacUrlFile(configuration.getPacFileUri().toString());

                result = findPac(pacEntity);
            }
        }

        return result;
    }

    public long findProxy(WiFiApConfig configuration)
    {
        long result = -1;

        if (configuration != null)
        {
            if (configuration.getProxySetting() == ProxySetting.STATIC && configuration.isValidProxyConfiguration())
            {
                ProxyEntity proxy = new ProxyEntity();
                proxy.setHost(configuration.getProxyHost());
                proxy.setPort(configuration.getProxyPort());
                proxy.setExclusion(configuration.getProxyExclusionList());

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
            proxy.setHost(proxyHost);
            proxy.setPort(proxyPort);
            proxy.setExclusion(proxyExclusion);

            return findProxy(proxy);
        }
        else
            return -1;
    }

    public List<Long> findDuplicatedProxy(String proxyHost, Integer proxyPort)
    {
        App.getTraceUtils().startTrace(TAG, "findDuplicatedProxy", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<Long> duplicatedProxiesID = new ArrayList<Long>();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST + " =?"
                + " AND " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT + "=?";

        String[] selectionArgs = {proxyHost, Integer.toString(proxyPort)};
        Cursor cursor = DBUtils.rawQuery(database, query, selectionArgs);

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

        App.getTraceUtils().stopTrace(TAG, "findDuplicatedProxy", Log.DEBUG);
        return duplicatedProxiesID;
    }

    public List<Long> findDuplicatedPac(String pacUrlFile)
    {
        App.getTraceUtils().startTrace(TAG, "findDuplicatedPac", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<Long> duplicatedProxiesID = new ArrayList<Long>();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PAC
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PAC_URL_FILE + " =?";

        String[] selectionArgs = {pacUrlFile};
        Cursor cursor = DBUtils.rawQuery(database, query, selectionArgs);

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

        App.getTraceUtils().stopTrace(TAG, "findDuplicatedPac", Log.DEBUG);
        return duplicatedProxiesID;
    }

    public long findPac(PacEntity pacEntity)
    {
        App.getTraceUtils().startTrace(TAG, "findPac", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PAC
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PAC_URL_FILE + " =?";

        String[] selectionArgs = {pacEntity.getPacUriFile().toString()};
        Cursor cursor = DBUtils.rawQuery(database, query, selectionArgs);

        cursor.moveToFirst();
        long pacId = -1;
        if (!cursor.isAfterLast())
        {
            pacId = cursor.getLong(0);
        }

        cursor.close();

        App.getTraceUtils().stopTrace(TAG, "findPac", String.format("Found PAC Id: '%d'", pacId) , Log.DEBUG);
        return pacId;
    }

    public long findProxy(ProxyEntity proxyData)
    {
        App.getTraceUtils().startTrace(TAG, "findProxy", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST + " =?"
                + " AND " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT + "=?"
                + " AND " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION + "=?";

        String[] selectionArgs = {proxyData.getHost(), Integer.toString(proxyData.getPort()), proxyData.getExclusion()};
        Cursor cursor = DBUtils.rawQuery(database, query, selectionArgs);

        cursor.moveToFirst();
        long proxyId = -1;
        if (!cursor.isAfterLast())
        {
            proxyId = cursor.getLong(0);
        }

        cursor.close();

        App.getTraceUtils().stopTrace(TAG, "findProxy", String.format("Found STATIC proxy Id: '%d'", proxyId) , Log.DEBUG);
        return proxyId;
    }

    public long findTag(String tagName)
    {
        App.getTraceUtils().startTrace(TAG, "findTag", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_TAG + " =?";

        Cursor cursor = DBUtils.rawQuery(database, query, new String[]{tagName});

        cursor.moveToFirst();
        long tagId = -1;
        if (!cursor.isAfterLast())
        {
            tagId = cursor.getLong(0);
        }

        cursor.close();

        App.getTraceUtils().stopTrace(TAG, "findTag", String.format("Found TAG Id: '%d'", tagId) , Log.DEBUG);
        return tagId;
    }

    public WiFiAPEntity createWifiAp(WiFiAPEntity wiFiAPEntity)
    {
        App.getTraceUtils().startTrace(TAG, "createWifiAp", Log.DEBUG, true);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_WIFI_SSID, wiFiAPEntity.getSsid());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_WIFI_SECURITY_TYPE, wiFiAPEntity.getSecurityType().toString());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_SETTING, wiFiAPEntity.getProxySetting().toString());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID, wiFiAPEntity.getProxyId());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID, wiFiAPEntity.getPacId());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_WIFI_AP, null, values);
        WiFiAPEntity newWifiAp = getWifiAP(insertId);

        updateInUseFlag();

        App.getTraceUtils().stopTrace(TAG, "createWifiAp", String.format("Created Wi-Fi AP Id: '%d'", insertId), Log.DEBUG);
        return newWifiAp;
    }

    public ProxyEntity createProxy(ProxyEntity proxyData)
    {
        App.getTraceUtils().startTrace(TAG, "createProxy", Log.DEBUG, true);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST, proxyData.getHost());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT, proxyData.getPort());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, proxyData.getExclusion());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE, proxyData.getCountryCode());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, proxyData.getUsedByCount());

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

        App.getTraceUtils().stopTrace(TAG, "createProxy", String.format("Created STATIC proxy Id: '%d'", insertId), Log.DEBUG);

        notifyProxyChange();

        return newProxy;
    }

    public PacEntity createPac(PacEntity pacEntity)
    {
        App.getTraceUtils().startTrace(TAG, "createPac", Log.DEBUG, true);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PAC_URL_FILE, pacEntity.getPacUriFile().toString());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PAC_IN_USE, pacEntity.getUsedByCount());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_PAC, null, values);
        PacEntity newPac = getPac(insertId);

        App.getTraceUtils().stopTrace(TAG, "createPac", String.format("Created PAC Id: '%d'", insertId) , Log.DEBUG);

        notifyProxyChange();

        return newPac;
    }

    public TagEntity createTag(TagEntity tag)
    {
        App.getTraceUtils().startTrace(TAG, "createTag", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG, tag.getTag());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_COLOR, tag.getTagColor());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_TAGS, null, values);

        TagEntity newTag = getTag(insertId);
        App.getTraceUtils().stopTrace(TAG, "createTag", Log.DEBUG);
        return newTag;
    }

    public ProxyTagLinkEntity createProxyTagLink(long proxyId, long tagId)
    {
        App.getTraceUtils().startTrace(TAG, "createProxyTagLink", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_ID, proxyId);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_ID, tagId);

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_CREATION_DATE, currentDate);
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long insertId = database.insert(DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS, null, values);

        ProxyTagLinkEntity newLink = getProxyTagLink(insertId);
        App.getTraceUtils().stopTrace(TAG, "createProxyTagLink", Log.DEBUG);
        return newLink;
    }

    public ProxyEntity updateProxy(long proxyId, ProxyEntity newData)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST, newData.getHost());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_PORT, newData.getPort());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_EXCLUSION, newData.getExclusion());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE, newData.getCountryCode());

        if (newData.getUsedByCount() != -1)
        {
            values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, newData.getUsedByCount());
        }

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

    public PacEntity updatePac(long pacId, PacEntity newData)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PAC_URL_FILE, newData.getPacUriFile().toString());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_PAC_IN_USE, newData.getUsedByCount());

        if (newData.getUsedByCount() != -1)
        {
            values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, newData.getUsedByCount());
        }

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long updatedRows = database.update(DatabaseSQLiteOpenHelper.TABLE_PAC, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[]{String.valueOf(pacId)});

        PacEntity updatedPac = getPac(pacId);

        notifyProxyChange();

        return updatedPac;
    }

    private WiFiAPEntity updateWifiAP(long wifiApId, WiFiAPEntity wiFiAPEntity)
    {
        WiFiAPEntity persistedWifiAp = getWifiAP(wifiApId);

        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_SETTING, wiFiAPEntity.getProxySetting().toString());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID, wiFiAPEntity.getProxyId());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID, wiFiAPEntity.getPacId());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long updatedRows = database.update(DatabaseSQLiteOpenHelper.TABLE_WIFI_AP, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[]{persistedWifiAp.getId().toString()});

        updateInUseFlag();
        WiFiAPEntity updatedWifiAP = getWifiAP(persistedWifiAp.getId());
        return updatedWifiAP;
    }

    public TagEntity updateTag(long tagId, TagEntity newData)
    {
        TagEntity persistedTag = getTag(tagId);

        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG, newData.getTag());
        values.put(DatabaseSQLiteOpenHelper.COLUMN_TAG_COLOR, newData.getTagColor());

        long currentDate = System.currentTimeMillis();
        values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);

        long updatedRows = database.update(DatabaseSQLiteOpenHelper.TABLE_TAGS, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[]{persistedTag.getId().toString()});

        TagEntity updatedTag = getTag(persistedTag.getId());
        return updatedTag;
    }

    public void updateInUseFlag()
    {
        App.getTraceUtils().startTrace(TAG, "updateInUseFlag", Log.DEBUG, true);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        database.beginTransaction();

        try
        {
            updateStaticInUseFlag(database);
            updatePacInUseFlag(database);

            database.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            Timber.e(e,"Exception during updateInUseFlag");
        }
        finally
        {
            database.endTransaction();
        }

        App.getTraceUtils().stopTrace(TAG, "updateInUseFlag", Log.DEBUG);
    }

    private void updatePacInUseFlag(SQLiteDatabase database)
    {
        long updatedRows = 0;
        ContentValues values = new ContentValues();

        Map<Integer,Integer> pacCount = new TreeMap<>();
        String pacCountQuery = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID + " , count(1)" +
                " FROM " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP +
                " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID + " != -1"+
                " GROUP BY " +  DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID  +
                " ORDER BY " +  DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID;

        Cursor pacCountCursor = DBUtils.rawQuery(database, pacCountQuery, null);
        pacCountCursor.moveToFirst();
        while (!pacCountCursor.isAfterLast())
        {
            DatabaseUtils.dumpCurrentRowToString(pacCountCursor);
            pacCount.put(pacCountCursor.getInt(0), pacCountCursor.getInt(1));
            pacCountCursor.moveToNext();
        }
        pacCountCursor.close();

        App.getTraceUtils().partialTrace(TAG, "updateInUseFlag", String.format("PAC proxy used: (%s)", TextUtils.join("|", pacCount.entrySet())), Log.DEBUG);

        values.put(DatabaseSQLiteOpenHelper.COLUMN_PAC_IN_USE, 0);
        updatedRows = database.update(DatabaseSQLiteOpenHelper.TABLE_PAC, values, null, null);
        App.getTraceUtils().partialTrace(TAG, "updateInUseFlag", String.format("Reset PAC proxy used flags (%d)",updatedRows), Log.DEBUG);

        updatedRows = 0;
        for(int pacId : pacCount.keySet())
        {
            values = new ContentValues();
            values.put(DatabaseSQLiteOpenHelper.COLUMN_PAC_IN_USE, pacCount.get(pacId));
            long currentDate = System.currentTimeMillis();
            values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);
            updatedRows += database.update(DatabaseSQLiteOpenHelper.TABLE_PAC, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[]{String.valueOf(pacId)});
        }

        App.getTraceUtils().partialTrace(TAG, "updateInUseFlag", String.format("Updated PAC proxy used flag (%d)",updatedRows), Log.DEBUG);
    }

    private void updateStaticInUseFlag(SQLiteDatabase database)
    {
        long updatedRows = 0;
        Map<Integer,Integer> staticCount = new TreeMap<>();
        ContentValues values = new ContentValues();

        String staticCountQuery = "SELECT " + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID + " , count(1)" +
                    " FROM " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP +
                    " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID + " != -1" +
                    " GROUP BY " +  DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID  +
                    " ORDER BY " +  DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID;

        Cursor staticCountCursor = DBUtils.rawQuery(database, staticCountQuery, null);

        staticCountCursor.moveToFirst();
        while (!staticCountCursor.isAfterLast())
        {
            DatabaseUtils.dumpCurrentRowToString(staticCountCursor);
            staticCount.put(staticCountCursor.getInt(0), staticCountCursor.getInt(1));
            staticCountCursor.moveToNext();
        }
        staticCountCursor.close();

        App.getTraceUtils().partialTrace(TAG, "updateInUseFlag", String.format("STATIC proxy used: (%s)", TextUtils.join(", ", staticCount.entrySet())), Log.DEBUG);

        values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, 0);
        updatedRows = database.update(DatabaseSQLiteOpenHelper.TABLE_PROXIES, values, null, null);
        App.getTraceUtils().partialTrace(TAG, "updateInUseFlag", String.format("Reset STATIC proxy used flags (%d)",updatedRows), Log.DEBUG);

        updatedRows = 0;
        for(int proxyId : staticCount.keySet())
        {
            values = new ContentValues();
            values.put(DatabaseSQLiteOpenHelper.COLUMN_PROXY_IN_USE, staticCount.get(proxyId));
            long currentDate = System.currentTimeMillis();
            values.put(DatabaseSQLiteOpenHelper.COLUMN_MODIFIED_DATE, currentDate);
            updatedRows += database.update(DatabaseSQLiteOpenHelper.TABLE_PROXIES, values, DatabaseSQLiteOpenHelper.COLUMN_ID + " =?", new String[]{String.valueOf(proxyId)});
        }

        App.getTraceUtils().partialTrace(TAG, "updateInUseFlag", String.format("Updated STATIC proxy used flag (%d)",updatedRows), Log.DEBUG);
    }

    public void deleteProxy(long proxyId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        database.delete(DatabaseSQLiteOpenHelper.TABLE_PROXIES, DatabaseSQLiteOpenHelper.COLUMN_ID + "=?", new String[]{String.valueOf(proxyId)});
    }

    public void deletePac(Long pacId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        database.delete(DatabaseSQLiteOpenHelper.TABLE_PAC, DatabaseSQLiteOpenHelper.COLUMN_ID + "=?", new String[]{String.valueOf(pacId)});
    }

    public void deleteWifiAP(long wifiApId)
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        database.delete(DatabaseSQLiteOpenHelper.TABLE_WIFI_AP, DatabaseSQLiteOpenHelper.COLUMN_ID + "=?", new String[]{String.valueOf(wifiApId)});

        updateInUseFlag();
    }

    public void deleteWifiAP(APLNetworkId aplNetworkId)
    {
        long wifiId = findWifiAp(aplNetworkId);

        if (wifiId != -1)
        {
            deleteWifiAP(wifiId);
        }
        else
        {
            Timber.w("Cannot find Wi-Fi network to delete: %s", aplNetworkId.toString());
        }
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
        long result = 0;

        try
        {
            SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

            String query = "SELECT COUNT(1)"
                    + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES;

            Cursor cursor = DBUtils.rawQuery(database, query, null);
            cursor.moveToFirst();
            result = cursor.getLong(0);

            // Make sure to close the cursor
            cursor.close();
        }
        catch (SQLiteException e)
        {
            Timber.e(e,"Exception during getProxiesCount");
        }

        return result;
    }

    public long getPacCount()
    {
        long result = 0;

        try
        {
            SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

            String query = "SELECT COUNT(1)"
                    + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PAC;

            Cursor cursor = DBUtils.rawQuery(database, query, null);
            cursor.moveToFirst();
            result = cursor.getLong(0);

            // Make sure to close the cursor
            cursor.close();
        }
        catch (SQLiteException e)
        {
            Timber.e(e,"Exception during getProxiesCount");
        }

        return result;
    }

    public long getWifiApCount()
    {
        long result = 0;

        try
        {
            SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

            String query = "SELECT COUNT(1)"
                    + " FROM " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP;

            Cursor cursor = DBUtils.rawQuery(database, query, null);
            cursor.moveToFirst();
            result = cursor.getLong(0);

            // Make sure to close the cursor
            cursor.close();
        }
        catch (SQLiteException e)
        {
            Timber.e(e,"Exception during getWifiApCount");
        }

        return result;
    }

    public long getTagsCount()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT COUNT(1)"
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_TAGS;

        Cursor cursor = DBUtils.rawQuery(database, query, null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);

        // Make sure to close the cursor
        cursor.close();

        return result;
    }

    public Map<Long, ProxyEntity> getAllProxiesWithTAGs()
    {
        App.getTraceUtils().startTrace(TAG,"getAllProxiesWithTAGs", Log.DEBUG);
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        Map<Long, ProxyEntity> proxies = new HashMap<Long, ProxyEntity>();

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_PROXIES, DatabaseSQLiteOpenHelper.TABLE_PROXIES_COLUMNS, null, null, null, null, DatabaseSQLiteOpenHelper.COLUMN_PROXY_HOST + " ASC");
//        App.getTraceUtils().partialTrace(TAG,"getAllProxiesWithTAGs", "query", Log.DEBUG);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            ProxyEntity proxy = cursorToProxy(cursor);
            proxies.put(proxy.getId(), proxy);
            cursor.moveToNext();
        }
        cursor.close();
//        App.getTraceUtils().partialTrace(TAG, "getAllProxiesWithTAGs", "cursor", Log.DEBUG);

        // TODO: enable tags reading
//        for (long proxyId : proxies.keySet())
//        {
//            ProxyEntity proxy = proxies.get(proxyId);
//            proxy.setTags(getTagsForProxy(proxy.getId()));
//        }

        App.getTraceUtils().stopTrace(TAG, "getAllProxiesWithTAGs", Log.DEBUG);
        return proxies;
    }

    public Map<Long, PacEntity> getAllPac()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        Map<Long, PacEntity> pacs = new HashMap<Long, PacEntity>();

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_PAC, DatabaseSQLiteOpenHelper.TABLE_PAC_COLUMNS, null, null, null, null, DatabaseSQLiteOpenHelper.COLUMN_PAC_URL_FILE + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            PacEntity pacEntity = cursorToPAC(cursor);
            pacs.put(pacEntity.getId(), pacEntity);
            cursor.moveToNext();
        }
        cursor.close();

        return pacs;
    }

    public Map<Long, WiFiAPEntity> getAllWifiAp()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        Map<Long, WiFiAPEntity> wifiAPs = new HashMap<Long, WiFiAPEntity>();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP_COLUMNS_STRING
                + " , " + DatabaseSQLiteOpenHelper.TABLE_PROXIES_COLUMNS_STRING
                + " , " + DatabaseSQLiteOpenHelper.TABLE_PAC_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP
                + " LEFT JOIN " + DatabaseSQLiteOpenHelper.TABLE_PROXIES + " ON "
                    + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP + "." + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PROXY_ID + " = "
                    + DatabaseSQLiteOpenHelper.TABLE_PROXIES + "." + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " LEFT JOIN " + DatabaseSQLiteOpenHelper.TABLE_PAC + " ON "
                    + DatabaseSQLiteOpenHelper.TABLE_WIFI_AP + "." + DatabaseSQLiteOpenHelper.COLUMN_WIFI_PAC_ID + " = "
                    + DatabaseSQLiteOpenHelper.TABLE_PAC + "." + DatabaseSQLiteOpenHelper.COLUMN_ID
                + " ORDER BY " + DatabaseSQLiteOpenHelper.COLUMN_WIFI_SSID + " ASC";

        Cursor cursor = DBUtils.rawQuery(database, query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            WiFiAPEntity wiFiAPEntity = cursorToWifiAP(cursor);
            wifiAPs.put(wiFiAPEntity.getId(), wiFiAPEntity);
            cursor.moveToNext();
        }
        cursor.close();

        return wifiAPs;
    }

    public List<ProxyEntity> getProxyWithEmptyCountryCode()
    {
        SQLiteDatabase database = DatabaseSQLiteOpenHelper.getInstance(context).getReadableDatabase();

        List<ProxyEntity> proxies = new ArrayList<ProxyEntity>();

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_PROXIES_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXIES
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_COUNTRY_CODE + " =?";

        Cursor cursor = DBUtils.rawQuery(database, query, new String[]{""});

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

        Cursor cursor = database.query(DatabaseSQLiteOpenHelper.TABLE_TAGS, DatabaseSQLiteOpenHelper.TABLE_TAGS_COLUMNS, null, null, null, null, DatabaseSQLiteOpenHelper.COLUMN_TAG + " ASC");

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

        String query = "SELECT " + DatabaseSQLiteOpenHelper.TABLE_TAGGED_PROXIES_COLUMNS_STRING
                + " FROM " + DatabaseSQLiteOpenHelper.TABLE_PROXY_TAG_LINKS
                + " WHERE " + DatabaseSQLiteOpenHelper.COLUMN_PROXY_ID + " =?";

        Cursor cursor = DBUtils.rawQuery(database, query, new String[]{String.valueOf(proxyId)});
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

    private ProxyEntity cursorToProxy(Cursor cursor)
    {
        if (DUMP_CURSOR_TOSTRING)
        {
            Timber.d("Cursor to StaticProxy entity: %s", DatabaseUtils.dumpCurrentRowToString(cursor));
        }

        ProxyEntity proxy = new ProxyEntity();
        proxy.setId(cursor.getLong(0));
        proxy.setHost(cursor.getString(1));
        proxy.setPort(cursor.getInt(2));
        proxy.setExclusion(cursor.getString(3));
        proxy.setCountryCode(cursor.getString(4));
        proxy.setUsedByCount(cursor.getInt(5));
        proxy.setCreationDate(cursor.getLong(6));
        proxy.setModifiedDate(cursor.getLong(7));

        proxy.setPersisted(true);

        return proxy;
    }

    private PacEntity cursorToPAC(Cursor cursor)
    {
        if (DUMP_CURSOR_TOSTRING)
        {
            Timber.d("Cursor to PAC entity: %s", DatabaseUtils.dumpCurrentRowToString(cursor));
        }

        PacEntity pac = new PacEntity();
        pac.setId(cursor.getLong(0));
        pac.setPacUrlFile(cursor.getString(1));
        pac.setUsedByCount(cursor.getInt(2));
        pac.setCreationDate(cursor.getLong(3));
        pac.setModifiedDate(cursor.getLong(4));

        pac.setPersisted(true);

        return pac;
    }

    private WiFiAPEntity cursorToWifiAP(Cursor cursor)
    {
        if (DUMP_CURSOR_TOSTRING)
        {
            Timber.d("Cursor to WiFiAp columns: '%s'", DBUtils.dumpCursorColumns(cursor));
            Timber.d("Cursor to WiFiAP values: %s", DatabaseUtils.dumpCurrentRowToString(cursor));
        }

        WiFiAPEntity wiFiAPEntity = new WiFiAPEntity();
        wiFiAPEntity.setId(cursor.getLong(0));

        wiFiAPEntity.setSsid(cursor.getString(1));
        wiFiAPEntity.setSecurityType(SecurityType.valueOf(cursor.getString(2)));

        wiFiAPEntity.setProxySetting(ProxySetting.valueOf(cursor.getString(3)));

        if (cursor.isNull(4))
        {
            wiFiAPEntity.setProxyId(-1L);
            wiFiAPEntity.setProxyEntity(null);
        }
        else
        {
            wiFiAPEntity.setProxyId(cursor.getLong(4));
            if (wiFiAPEntity.getProxyId() != -1)
            {
                ProxyEntity proxyEntity = new ProxyEntity();
                proxyEntity.setId(cursor.getLong(8));
                proxyEntity.setHost(cursor.getString(9));
                proxyEntity.setPort(cursor.getInt(10));
                proxyEntity.setExclusion(cursor.getString(11));
                proxyEntity.setCountryCode(cursor.getString(12));
                proxyEntity.setUsedByCount(cursor.getInt(13));
                proxyEntity.setCreationDate(cursor.getLong(14));
                proxyEntity.setModifiedDate(cursor.getLong(15));
                proxyEntity.setPersisted(true);
                wiFiAPEntity.setProxyEntity(proxyEntity);
            }
            else
            {
                wiFiAPEntity.setProxyEntity(null);
            }
        }

        if (cursor.isNull(5))
        {
            wiFiAPEntity.setPACId(-1L);
            wiFiAPEntity.setPacEntity(null);
        }
        else
        {
            wiFiAPEntity.setPACId(cursor.getLong(5));
            if (wiFiAPEntity.getPacId() != -1)
            {
                PacEntity pacEntity = new PacEntity();
                pacEntity.setId(cursor.getLong(16));
                pacEntity.setPacUrlFile(cursor.getString(17));
                pacEntity.setUsedByCount(cursor.getInt(18));
                pacEntity.setCreationDate(cursor.getLong(19));
                pacEntity.setModifiedDate(cursor.getLong(20));
                pacEntity.setPersisted(true);
                wiFiAPEntity.setPacEntity(pacEntity);
            }
            else
            {
                wiFiAPEntity.setPacEntity(null);
            }
        }

        wiFiAPEntity.setCreationDate(cursor.getLong(6));
        wiFiAPEntity.setModifiedDate(cursor.getLong(7));

        wiFiAPEntity.setPersisted(true);

        return wiFiAPEntity;
    }

    private TagEntity cursorToTag(Cursor cursor)
    {
        Timber.d("Cursor to TAG entity: %s", DatabaseUtils.dumpCurrentRowToString(cursor));

        TagEntity tag = new TagEntity();
        tag.setId(cursor.getLong(0));
        tag.setTag(cursor.getString(1));
        tag.setTagColor(cursor.getInt(2));
        tag.setCreationDate(cursor.getLong(3));
        tag.setModifiedDate(cursor.getLong(4));

        tag.setPersisted(true);

        return tag;
    }

    private ProxyTagLinkEntity cursorToProxyTagLink(Cursor cursor)
    {
        Timber.d("Cursor to ProxyTagLink entity: %s", DatabaseUtils.dumpCurrentRowToString(cursor));

        ProxyTagLinkEntity link = new ProxyTagLinkEntity();
        link.setId(cursor.getLong(0));
        link.proxyId = cursor.getLong(1);
        link.tagId = cursor.getLong(2);

        link.setCreationDate(cursor.getLong(3));
        link.setModifiedDate(cursor.getLong(4));

        link.setPersisted(true);

        return link;
    }

    private long getUpdatedRowsFromRawQuery(SQLiteDatabase db)
    {
        Cursor cursor = null;
        long affectedRowCount = -1L;

        try
        {
            cursor = db.rawQuery("SELECT changes() AS affected_row_count", null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
            {
                affectedRowCount = cursor.getLong(cursor.getColumnIndex("affected_row_count"));
                Log.d("LOG", "affectedRowCount = " + affectedRowCount);
            }
            else
            {
                // Some error occurred?
            }
        }
        catch (SQLException e)
        {
            // Handle exception here.
            Timber.e(e,"Exception during getUpdatedRowsFromRawQuery");
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }

        return affectedRowCount;
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
}
